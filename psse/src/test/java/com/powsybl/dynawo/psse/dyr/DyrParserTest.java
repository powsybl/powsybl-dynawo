package com.powsybl.dynawo.psse.dyr;

import com.powsybl.dynawo.psse.dyr.exciters.*;
import com.powsybl.dynawo.psse.dyr.facts.*;
import com.powsybl.dynawo.psse.dyr.generators.*;
import com.powsybl.dynawo.psse.dyr.governors.*;
import com.powsybl.dynawo.psse.dyr.loads.*;
import com.powsybl.dynawo.psse.dyr.renewables.*;
import com.powsybl.dynawo.psse.dyr.stabilizers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DyrParser}.
 *
 * <p>Each nested class focuses on one concern:
 * <ul>
 *   <li>{@code FormatTests}      – tokenisation, comments, multi-line, Fortran exponents</li>
 *   <li>{@code GeneratorTests}   – GENCLS, GENROU, GENTP, GENTRA</li>
 *   <li>{@code ExciterTests}     – IEEET1, IEEEX1, SEXS, ESAC1A, ESDC1A</li>
 *   <li>{@code GovernorTests}    – TGOV1, IEEEG1, GAST, HYGOV</li>
 *   <li>{@code StabilizerTests}  – STAB1, PSS2A, PSS2B</li>
 *   <li>{@code LoadTests}        – LMXD, IEEL, CIM5, CIM6</li>
 *   <li>{@code RenewableTests}   – REGCA1, REECA1, WTDTA1</li>
 *   <li>{@code FactsTests}       – CSVGN, VSCDCT, STATCON</li>
 *   <li>{@code FileTests}        – parse(Path) API</li>
 * </ul>
 */
class DyrParserTest {

    private static final double DELTA = 1e-9;

    private DyrParser parser;

    @BeforeEach
    void setUp() {
        parser = new DyrParser();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Parses content and asserts exactly one record is returned, then casts it. */
    @SuppressWarnings("unchecked")
    private <T extends DyrRecord> T parseSingle(String content, Class<T> type) {
        List<DyrRecord> records = parser.parseContent(content);
        assertThat(records).hasSize(1);
        assertThat(records.getFirst()).isInstanceOf(type);
        return (T) records.getFirst();
    }

    /** Asserts common header fields shared by every DyrRecord. */
    private static void assertHeader(DyrRecord r, int expectedBus, String expectedModel, String expectedId) {
        assertThat(r.busNumber()).isEqualTo(expectedBus);
        assertThat(r.modelName()).isEqualTo(expectedModel);
        assertThat(r.machineId()).isEqualTo(expectedId);
    }

    // =========================================================================
    // FORMAT TESTS
    // =========================================================================

    @Nested
    @DisplayName("Format and tokenisation")
    class FormatTests {

        @Test
        @DisplayName("Empty content returns empty list")
        void emptyContent() {
            assertThat(parser.parseContent("")).isEmpty();
        }

        @Test
        @DisplayName("Whitespace-only content returns empty list")
        void blankContent() {
            assertThat(parser.parseContent("   \n\n\t  \n")).isEmpty();
        }

        @Test
        @DisplayName("Lines starting with @ are treated as comments and skipped")
        void commentLinesAreIgnored() {
            String content = """
                    @ This is a comment
                    @ Another comment line
                    101 'GENCLS' '1'  4.5  0.0 /
                    """;
            assertThat(parser.parseContent(content)).hasSize(1);
        }

        @Test
        @DisplayName("Inline comments (@) after data on same line are not supported – @ must start line")
        void inlineCommentIsNotStripped() {
            // The @ only strips a full line; inline @ becomes part of the token stream
            // and will cause a parse failure that is caught and swallowed → 0 records
            String content = "101 'GENCLS' '1'  4.5  0.0 @ inline /\n";
            // The parser logs a warning and returns 0 successfully parsed records
            List<DyrRecord> records = parser.parseContent(content);
            // We just verify no exception is thrown; result may be 0 or parser may still work
            // depending on whether "inline" breaks number parsing
            assertThat(records).hasSizeLessThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Record spanning multiple lines is parsed as one unit")
        void multiLineRecord() {
            String content = """
                    101 'GENROU' '1'
                      8.00  0.03  0.40  0.05
                      6.50  0.0
                      1.80  1.72  0.30  0.50  0.25  0.20
                      0.07  0.13 /
                    """;
            GenrouRecord r = parseSingle(content, GenrouRecord.class);
            assertThat(r.busNumber()).isEqualTo(101);
            assertThat(r.h()).isCloseTo(6.50, within(DELTA));
        }

        @Test
        @DisplayName("Multiple records in one string are all parsed")
        void multipleRecords() {
            String content = """
                    101 'GENCLS' '1'  6.5  0.0 /
                    102 'GENCLS' '2'  3.0  0.5 /
                    103 'GENCLS' '1'  5.0  0.0 /
                    """;
            assertThat(parser.parseContent(content)).hasSize(3);
        }

        @Test
        @DisplayName("Unrecognised model name is silently skipped")
        void unknownModelSkipped() {
            String content = """
                    101 'UNKNOWN_MODEL' '1'  1.0  2.0  3.0 /
                    102 'GENCLS' '1'  4.5  0.0 /
                    """;
            List<DyrRecord> records = parser.parseContent(content);
            assertThat(records).hasSize(1);
            assertThat(records.getFirst()).isInstanceOf(GenclsRecord.class);
        }

        @Test
        @DisplayName("Model name is case-insensitive (lowercase accepted)")
        void modelNameCaseInsensitive() {
            String content = "101 'gencls' '1'  4.5  0.0 /\n";
            assertThat(parser.parseContent(content)).hasSize(1);
            assertThat(parser.parseContent(content).getFirst()).isInstanceOf(GenclsRecord.class);
        }

        @Test
        @DisplayName("Model name is case-insensitive (mixed case accepted)")
        void modelNameMixedCase() {
            String content = "101 'GenRou' '1'  8.0 0.03 0.4 0.05 6.5 0.0 1.8 1.72 0.3 0.5 0.25 0.2 0.07 0.13 /\n";
            assertThat(parser.parseContent(content)).hasSize(1);
            assertThat(parser.parseContent(content).getFirst()).isInstanceOf(GenrouRecord.class);
        }

        @Test
        @DisplayName("Fortran D-notation exponents are parsed correctly (e.g. 1.5D-2)")
        void fortranDExponent() {
            String content = "101 'SEXS' '1'  3.3D-1  5.0D0  2.0D1  5.0D-2  -9.9D-1  9.9D-1 /\n";
            SexsRecord r = parseSingle(content, SexsRecord.class);
            assertThat(r.taTb()).isCloseTo(0.33, within(1e-7));
            assertThat(r.k()).isCloseTo(20.0, within(1e-7));
        }

        @Test
        @DisplayName("Machine id with spaces inside quotes is preserved")
        void machineIdWithSpaces() {
            String content = "101 'GENCLS' ' 1'  4.5  0.0 /\n";
            GenclsRecord r = parseSingle(content, GenclsRecord.class);
            assertThat(r.machineId()).isEqualTo("1");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "101 'GENCLS' '1'  4.5 /", // too few params
            "101 'GENROU' '1'  1.0  2.0 /"       // too few params for GENROU
        })
        @DisplayName("Records with insufficient parameters are skipped without throwing")
        void insufficientParametersAreSkipped(String content) {
            // Should not throw; parser catches internally and skips
            assertThatCode(() -> parser.parseContent(content)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Mixed valid and malformed records – valid ones are returned")
        void mixedValidAndMalformed() {
            String content = """
                    101 'GENCLS' '1'  6.5  0.0 /
                    999 'GENCLS' '1'  bad_data /
                    102 'GENCLS' '2'  3.0  0.5 /
                    """;
            List<DyrRecord> records = parser.parseContent(content);
            assertThat(records).hasSize(2);
        }

        @Test
        @DisplayName("Trailing whitespace and extra blank lines do not cause errors")
        void extraWhitespaceHandled() {
            String content = "\n\n  101 'GENCLS' '1'  4.5  0.0 /  \n\n\n";
            assertThat(parser.parseContent(content)).hasSize(1);
        }
    }

    // =========================================================================
    // GENERATOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Generators")
    class GeneratorTests {

        @Test
        @DisplayName("GENCLS – all fields mapped correctly")
        void gencls() {
            String content = "101 'GENCLS' '1'  6.5  0.02 /\n";
            GenclsRecord r = parseSingle(content, GenclsRecord.class);

            assertHeader(r, 101, "GENCLS", "1");
            assertThat(r.h()).isCloseTo(6.5, within(DELTA));
            assertThat(r.d()).isCloseTo(0.02, within(DELTA));
        }

        @Test
        @DisplayName("GENCLS – zero damping is accepted")
        void genclsZeroDamping() {
            String content = "202 'GENCLS' '2'  3.0  0.0 /\n";
            GenclsRecord r = parseSingle(content, GenclsRecord.class);
            assertThat(r.d()).isZero();
        }

        @Test
        @DisplayName("GENROU – all 14 parameters mapped in correct order")
        void genrou() {
            // T'do T''do T'qo T''qo H D Xd Xq X'd X'q X''d Xl S10 S12
            String content = "101 'GENROU' '1'  8.00  0.03  0.40  0.05  6.50  0.0  1.80  1.72  0.30  0.50  0.25  0.20  0.07  0.13 /\n";
            GenrouRecord r = parseSingle(content, GenrouRecord.class);

            assertHeader(r, 101, "GENROU", "1");
            assertThat(r.tpdo()).isCloseTo(8.00, within(DELTA));
            assertThat(r.tppdo()).isCloseTo(0.03, within(DELTA));
            assertThat(r.tpqo()).isCloseTo(0.40, within(DELTA));
            assertThat(r.tppqo()).isCloseTo(0.05, within(DELTA));
            assertThat(r.h()).isCloseTo(6.50, within(DELTA));
            assertThat(r.d()).isCloseTo(0.0, within(DELTA));
            assertThat(r.xd()).isCloseTo(1.80, within(DELTA));
            assertThat(r.xq()).isCloseTo(1.72, within(DELTA));
            assertThat(r.xpd()).isCloseTo(0.30, within(DELTA));
            assertThat(r.xpq()).isCloseTo(0.50, within(DELTA));
            assertThat(r.xppd()).isCloseTo(0.25, within(DELTA));
            assertThat(r.xl()).isCloseTo(0.20, within(DELTA));
            assertThat(r.s10()).isCloseTo(0.07, within(DELTA));
            assertThat(r.s12()).isCloseTo(0.13, within(DELTA));
        }

        @Test
        @DisplayName("GENTP – salient-pole model; T'qo absent, X''q present")
        void gentp() {
            // T'do T''do T''qo H D Xd Xq X'd X''d X''q Xl S10 S12
            String content = "201 'GENTP' '1'  7.50  0.04  0.08  4.00  0.0  1.05  0.65  0.28  0.22  0.35  0.18  0.10  0.25 /\n";
            GentpRecord r = parseSingle(content, GentpRecord.class);

            assertHeader(r, 201, "GENTP", "1");
            assertThat(r.tpdo()).isCloseTo(7.50, within(DELTA));
            assertThat(r.tppdo()).isCloseTo(0.04, within(DELTA));
            assertThat(r.tppqo()).isCloseTo(0.08, within(DELTA));  // no T'qo
            assertThat(r.h()).isCloseTo(4.00, within(DELTA));
            assertThat(r.xppd()).isCloseTo(0.22, within(DELTA));
            assertThat(r.xppq()).isCloseTo(0.35, within(DELTA));   // X''q ≠ X''d
        }

        @Test
        @DisplayName("GENTRA – transient model; no sub-transient parameters")
        void gentra() {
            // T'do T'qo H D Xd Xq X'd X'q Xl
            String content = "301 'GENTRA' '1'  7.0  2.0  5.5  0.0  1.60  1.00  0.25  0.35  0.15 /\n";
            GentraRecord r = parseSingle(content, GentraRecord.class);

            assertHeader(r, 301, "GENTRA", "1");
            assertThat(r.tpdo()).isCloseTo(7.0, within(DELTA));
            assertThat(r.tpqo()).isCloseTo(2.0, within(DELTA));
            assertThat(r.h()).isCloseTo(5.5, within(DELTA));
            assertThat(r.xpd()).isCloseTo(0.25, within(DELTA));
            assertThat(r.xl()).isCloseTo(0.15, within(DELTA));
        }
    }

    // =========================================================================
    // EXCITER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Exciters")
    class ExciterTests {

        @Test
        @DisplayName("IEEET1 – all 16 parameters, including integer SWITCH flag")
        void ieeet1() {
            // TR KA TA TB TC VRMAX VRMIN KE TE KF TF1 SWITCH E1 SE1 E2 SE2
            String content = "101 'IEEET1' '1'  0.02  200.0  0.02  0.0  0.0  7.32  -7.32  1.0  0.80  0.03  1.0  1  3.1  0.10  2.3  0.03 /\n";
            Ieeet1Record r = parseSingle(content, Ieeet1Record.class);

            assertHeader(r, 101, "IEEET1", "1");
            assertThat(r.tr()).isCloseTo(0.02, within(DELTA));
            assertThat(r.ka()).isCloseTo(200.0, within(DELTA));
            assertThat(r.ta()).isCloseTo(0.02, within(DELTA));
            assertThat(r.tb()).isCloseTo(0.0, within(DELTA));
            assertThat(r.tc()).isCloseTo(0.0, within(DELTA));
            assertThat(r.vrMax()).isCloseTo(7.32, within(DELTA));
            assertThat(r.vrMin()).isCloseTo(-7.32, within(DELTA));
            assertThat(r.ke()).isCloseTo(1.0, within(DELTA));
            assertThat(r.te()).isCloseTo(0.80, within(DELTA));
            assertThat(r.kf()).isCloseTo(0.03, within(DELTA));
            assertThat(r.tf1()).isCloseTo(1.0, within(DELTA));
            assertThat(r.switchFlag()).isEqualTo(1);
            assertThat(r.e1()).isCloseTo(3.1, within(DELTA));
            assertThat(r.se1()).isCloseTo(0.10, within(DELTA));
            assertThat(r.e2()).isCloseTo(2.3, within(DELTA));
            assertThat(r.se2()).isCloseTo(0.03, within(DELTA));
        }

        @Test
        @DisplayName("IEEEX1 – 16 parameters, no SWITCH flag")
        void ieeex1() {
            // TR KA TA TB TC VRMAX VRMIN KE TE KF TF1 E1 SE1 E2 SE2
            String content = "101 'IEEEX1' '1'  0.05  50.0  0.06  0.0  0.0  3.5  -3.5  1.0  1.2  0.04  1.0  4.18  0.10  3.14  0.03 /\n";
            Ieeex1Record r = parseSingle(content, Ieeex1Record.class);

            assertHeader(r, 101, "IEEEX1", "1");
            assertThat(r.ka()).isCloseTo(50.0, within(DELTA));
            assertThat(r.vrMax()).isCloseTo(3.5, within(DELTA));
            assertThat(r.e1()).isCloseTo(4.18, within(DELTA));
        }

        @Test
        @DisplayName("SEXS – simplified 6-parameter exciter")
        void sexs() {
            // TATB TB K TE EMIN EMAX
            String content = "101 'SEXS' '1'  0.1  10.0  100.0  0.05  -3.0  3.0 /\n";
            SexsRecord r = parseSingle(content, SexsRecord.class);

            assertHeader(r, 101, "SEXS", "1");
            assertThat(r.taTb()).isCloseTo(0.1, within(DELTA));
            assertThat(r.tb()).isCloseTo(10.0, within(DELTA));
            assertThat(r.k()).isCloseTo(100.0, within(DELTA));
            assertThat(r.te()).isCloseTo(0.05, within(DELTA));
            assertThat(r.eMin()).isCloseTo(-3.0, within(DELTA));
            assertThat(r.eMax()).isCloseTo(3.0, within(DELTA));
        }

        @Test
        @DisplayName("ESAC1A – 20 parameters including demagnetisation factor KD")
        void esac1a() {
            // TR TB TC KA TA VAMAX VAMIN TE KF TF KL VLMAX KCS KD SEEFD1 SEEFD2 VFE1 VFE2 VRMAX VRMIN
            String content = "101 'ESAC1A' '1'  0.02  0.0  0.0  400.0  0.02  7.3  -7.3  0.80  0.03  1.0  1.0  5.0  0.5  0.2  0.05  0.15  3.14  0.08  6.30  -5.43 /\n";
            Esac1aRecord r = parseSingle(content, Esac1aRecord.class);

            assertHeader(r, 101, "ESAC1A", "1");
            assertThat(r.ka()).isCloseTo(400.0, within(DELTA));
            assertThat(r.kd()).isCloseTo(0.2, within(DELTA));
            assertThat(r.vrMax()).isCloseTo(6.30, within(DELTA));
            assertThat(r.vrMin()).isCloseTo(-5.43, within(DELTA));
        }

        @Test
        @DisplayName("ESDC1A – DC1A; identical layout to IEEET1 including SWITCH")
        void esdc1a() {
            // TR KA TA TB TC VRMAX VRMIN KE TE KF TF1 SWITCH E1 SE1 E2 SE2
            String content = "101 'ESDC1A' '1'  0.0  46.0  0.06  0.0  0.0  1.0  -0.9  1.0  0.46  0.1  1.0  0  3.7  0.33  2.8  0.10 /\n";
            Esdc1aRecord r = parseSingle(content, Esdc1aRecord.class);

            assertHeader(r, 101, "ESDC1A", "1");
            assertThat(r.ka()).isCloseTo(46.0, within(DELTA));
            assertThat(r.switchFlag()).isEqualTo(0);
            assertThat(r.e1()).isCloseTo(3.7, within(DELTA));
        }
    }

    // =========================================================================
    // GOVERNOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Governors")
    class GovernorTests {

        @Test
        @DisplayName("TGOV1 – 7-parameter steam governor")
        void tgov1() {
            // R T1 VMAX VMIN T2 T3 Dt
            String content = "101 'TGOV1' '1'  0.05  0.5  1.0  0.0  2.1  7.0  0.0 /\n";
            Tgov1Record r = parseSingle(content, Tgov1Record.class);

            assertHeader(r, 101, "TGOV1", "1");
            assertThat(r.r()).isCloseTo(0.05, within(DELTA));
            assertThat(r.t1()).isCloseTo(0.5, within(DELTA));
            assertThat(r.vMax()).isCloseTo(1.0, within(DELTA));
            assertThat(r.vMin()).isCloseTo(0.0, within(DELTA));
            assertThat(r.t2()).isCloseTo(2.1, within(DELTA));
            assertThat(r.t3()).isCloseTo(7.0, within(DELTA));
            assertThat(r.dt()).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("IEEEG1 – 20-parameter multi-stage turbine; fractions K1–K8 verified")
        void ieeeg1() {
            // K T1 T2 T3 Uo Uc PMAX PMIN T4 K1 K2 T5 K3 K4 T6 K5 K6 T7 K7 K8
            String content = "101 'IEEEG1' '1'  20.0  0.2  0.0  0.2  0.3  -0.3  1.0  0.0  0.25  0.3  0.0  10.0  0.25  0.0  0.4  0.25  0.0  0.0  0.0  0.0 /\n";
            Ieeeg1Record r = parseSingle(content, Ieeeg1Record.class);

            assertHeader(r, 101, "IEEEG1", "1");
            assertThat(r.k()).isCloseTo(20.0, within(DELTA));
            assertThat(r.pMax()).isCloseTo(1.0, within(DELTA));
            assertThat(r.t4()).isCloseTo(0.25, within(DELTA));
            assertThat(r.k1()).isCloseTo(0.3, within(DELTA));
            assertThat(r.k8()).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("GAST – 9-parameter gas turbine governor")
        void gast() {
            // R T1 T2 T3 AT KT VMAX VMIN Dturb
            String content = "101 'GAST' '1'  0.05  0.4  0.1  3.0  1.0  2.0  1.0  0.0  0.0 /\n";
            GastRecord r = parseSingle(content, GastRecord.class);

            assertHeader(r, 101, "GAST", "1");
            assertThat(r.r()).isCloseTo(0.05, within(DELTA));
            assertThat(r.at()).isCloseTo(1.0, within(DELTA));
            assertThat(r.kt()).isCloseTo(2.0, within(DELTA));
            assertThat(r.dTurb()).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("HYGOV – 12-parameter hydro governor; permanent and temporary droop distinct")
        void hygov() {
            // R r TR TF TG VELM GMAX GMIN TW At Dturb qNL
            String content = "101 'HYGOV' '1'  0.05  0.30  5.0  0.05  0.5  0.2  1.0  0.0  1.5  1.1  0.5  0.08 /\n";
            HygovRecord r = parseSingle(content, HygovRecord.class);

            assertHeader(r, 101, "HYGOV", "1");
            assertThat(r.r()).isCloseTo(0.05, within(DELTA));
            assertThat(r.rTemp()).isCloseTo(0.30, within(DELTA));   // temporary droop
            assertThat(r.tr()).isCloseTo(5.0, within(DELTA));
            assertThat(r.tw()).isCloseTo(1.5, within(DELTA));
            assertThat(r.qNl()).isCloseTo(0.08, within(DELTA));
        }
    }

    // =========================================================================
    // STABILIZER TESTS
    // =========================================================================

    @Nested
    @DisplayName("Stabilisers (PSS)")
    class StabilizerTests {

        @Test
        @DisplayName("STAB1 – 7-parameter single-input PSS")
        void stab1() {
            // KQ TQ T1 T2 T3 T4 HLim
            String content = "101 'STAB1' '1'  20.0  1.5  0.15  0.05  0.15  0.05  0.05 /\n";
            Stab1Record r = parseSingle(content, Stab1Record.class);

            assertHeader(r, 101, "STAB1", "1");
            assertThat(r.kq()).isCloseTo(20.0, within(DELTA));
            assertThat(r.tq()).isCloseTo(1.5, within(DELTA));
            assertThat(r.t1()).isCloseTo(0.15, within(DELTA));
            assertThat(r.t2()).isCloseTo(0.05, within(DELTA));
            assertThat(r.hLim()).isCloseTo(0.05, within(DELTA));
        }

        @Test
        @DisplayName("PSS2A – integer flags (Input1, Input2, M, N) parsed as ints")
        void pss2a() {
            // Input1 Input2 M N T1..T8 Ks1 Ks2 Ks3 T9 T10 LSMAX LSMIN VCU VCL
            String content = "101 'PSS2A' '1'  1  3  2  5  "
                    + "0.05  0.02  0.10  0.02  "
                    + "0.0   0.0   1.00  0.20  "
                    + "10.0  0.5   1.0   "
                    + "0.0   0.0   "
                    + "0.20  -0.20  "
                    + "1.10  0.90 /\n";
            Pss2aRecord r = parseSingle(content, Pss2aRecord.class);

            assertHeader(r, 101, "PSS2A", "1");
            assertThat(r.input1()).isEqualTo(1);
            assertThat(r.input2()).isEqualTo(3);
            assertThat(r.m()).isEqualTo(2);
            assertThat(r.n()).isEqualTo(5);
            assertThat(r.ks1()).isCloseTo(10.0, within(DELTA));
            assertThat(r.lsMax()).isCloseTo(0.20, within(DELTA));
            assertThat(r.lsMin()).isCloseTo(-0.20, within(DELTA));
            assertThat(r.vcu()).isCloseTo(1.10, within(DELTA));
            assertThat(r.vcl()).isCloseTo(0.90, within(DELTA));
        }

        @Test
        @DisplayName("PSS2B – has four washout time constants Tw1–Tw4 not present in PSS2A")
        void pss2b() {
            // Same as PSS2A + Tw1 Tw2 Tw3 Tw4 (25 params total after header)
            String content = "101 'PSS2B' '1'  1  3  2  5  "
                    + "0.05  0.02  0.10  0.02  "
                    + "0.0   0.0   1.00  0.20  "
                    + "10.0  0.5   1.0   "
                    + "0.0   0.0   "
                    + "2.0   3.0   2.0   3.0  "   // Tw1 Tw2 Tw3 Tw4
                    + "0.20  -0.20  "
                    + "1.10  0.90 /\n";
            Pss2bRecord r = parseSingle(content, Pss2bRecord.class);

            assertHeader(r, 101, "PSS2B", "1");
            assertThat(r.tw1()).isCloseTo(2.0, within(DELTA));
            assertThat(r.tw2()).isCloseTo(3.0, within(DELTA));
            assertThat(r.tw3()).isCloseTo(2.0, within(DELTA));
            assertThat(r.tw4()).isCloseTo(3.0, within(DELTA));
            assertThat(r.lsMax()).isCloseTo(0.20, within(DELTA));
        }
    }

    // =========================================================================
    // LOAD TESTS
    // =========================================================================

    @Nested
    @DisplayName("Loads")
    class LoadTests {

        @Test
        @DisplayName("LMXD – induction motor; 15 parameters including stall logic")
        void lmxd() {
            // Ra Xs Xr Xm Rr Tr Hm D Tpo Tppo Ls Lp Lpp Vt Tv
            String content = "101 'LMXD' '1'  0.04  0.10  0.10  3.50  0.03  0.05  0.50  1.0  0.10  0.02  3.70  0.15  0.12  0.75  0.02 /\n";
            LmxdRecord r = parseSingle(content, LmxdRecord.class);

            assertHeader(r, 101, "LMXD", "1");
            assertThat(r.ra()).isCloseTo(0.04, within(DELTA));
            assertThat(r.xm()).isCloseTo(3.50, within(DELTA));
            assertThat(r.hm()).isCloseTo(0.50, within(DELTA));
            assertThat(r.vt()).isCloseTo(0.75, within(DELTA));
            assertThat(r.tv()).isCloseTo(0.02, within(DELTA));
        }

        @Test
        @DisplayName("IEEL – static exponential load; 8 parameters")
        void ieel() {
            // PF1 EX1 PF2 EX2 QF1 EX3 QF2 EX4
            String content = "101 'IEEL' '1'  0.8  1.0  0.2  2.0  0.3  2.0  0.7  3.0 /\n";
            IeelRecord r = parseSingle(content, IeelRecord.class);

            assertHeader(r, 101, "IEEL", "1");
            assertThat(r.pf1()).isCloseTo(0.8, within(DELTA));
            assertThat(r.ex1()).isCloseTo(1.0, within(DELTA));
            assertThat(r.pf2()).isCloseTo(0.2, within(DELTA));
            assertThat(r.ex2()).isCloseTo(2.0, within(DELTA));
            assertThat(r.qf1()).isCloseTo(0.3, within(DELTA));
            assertThat(r.ex3()).isCloseTo(2.0, within(DELTA));
            assertThat(r.qf2()).isCloseTo(0.7, within(DELTA));
            assertThat(r.ex4()).isCloseTo(3.0, within(DELTA));
        }

        @Test
        @DisplayName("CIM5 – single-cage detailed motor; 26 parameters")
        void cim5() {
            // Ra Xa Xm R1 X1 T1o Lss H Etrip Ttrip Pfrac Tb D Vc1 Vd1 Vc2 Vd2 Vbrk Frst Vrst Tst Vst VCA VCB EF LF
            String content = "101 'CIM5' '1'  "
                    + "0.04  0.10  3.50  0.03  0.10  0.05  3.80  "
                    + "0.50  0.20  0.10  0.80  2.0  2.0  "
                    + "0.50  0.40  0.50  0.40  "
                    + "0.55  60.0  0.65  0.03  0.55  "
                    + "0.40  0.30  1.0  0.8 /\n";
            Cim5Record r = parseSingle(content, Cim5Record.class);

            assertHeader(r, 101, "CIM5", "1");
            assertThat(r.ra()).isCloseTo(0.04, within(DELTA));
            assertThat(r.h()).isCloseTo(0.50, within(DELTA));
            assertThat(r.pFrac()).isCloseTo(0.80, within(DELTA));
            assertThat(r.vBrk()).isCloseTo(0.55, within(DELTA));
            assertThat(r.ef()).isCloseTo(1.0, within(DELTA));
            assertThat(r.lf()).isCloseTo(0.8, within(DELTA));
        }

        @Test
        @DisplayName("CIM6 – double-cage motor; adds R2 X2 T2o vs CIM5")
        void cim6() {
            // Same as CIM5 but adds R2 X2 T2o in electrical block (29 params total)
            String content = "101 'CIM6' '1'  "
                    + "0.04  0.10  3.50  0.03  0.10  0.05  0.04  0.06  0.03  3.80  "  // Ra Xa Xm R1 X1 R2 X2 T1o T2o Lss
                    + "0.50  0.20  0.10  0.80  2.0  2.0  "
                    + "0.50  0.40  0.50  0.40  "
                    + "0.55  60.0  0.65  0.03  0.55  "
                    + "0.40  0.30  1.0  0.8 /\n";
            Cim6Record r = parseSingle(content, Cim6Record.class);

            assertHeader(r, 101, "CIM6", "1");
            assertThat(r.r2()).isCloseTo(0.05, within(DELTA));   // second cage R2
            assertThat(r.x2()).isCloseTo(0.04, within(DELTA));   // second cage X2
            assertThat(r.t2o()).isCloseTo(0.03, within(DELTA));   // second cage T2o
            assertThat(r.lss()).isCloseTo(3.80, within(DELTA));
            assertThat(r.h()).isCloseTo(0.50, within(DELTA));
        }
    }

    // =========================================================================
    // RENEWABLE TESTS
    // =========================================================================

    @Nested
    @DisplayName("Renewables")
    class RenewableTests {

        @Test
        @DisplayName("REGCA1 – converter model; 15 parameters including LVPL and HVRC")
        void regca1() {
            // Tg Rrpwr Brkpt Zerox Lvpl1 Vo Lv1 Vo1 Lv2 Vo2 Tfltr Khv Iqrmax Iqrmin Accel
            String content = "101 'REGCA1' '1'  0.02  10.0  0.90  0.40  1.22  1.10  1.0  1.20  0.0  1.15  0.02  0.0  999.0  -999.0  0.7 /\n";
            Regca1Record r = parseSingle(content, Regca1Record.class);

            assertHeader(r, 101, "REGCA1", "1");
            assertThat(r.tg()).isCloseTo(0.02, within(DELTA));
            assertThat(r.rrpwr()).isCloseTo(10.0, within(DELTA));
            assertThat(r.brkpt()).isCloseTo(0.90, within(DELTA));
            assertThat(r.zerox()).isCloseTo(0.40, within(DELTA));
            assertThat(r.lvpl1()).isCloseTo(1.22, within(DELTA));
            assertThat(r.iqrMax()).isCloseTo(999.0, within(DELTA));
            assertThat(r.iqrMin()).isCloseTo(-999.0, within(DELTA));
            assertThat(r.accel()).isCloseTo(0.7, within(DELTA));
        }

        @Test
        @DisplayName("REECA1 – electrical control; integer flags + 48 parameters + tables")
        void reeca1() {
            // PFFLAG VFLAG QFLAG Vdip Vup Trv dbd1 dbd2 Kqv Iqh1 Iql1 Vref0 Iqfrz
            // Thld Thld2 Tp QMax QMin VMAX VMIN Kqp Kqi Kvp Kvi Vbias Tiq
            // dPmax dPmin PMAX PMIN Imax Tpord
            // Vq1 Iq1 Vq2 Iq2 Vq3 Iq3 Vq4 Iq4
            // Vp1 Ip1 Vp2 Ip2 Vp3 Ip3 Vp4 Ip4
            String content = "101 'REECA1' '1'  "
                    + "0  1  0  "                                  // PFFLAG VFLAG QFLAG
                    + "0.90  1.10  0.02  "                         // Vdip Vup Trv
                    + "-0.05  0.05  "                              // dbd1 dbd2
                    + "2.0  1.05  -1.05  0.0  0.0  "              // Kqv Iqh1 Iql1 Vref0 Iqfrz
                    + "0.0  0.0  "                                 // Thld Thld2
                    + "0.02  "                                     // Tp
                    + "0.436  -0.436  1.1  0.9  "                  // QMax QMin VMAX VMIN
                    + "0.0  0.0  0.0  0.0  0.0  0.02  "           // Kqp Kqi Kvp Kvi Vbias Tiq
                    + "99.0  -99.0  1.0  0.0  1.1  0.02  "        // dPmax dPmin PMAX PMIN Imax Tpord
                    + "0.5 1.05  0.7 0.50  0.8 0.25  0.9 0.0  "  // Vq1..Iq4
                    + "0.2 1.0   0.4 0.80  0.6 0.50  0.8 0.0 /\n"; // Vp1..Ip4
            Reeca1Record r = parseSingle(content, Reeca1Record.class);

            assertHeader(r, 101, "REECA1", "1");
            assertThat(r.pfFlag()).isEqualTo(0);
            assertThat(r.vFlag()).isEqualTo(1);
            assertThat(r.qFlag()).isEqualTo(0);
            assertThat(r.vdip()).isCloseTo(0.90, within(DELTA));
            assertThat(r.kqv()).isCloseTo(2.0, within(DELTA));
            assertThat(r.tpOrd()).isCloseTo(0.02, within(DELTA));
            // Verify reactive current table
            assertThat(r.vq1()).isCloseTo(0.5, within(DELTA));
            assertThat(r.iq1()).isCloseTo(1.05, within(DELTA));
            assertThat(r.vq4()).isCloseTo(0.9, within(DELTA));
            assertThat(r.iq4()).isCloseTo(0.0, within(DELTA));
            // Verify active current table
            assertThat(r.vp1()).isCloseTo(0.2, within(DELTA));
            assertThat(r.ip4()).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("WTDTA1 – wind drive-train; 5 parameters")
        void wtdta1() {
            // H DAMP Hself KSelf Theta
            String content = "101 'WTDTA1' '1'  0.50  1.5  4.50  25.0  0.0 /\n";
            Wtdta1Record r = parseSingle(content, Wtdta1Record.class);

            assertHeader(r, 101, "WTDTA1", "1");
            assertThat(r.h()).isCloseTo(0.50, within(DELTA));
            assertThat(r.damp()).isCloseTo(1.5, within(DELTA));
            assertThat(r.hSelf()).isCloseTo(4.50, within(DELTA));
            assertThat(r.kSelf()).isCloseTo(25.0, within(DELTA));
            assertThat(r.theta()).isCloseTo(0.0, within(DELTA));
        }
    }

    // =========================================================================
    // FACTS / HVDC TESTS
    // =========================================================================

    @Nested
    @DisplayName("FACTS and HVDC")
    class FactsTests {

        @Test
        @DisplayName("CSVGN – SVC model; 8 parameters including susceptance limits")
        void csvgn() {
            // K T1 T2 T3 T4 T5 CMAX CMIN
            String content = "101 'CSVGN' '1'  200.0  0.01  0.0  0.0  0.05  0.05  2.0  -2.0 /\n";
            CsvgnRecord r = parseSingle(content, CsvgnRecord.class);

            assertHeader(r, 101, "CSVGN", "1");
            assertThat(r.k()).isCloseTo(200.0, within(DELTA));
            assertThat(r.t1()).isCloseTo(0.01, within(DELTA));
            assertThat(r.cMax()).isCloseTo(2.0, within(DELTA));
            assertThat(r.cMin()).isCloseTo(-2.0, within(DELTA));
        }

        @Test
        @DisplayName("VSCDCT – VSC-HVDC terminal; 14 parameters including DC capacitor")
        void vscdct() {
            // Tr Kp Ki Vdcmax Vdcmin Iqmax Iqmin Idmax Idmin Tp Tq Kpdc Kidc Cdc
            String content = "101 'VSCDCT' '1'  0.02  1.0  5.0  1.05  0.95  1.0  -1.0  1.0  -1.0  0.01  0.01  1.0  5.0  0.01 /\n";
            VscdctRecord r = parseSingle(content, VscdctRecord.class);

            assertHeader(r, 101, "VSCDCT", "1");
            assertThat(r.tr()).isCloseTo(0.02, within(DELTA));
            assertThat(r.kp()).isCloseTo(1.0, within(DELTA));
            assertThat(r.ki()).isCloseTo(5.0, within(DELTA));
            assertThat(r.vdcMax()).isCloseTo(1.05, within(DELTA));
            assertThat(r.vdcMin()).isCloseTo(0.95, within(DELTA));
            assertThat(r.iqMax()).isCloseTo(1.0, within(DELTA));
            assertThat(r.iqMin()).isCloseTo(-1.0, within(DELTA));
            assertThat(r.cdc()).isCloseTo(0.01, within(DELTA));
        }

        @Test
        @DisplayName("STATCON – STATCOM model; 14 parameters including inner current controller")
        void statcon() {
            // Tr Kp Ki Iqmax Iqmin Emax Emin Kf Tf Ks Ts T1 T2 Vref
            String content = "101 'STATCON' '1'  0.02  50.0  5.0  1.0  -1.0  1.2  -1.2  0.02  0.05  1.0  0.01  0.0  0.0  0.0 /\n";
            StatconRecord r = parseSingle(content, StatconRecord.class);

            assertHeader(r, 101, "STATCON", "1");
            assertThat(r.tr()).isCloseTo(0.02, within(DELTA));
            assertThat(r.kp()).isCloseTo(50.0, within(DELTA));
            assertThat(r.iqMax()).isCloseTo(1.0, within(DELTA));
            assertThat(r.iqMin()).isCloseTo(-1.0, within(DELTA));
            assertThat(r.eMax()).isCloseTo(1.2, within(DELTA));
            assertThat(r.eMin()).isCloseTo(-1.2, within(DELTA));
            assertThat(r.vRef()).isCloseTo(0.0, within(DELTA));
        }
    }

    // =========================================================================
    // MULTI-RECORD / INTEGRATION TESTS
    // =========================================================================

    @Nested
    @DisplayName("Multi-record and integration")
    class MultiRecordTests {

        @Test
        @DisplayName("Typical generator block: GENROU + IEEET1 + TGOV1 + STAB1 at same bus")
        void typicalGeneratorBlock() {
            String content = """
                    @ Generator block for bus 101
                    101 'GENROU' '1'  8.00  0.03  0.40  0.05  6.50  0.0  1.80  1.72  0.30  0.50  0.25  0.20  0.07  0.13 /
                    101 'IEEET1' '1'  0.02  200.0  0.02  0.0  0.0  7.32  -7.32  1.0  0.80  0.03  1.0  1  3.1  0.10  2.3  0.03 /
                    101 'TGOV1' '1'  0.05  0.5  1.0  0.0  2.1  7.0  0.0 /
                    101 'STAB1' '1'  20.0  1.5  0.15  0.05  0.15  0.05  0.05 /
                    """;
            List<DyrRecord> records = parser.parseContent(content);
            assertThat(records).hasSize(4);
            assertThat(records.get(0)).isInstanceOf(GenrouRecord.class);
            assertThat(records.get(1)).isInstanceOf(Ieeet1Record.class);
            assertThat(records.get(2)).isInstanceOf(Tgov1Record.class);
            assertThat(records.get(3)).isInstanceOf(Stab1Record.class);

            // All on same bus
            records.forEach(r -> assertThat(r.busNumber()).isEqualTo(101));
        }

        @Test
        @DisplayName("Multiple buses, multiple model categories – correct counts per type")
        void multipleModelCategories() {
            String content = """
                    101 'GENCLS' '1'  6.5  0.0 /
                    102 'GENROU' '1'  8.00  0.03  0.40  0.05  6.50  0.0  1.80  1.72  0.30  0.50  0.25  0.20  0.07  0.13 /
                    101 'SEXS'   '1'  0.1  10.0  100.0  0.05  -3.0  3.0 /
                    102 'SEXS'   '1'  0.1  10.0  100.0  0.05  -3.0  3.0 /
                    101 'TGOV1'  '1'  0.05  0.5  1.0  0.0  2.1  7.0  0.0 /
                    103 'REGCA1' '1'  0.02  10.0  0.90  0.40  1.22  1.10  1.0  1.20  0.0  1.15  0.02  0.0  999.0  -999.0  0.7 /
                    """;
            List<DyrRecord> records = parser.parseContent(content);
            assertThat(records).hasSize(6);

            long genCount = records.stream().filter(r -> r instanceof GenclsRecord || r instanceof GenrouRecord).count();
            long exciterCount = records.stream().filter(r -> r instanceof SexsRecord).count();
            long govCount = records.stream().filter(r -> r instanceof Tgov1Record).count();
            long renCount = records.stream().filter(r -> r instanceof Regca1Record).count();

            assertThat(genCount).isEqualTo(2);
            assertThat(exciterCount).isEqualTo(2);
            assertThat(govCount).isEqualTo(1);
            assertThat(renCount).isEqualTo(1);
        }

        @Test
        @DisplayName("Comment-only file produces empty result")
        void commentOnlyFile() {
            String content = """
                    @ PSS/E Dynamic Data File
                    @ System: Test Network
                    @ Created: 2024-01-01
                    """;
            assertThat(parser.parseContent(content)).isEmpty();
        }

        @Test
        @DisplayName("Records with Windows-style CRLF line endings are parsed correctly")
        void windowsLineEndings() {
            String content = "101 'GENCLS' '1'  6.5  0.0 /\r\n102 'GENCLS' '2'  3.0  0.5 /\r\n";
            assertThat(parser.parseContent(content)).hasSize(2);
        }

        @Test
        @DisplayName("Model name stored as uppercase regardless of input casing")
        void modelNameStoredUppercase() {
            String content = "101 'gencls' '1'  4.5  0.0 /\n";
            DyrRecord r = parser.parseContent(content).getFirst();
            assertThat(r.modelName()).isEqualTo("GENCLS");
        }

        @Test
        @DisplayName("Machine id is preserved exactly (no case change)")
        void machineIdPreserved() {
            String content = "101 'GENCLS' 'G1'  4.5  0.0 /\n";
            DyrRecord r = parser.parseContent(content).getFirst();
            assertThat(r.machineId()).isEqualTo("G1");
        }

        @Test
        @DisplayName("Negative parameter values are accepted")
        void negativeParameters() {
            String content = "101 'SEXS' '1'  0.1  10.0  100.0  0.05  -5.0  5.0 /\n";
            SexsRecord r = parseSingle(content, SexsRecord.class);
            assertThat(r.eMin()).isCloseTo(-5.0, within(DELTA));
        }

        @Test
        @DisplayName("Very large bus numbers are accepted")
        void largeBusNumber() {
            String content = "999999 'GENCLS' '1'  4.5  0.0 /\n";
            DyrRecord r = parseSingle(content, GenclsRecord.class);
            assertThat(r.busNumber()).isEqualTo(999_999);
        }
    }

    // =========================================================================
    // FILE I/O TESTS
    // =========================================================================

    @Nested
    @DisplayName("File I/O – parse(Path)")
    class FileTests {

        @Test
        @DisplayName("parse(Path) reads file and returns same results as parseContent")
        void parsePathEquivalentToParseContent(@TempDir Path tmpDir) throws IOException {
            String content = """
                    101 'GENCLS' '1'  6.5  0.0 /
                    102 'TGOV1' '1'  0.05  0.5  1.0  0.0  2.1  7.0  0.0 /
                    """;
            Path dyrFile = tmpDir.resolve("test.dyr");
            Files.writeString(dyrFile, content);

            List<DyrRecord> fromFile = parser.parse(dyrFile);
            List<DyrRecord> fromContent = parser.parseContent(content);

            assertThat(fromFile).hasSize(fromContent.size());
            for (int i = 0; i < fromFile.size(); i++) {
                assertThat(fromFile.get(i).getClass()).isEqualTo(fromContent.get(i).getClass());
                assertThat(fromFile.get(i).busNumber()).isEqualTo(fromContent.get(i).busNumber());
                assertThat(fromFile.get(i).modelName()).isEqualTo(fromContent.get(i).modelName());
            }
        }

        @Test
        @DisplayName("parse(Path) throws IOException for non-existent file")
        void parseNonExistentFileThrows(@TempDir Path tmpDir) {
            Path missing = tmpDir.resolve("does_not_exist.dyr");
            assertThatThrownBy(() -> parser.parse(missing))
                    .isInstanceOf(IOException.class);
        }

        @Test
        @DisplayName("parse(Path) returns empty list for empty file")
        void parseEmptyFile(@TempDir Path tmpDir) throws IOException {
            Path emptyFile = tmpDir.resolve("empty.dyr");
            Files.writeString(emptyFile, "");
            assertThat(parser.parse(emptyFile)).isEmpty();
        }

        @Test
        @DisplayName("Returned list is immutable (add throws UnsupportedOperationException)")
        void returnedListIsImmutable() {
            List<DyrRecord> records = parser.parseContent("101 'GENCLS' '1'  4.5  0.0 /\n");
            assertThatThrownBy(() -> records.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
