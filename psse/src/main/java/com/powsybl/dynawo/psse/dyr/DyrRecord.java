package com.powsybl.dynawo.psse.dyr;

import com.powsybl.dynawo.psse.dyr.exciters.*;
import com.powsybl.dynawo.psse.dyr.facts.*;
import com.powsybl.dynawo.psse.dyr.generators.*;
import com.powsybl.dynawo.psse.dyr.governors.*;
import com.powsybl.dynawo.psse.dyr.loads.*;
import com.powsybl.dynawo.psse.dyr.renewables.*;
import com.powsybl.dynawo.psse.dyr.stabilizers.*;

/**
 * Sealed interface representing any parsed PSS/E dyr dynamic model record.
 *
 * <p>Every permitted implementation is a Java {@code record} carrying all
 * parameters exactly as they appear in the raw {@code .dyr} file line.
 * Downstream mapping to the powsybl-dynawo internal model is intentionally
 * left to a separate translation layer.
 *
 * <p>Common header fields available on every model:
 * <ul>
 *   <li>{@code busNumber}  – bus to which the model is attached</li>
 *   <li>{@code modelName} – PSS/E keyword string (e.g. {@code "GENROU"})</li>
 *   <li>{@code machineId} – generator/load/device id (1‑char PSS/E id)</li>
 * </ul>
 *
 * @author powsybl-dynawo contributors
 */
public sealed interface DyrRecord
        permits
        // Generators
        GenclsRecord, GenrouRecord, GentpRecord, GentraRecord,
        // Exciters
        Ieeet1Record, Ieeex1Record, SexsRecord, Esac1aRecord, Esdc1aRecord,
        // Governors
        Tgov1Record, Ieeeg1Record, GastRecord, HygovRecord,
        // Stabilizers
        Stab1Record, Pss2aRecord, Pss2bRecord,
        // Loads
        LmxdRecord, IeelRecord, Cim5Record, Cim6Record,
        // Renewables
        Regca1Record, Reeca1Record, Wtdta1Record,
        // FACTS / HVDC
        CsvgnRecord, VscdctRecord, StatconRecord {

    /** Bus number to which this dynamic model is attached. */
    int busNumber();

    /** PSS/E model keyword identifier (e.g. {@code GENROU}). */
    String modelName();

    /** Generator / device id string as it appears in the dyr file. */
    String machineId();
}
