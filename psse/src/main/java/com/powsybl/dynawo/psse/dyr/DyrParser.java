package com.powsybl.dynawo.psse.dyr;

import com.powsybl.dynawo.psse.dyr.exciters.*;
import com.powsybl.dynawo.psse.dyr.facts.*;
import com.powsybl.dynawo.psse.dyr.generators.*;
import com.powsybl.dynawo.psse.dyr.governors.*;
import com.powsybl.dynawo.psse.dyr.loads.*;
import com.powsybl.dynawo.psse.dyr.renewables.*;
import com.powsybl.dynawo.psse.dyr.stabilizers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Parser for PSS/E dynamic model files ({@code .dyr}).
 *
 * <h2>File format</h2>
 * Each record in a {@code .dyr} file follows the form:
 * <pre>
 *   &lt;BUSNUM&gt; '&lt;MODELNAME&gt;' '&lt;ID&gt;' &lt;PARAM1&gt; &lt;PARAM2&gt; ... /
 * </pre>
 * Records can span multiple physical lines; the slash {@code /} is the record terminator.
 * Lines starting with {@code @} are comments and are ignored.
 * The model name and id are enclosed in single quotes.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 *   DyrParser parser = new DyrParser();
 *   List<DyrRecord> records = parser.parse(Path.of("my_system.dyr"));
 *   for (DyrRecord r : records) {
 *       switch (r) {
 *           case GenrouRecord g -> System.out.println("GENROU bus=" + g.busNumber());
 *           case Tgov1Record t -> System.out.println("TGOV1 bus=" + t.busNumber());
 *           default           -> {}
 *       }
 *   }
 * }</pre>
 *
 * <h2>Unknown models</h2>
 * Records whose model name is not recognised are silently skipped. A warning is logged
 * via {@link java.util.logging.Logger} so that gaps in coverage can be tracked.
 *
 * @author powsybl-dynawo contributors
 */
public final class DyrParser {

    private static final Logger LOG = Logger.getLogger(DyrParser.class.getName());

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Parses the given {@code .dyr} file and returns all recognised records.
     *
     * @param path path to the PSS/E dyr file
     * @return immutable list of {@link DyrRecord} instances
     * @throws IOException if the file cannot be read
     */
    public List<DyrRecord> parse(Path path) throws IOException {
        String content = Files.readString(path);
        return parseContent(content);
    }

    /**
     * Parses dyr content supplied as a {@link String}.
     * Useful for testing or when the content has already been read from a stream.
     *
     * @param content raw text of a {@code .dyr} file
     * @return immutable list of {@link DyrRecord} instances
     */
    public List<DyrRecord> parseContent(String content) {
        List<String> rawRecords = splitIntoRawRecords(content);
        List<DyrRecord> result = new ArrayList<>(rawRecords.size());
        for (String raw : rawRecords) {
            DyrRecord record = parseRecord(raw.trim());
            if (record != null) {
                result.add(record);
            }
        }
        return List.copyOf(result);
    }

    // -------------------------------------------------------------------------
    // Tokenisation helpers
    // -------------------------------------------------------------------------

    /**
     * Splits raw file content into individual record strings delimited by {@code /}.
     * Strips comment lines ({@code @...}) and blank lines first.
     */
    private static List<String> splitIntoRawRecords(String content) {
        // Normalise line endings and strip comment lines
        String normalised = Arrays.stream(content.split("\\r?\\n"))
                .filter(line -> !line.isBlank() && !line.stripLeading().startsWith("@"))
                .collect(Collectors.joining(" "));

        // Split on record terminator '/'
        String[] parts = normalised.split("/");
        List<String> records = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                records.add(trimmed);
            }
        }
        return records;
    }

    /**
     * Tokenises a single record string, extracting:
     * <ol>
     *   <li>Bus number (integer)</li>
     *   <li>Model name (single-quoted string, converted to uppercase)</li>
     *   <li>Machine/device id (single-quoted string)</li>
     *   <li>Remaining parameters as double tokens</li>
     * </ol>
     */
    private static TokenisedRecord tokenise(String raw) {
        // Replace single-quoted tokens for uniform splitting
        // Strategy: extract quoted strings first, replace with placeholders
        List<String> quotedTokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        StringBuilder current = new StringBuilder();

        for (char c : raw.toCharArray()) {
            if (c == '\'') {
                if (inQuote) {
                    quotedTokens.add(current.toString().trim());
                    sb.append("__QUOTED__");
                    current = new StringBuilder();
                    inQuote = false;
                } else {
                    inQuote = true;
                }
            } else if (inQuote) {
                current.append(c);
            } else {
                sb.append(c);
            }
        }

        // Now tokenise the rest by whitespace
        String[] parts = sb.toString().trim().split("\\s+");
        // Parts interleaved with quoted tokens:
        // parts[0]       = bus number
        // __QUOTED__     = model name  (quotedTokens[0])
        // __QUOTED__     = machine id  (quotedTokens[1])
        // then numeric parameters

        // Rebuild ordered token list
        List<String> tokens = new ArrayList<>();
        int qi = 0;
        for (String p : parts) {
            if (p.equals("__QUOTED__")) {
                if (qi < quotedTokens.size()) {
                    tokens.add(quotedTokens.get(qi++));
                }
            } else if (!p.isBlank()) {
                tokens.add(p);
            }
        }

        if (tokens.size() < 3) {
            throw new DyrParseException("Record too short to contain bus/model/id: " + raw);
        }

        int busNumber = Integer.parseInt(tokens.get(0).trim());
        String modelName = tokens.get(1).toUpperCase(Locale.ROOT).trim();
        String machineId = tokens.get(2).trim();

        double[] params = tokens.stream()
                .skip(3)
                .mapToDouble(t -> Double.parseDouble(t.replace("d", "e").replace("D", "e")))
                .toArray();

        return new TokenisedRecord(busNumber, modelName, machineId, params);
    }

    // -------------------------------------------------------------------------
    // Record parsing dispatch
    // -------------------------------------------------------------------------

    /**
     * Parses a single raw record string into the appropriate {@link DyrRecord} implementation.
     * Returns {@code null} and logs a warning for unrecognised model names.
     */
    private static DyrRecord parseRecord(String raw) {
        if (raw.isBlank()) {
            return null;
        }
        try {
            TokenisedRecord t = tokenise(raw);
            return switch (t.modelName()) {
                // ── Generators ───────────────────────────────────────────────
                case "GENCLS" -> parseGencls(t);
                case "GENROU" -> parseGenrou(t);
                case "GENTP" -> parseGentp(t);
                case "GENTRA" -> parseGentra(t);

                // ── Exciters ─────────────────────────────────────────────────
                case "IEEET1" -> parseIeeet1(t);
                case "IEEEX1" -> parseIeeex1(t);
                case "SEXS" -> parseSexs(t);
                case "ESAC1A" -> parseEsac1a(t);
                case "ESDC1A" -> parseEsdc1a(t);

                // ── Governors ────────────────────────────────────────────────
                case "TGOV1" -> parseTgov1(t);
                case "IEEEG1" -> parseIeeeg1(t);
                case "GAST" -> parseGast(t);
                case "HYGOV" -> parseHygov(t);

                // ── Stabilisers ──────────────────────────────────────────────
                case "STAB1" -> parseStab1(t);
                case "PSS2A" -> parsePss2a(t);
                case "PSS2B" -> parsePss2b(t);

                // ── Loads ────────────────────────────────────────────────────
                case "LMXD" -> parseLmxd(t);
                case "IEEL" -> parseIeel(t);
                case "CIM5" -> parseCim5(t);
                case "CIM6" -> parseCim6(t);

                // ── Renewables ───────────────────────────────────────────────
                case "REGCA1" -> parseRegca1(t);
                case "REECA1" -> parseReeca1(t);
                case "WTDTA1" -> parseWtdta1(t);

                // ── FACTS / HVDC ─────────────────────────────────────────────
                case "CSVGN" -> parseCsvgn(t);
                case "VSCDCT" -> parseVscdct(t);
                case "STATCON" -> parseStatcon(t);

                default -> {
                    LOG.warning(() -> "Unrecognised PSS/E dyr model: " + t.modelName()
                            + " at bus " + t.busNumber() + " – record skipped.");
                    yield null;
                }
            };
        } catch (Exception e) {
            LOG.warning(() -> "Failed to parse dyr record [" + raw + "]: " + e.getMessage());
            return null;
        }
    }

    // =========================================================================
    // ── Generator parsers ─────────────────────────────────────────────────────
    // =========================================================================

    private static GenclsRecord parseGencls(TokenisedRecord t) {
        double[] p = t.params();
        return new GenclsRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // H
                p[1]   // D
        );
    }

    private static GenrouRecord parseGenrou(TokenisedRecord t) {
        double[] p = t.params();
        return new GenrouRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // T'do
                p[1],  // T''do
                p[2],  // T'qo
                p[3],  // T''qo
                p[4],  // H
                p[5],  // D
                p[6],  // Xd
                p[7],  // Xq
                p[8],  // X'd
                p[9],  // X'q
                p[10], // X''d
                p[11], // Xl
                p[12], // S(1.0)
                p[13]  // S(1.2)
        );
    }

    private static GentpRecord parseGentp(TokenisedRecord t) {
        double[] p = t.params();
        return new GentpRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // T'do
                p[1],  // T''do
                p[2],  // T''qo
                p[3],  // H
                p[4],  // D
                p[5],  // Xd
                p[6],  // Xq
                p[7],  // X'd
                p[8],  // X''d
                p[9],  // X''q
                p[10], // Xl
                p[11], // S(1.0)
                p[12]  // S(1.2)
        );
    }

    private static GentraRecord parseGentra(TokenisedRecord t) {
        double[] p = t.params();
        return new GentraRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // T'do
                p[1],  // T'qo
                p[2],  // H
                p[3],  // D
                p[4],  // Xd
                p[5],  // Xq
                p[6],  // X'd
                p[7],  // X'q
                p[8]   // Xl
        );
    }

    // =========================================================================
    // ── Exciter parsers ───────────────────────────────────────────────────────
    // =========================================================================

    private static Ieeet1Record parseIeeet1(TokenisedRecord t) {
        double[] p = t.params();
        return new Ieeet1Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0],        // TR
                p[1],        // KA
                p[2],        // TA
                p[3],        // TB
                p[4],        // TC
                p[5],        // VRMAX
                p[6],        // VRMIN
                p[7],        // KE
                p[8],        // TE
                p[9],        // KF
                p[10],       // TF1
                (int) p[11], // SWITCH
                p[12],       // E1
                p[13],       // SE1
                p[14],       // E2
                p[15]        // SE2
        );
    }

    private static Ieeex1Record parseIeeex1(TokenisedRecord t) {
        double[] p = t.params();
        return new Ieeex1Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3], p[4],   // TR KA TA TB TC
                p[5], p[6],                       // VRMAX VRMIN
                p[7], p[8],                       // KE TE
                p[9], p[10],                      // KF TF1
                p[11], p[12], p[13], p[14]        // E1 SE1 E2 SE2
        );
    }

    private static SexsRecord parseSexs(TokenisedRecord t) {
        double[] p = t.params();
        return new SexsRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // TATB
                p[1],  // TB
                p[2],  // K
                p[3],  // TE
                p[4],  // EMIN
                p[5]   // EMAX
        );
    }

    private static Esac1aRecord parseEsac1a(TokenisedRecord t) {
        double[] p = t.params();
        return new Esac1aRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // TR
                p[1],  // TB
                p[2],  // TC
                p[3],  // KA
                p[4],  // TA
                p[5],  // VAMAX
                p[6],  // VAMIN
                p[7],  // TE
                p[8],  // KF
                p[9],  // TF
                p[10], // KL
                p[11], // VLMAX
                p[12], // KCS
                p[13], // KD
                p[14], // SEEFD1
                p[15], // SEEFD2
                p[16], // VFE1
                p[17], // VFE2
                p[18], // VRMAX
                p[19]  // VRMIN
        );
    }

    private static Esdc1aRecord parseEsdc1a(TokenisedRecord t) {
        double[] p = t.params();
        return new Esdc1aRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3], p[4],    // TR KA TA TB TC
                p[5], p[6],                        // VRMAX VRMIN
                p[7], p[8],                        // KE TE
                p[9], p[10],                       // KF TF1
                (int) p[11],                       // SWITCH
                p[12], p[13], p[14], p[15]         // E1 SE1 E2 SE2
        );
    }

    // =========================================================================
    // ── Governor parsers ──────────────────────────────────────────────────────
    // =========================================================================

    private static Tgov1Record parseTgov1(TokenisedRecord t) {
        double[] p = t.params();
        return new Tgov1Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // R
                p[1],  // T1
                p[2],  // VMAX
                p[3],  // VMIN
                p[4],  // T2
                p[5],  // T3
                p[6]   // Dt
        );
    }

    private static Ieeeg1Record parseIeeeg1(TokenisedRecord t) {
        double[] p = t.params();
        return new Ieeeg1Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // K
                p[1],  // T1
                p[2],  // T2
                p[3],  // T3
                p[4],  // Uo
                p[5],  // Uc
                p[6],  // PMAX
                p[7],  // PMIN
                p[8],  // T4
                p[9],  // K1
                p[10], // K2
                p[11], // T5
                p[12], // K3
                p[13], // K4
                p[14], // T6
                p[15], // K5
                p[16], // K6
                p[17], // T7
                p[18], // K7
                p[19]  // K8
        );
    }

    private static GastRecord parseGast(TokenisedRecord t) {
        double[] p = t.params();
        return new GastRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3],   // R T1 T2 T3
                p[4], p[5],                // AT KT
                p[6], p[7],                // VMAX VMIN
                p[8]                       // Dturb
        );
    }

    private static HygovRecord parseHygov(TokenisedRecord t) {
        double[] p = t.params();
        return new HygovRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],  // R
                p[1],  // r (temporary droop)
                p[2],  // TR
                p[3],  // TF
                p[4],  // TG
                p[5],  // VELM
                p[6],  // GMAX
                p[7],  // GMIN
                p[8],  // TW
                p[9],  // At
                p[10], // Dturb
                p[11]  // qNL
        );
    }

    // =========================================================================
    // ── Stabiliser parsers ────────────────────────────────────────────────────
    // =========================================================================

    private static Stab1Record parseStab1(TokenisedRecord t) {
        double[] p = t.params();
        return new Stab1Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3], p[4], p[5], p[6]
        );
    }

    private static Pss2aRecord parsePss2a(TokenisedRecord t) {
        double[] p = t.params();
        return new Pss2aRecord(t.busNumber(), t.modelName(), t.machineId(),
                (int) p[0], (int) p[1],    // Input1 Input2
                (int) p[2], (int) p[3],    // M N
                p[4], p[5], p[6], p[7],    // T1 T2 T3 T4
                p[8], p[9], p[10], p[11],  // T5 T6 T7 T8
                p[12], p[13], p[14],       // Ks1 Ks2 Ks3
                p[15], p[16],              // T9 T10
                p[17], p[18],              // LSMAX LSMIN
                p[19], p[20]               // VCU VCL
        );
    }

    private static Pss2bRecord parsePss2b(TokenisedRecord t) {
        double[] p = t.params();
        return new Pss2bRecord(t.busNumber(), t.modelName(), t.machineId(),
                (int) p[0], (int) p[1],    // Input1 Input2
                (int) p[2], (int) p[3],    // M N
                p[4], p[5], p[6], p[7],    // T1..T4
                p[8], p[9], p[10], p[11],  // T5..T8
                p[12], p[13], p[14],       // Ks1 Ks2 Ks3
                p[15], p[16],              // T9 T10
                p[17], p[18], p[19], p[20], // Tw1 Tw2 Tw3 Tw4
                p[21], p[22],              // LSMAX LSMIN
                p[23], p[24]               // VCU VCL
        );
    }

    // =========================================================================
    // ── Load parsers ──────────────────────────────────────────────────────────
    // =========================================================================

    private static LmxdRecord parseLmxd(TokenisedRecord t) {
        double[] p = t.params();
        return new LmxdRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3], p[4], p[5],  // Ra Xs Xr Xm Rr Tr
                p[6], p[7],                            // Hm D
                p[8], p[9], p[10], p[11], p[12],      // Tpo Tppo Ls Lp Lpp
                p[13], p[14]                           // Vt Tv
        );
    }

    private static IeelRecord parseIeel(TokenisedRecord t) {
        double[] p = t.params();
        return new IeelRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3],   // PF1 EX1 PF2 EX2
                p[4], p[5], p[6], p[7]    // QF1 EX3 QF2 EX4
        );
    }

    private static Cim5Record parseCim5(TokenisedRecord t) {
        double[] p = t.params();
        return new Cim5Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3], p[4], p[5], p[6],  // Ra Xa Xm R1 X1 T1o Lss
                p[7], p[8], p[9], p[10], p[11], p[12],       // H Etrip Ttrip Pfrac Tb D
                p[13], p[14], p[15], p[16],                   // Vc1 Vd1 Vc2 Vd2
                p[17], p[18], p[19], p[20], p[21],            // Vbrk Frst Vrst Tst Vst
                p[22], p[23], p[24], p[25]                    // VCA VCB EF LF
        );
    }

    private static Cim6Record parseCim6(TokenisedRecord t) {
        double[] p = t.params();
        return new Cim6Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9], // Ra Xa Xm R1 X1 R2 X2 T1o T2o Lss
                p[10], p[11], p[12], p[13], p[14], p[15],    // H Etrip Ttrip Pfrac Tb D
                p[16], p[17], p[18], p[19],                   // Vc1 Vd1 Vc2 Vd2
                p[20], p[21], p[22], p[23], p[24],            // Vbrk Frst Vrst Tst Vst
                p[25], p[26], p[27], p[28]                    // VCA VCB EF LF
        );
    }

    // =========================================================================
    // ── Renewable parsers ─────────────────────────────────────────────────────
    // =========================================================================

    private static Regca1Record parseRegca1(TokenisedRecord t) {
        double[] p = t.params();
        return new Regca1Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1],             // Tg Rrpwr
                p[2], p[3], p[4],       // Brkpt Zerox Lvpl1
                p[5], p[6], p[7],       // Vo Lv1 Vo1
                p[8], p[9],             // Lv2 Vo2
                p[10], p[11],           // Tfltr Khv
                p[12], p[13],           // Iqrmax Iqrmin
                p[14]                   // Accel
        );
    }

    private static Reeca1Record parseReeca1(TokenisedRecord t) {
        double[] p = t.params();
        return new Reeca1Record(t.busNumber(), t.modelName(), t.machineId(),
                (int) p[0], (int) p[1], (int) p[2],  // PFFLAG VFLAG QFLAG
                p[3], p[4], p[5],                      // Vdip Vup Trv
                p[6], p[7],                            // dbd1 dbd2
                p[8], p[9], p[10], p[11], p[12],      // Kqv Iqh1 Iql1 Vref0 Iqfrz
                p[13], p[14],                          // Thld Thld2
                p[15],                                 // Tp
                p[16], p[17], p[18], p[19],            // QMax QMin VMAX VMIN
                p[20], p[21], p[22], p[23], p[24], p[25], // Kqp Kqi Kvp Kvi Vbias Tiq
                p[26], p[27], p[28], p[29], p[30], p[31], // dPmax dPmin PMAX PMIN Imax Tpord
                // Reactive current table
                p[32], p[33], p[34], p[35], p[36], p[37], p[38], p[39],
                // Active current table
                p[40], p[41], p[42], p[43], p[44], p[45], p[46], p[47]
        );
    }

    private static Wtdta1Record parseWtdta1(TokenisedRecord t) {
        double[] p = t.params();
        return new Wtdta1Record(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3], p[4]
        );
    }

    // =========================================================================
    // ── FACTS / HVDC parsers ──────────────────────────────────────────────────
    // =========================================================================

    private static CsvgnRecord parseCsvgn(TokenisedRecord t) {
        double[] p = t.params();
        return new CsvgnRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0], p[1], p[2], p[3], p[4], p[5], // K T1 T2 T3 T4 T5
                p[6], p[7]                            // CMAX CMIN
        );
    }

    private static VscdctRecord parseVscdct(TokenisedRecord t) {
        double[] p = t.params();
        return new VscdctRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],                          // Tr
                p[1], p[2], p[3], p[4],        // Kp Ki Vdcmax Vdcmin
                p[5], p[6], p[7], p[8],        // Iqmax Iqmin Idmax Idmin
                p[9], p[10],                   // Tp Tq
                p[11], p[12],                  // Kpdc Kidc
                p[13]                          // Cdc
        );
    }

    private static StatconRecord parseStatcon(TokenisedRecord t) {
        double[] p = t.params();
        return new StatconRecord(t.busNumber(), t.modelName(), t.machineId(),
                p[0],                  // Tr
                p[1], p[2],            // Kp Ki
                p[3], p[4],            // Iqmax Iqmin
                p[5], p[6],            // Emax Emin
                p[7], p[8],            // Kf Tf
                p[9], p[10],           // Ks Ts
                p[11], p[12],          // T1 T2
                p[13]                  // Vref
        );
    }

    // =========================================================================
    // ── Internal data carrier ─────────────────────────────────────────────────
    // =========================================================================

    /**
     * Intermediate tokenisation result before type-specific record construction.
     */
    private record TokenisedRecord(
            int busNumber,
            String modelName,
            String machineId,
            double[] params
    ) { }

    // =========================================================================
    // ── Exception ─────────────────────────────────────────────────────────────
    // =========================================================================

    /** Thrown when a dyr record cannot be tokenised or its parameters are incomplete. */
    public static final class DyrParseException extends RuntimeException {
        public DyrParseException(String message) {
            super(message);
        }
    }
}
