/**
 * PSS/E dyr dynamic model parser for powsybl-dynawo.
 *
 * <p>Declares a named JPMS module so that the sealed {@code DyrRecord} interface
 * can permit implementations spread across multiple sub-packages.
 *
 * <p>In Java, a sealed interface may only permit classes/interfaces that are
 * either (a) in the same package, or (b) in the same <em>named</em> module.
 * Without this file the project runs in the unnamed module, where cross-package
 * {@code permits} clauses are rejected by the compiler.
 */
module com.powsybl.dynawo.psse.dyr {
    requires java.logging;

    // ── Public API ────────────────────────────────────────────────────────────
    exports com.powsybl.dynawo.psse.dyr;
    exports com.powsybl.dynawo.psse.dyr.generators;
    exports com.powsybl.dynawo.psse.dyr.exciters;
    exports com.powsybl.dynawo.psse.dyr.governors;
    exports com.powsybl.dynawo.psse.dyr.stabilizers;
    exports com.powsybl.dynawo.psse.dyr.loads;
    exports com.powsybl.dynawo.psse.dyr.renewables;
    exports com.powsybl.dynawo.psse.dyr.facts;

    // ── Test framework access (open for reflection-based test runners) ─────────
    opens com.powsybl.dynawo.psse.dyr            to org.junit.platform.commons;
    opens com.powsybl.dynawo.psse.dyr.generators to org.junit.platform.commons;
    opens com.powsybl.dynawo.psse.dyr.exciters   to org.junit.platform.commons;
    opens com.powsybl.dynawo.psse.dyr.governors  to org.junit.platform.commons;
    opens com.powsybl.dynawo.psse.dyr.stabilizers to org.junit.platform.commons;
    opens com.powsybl.dynawo.psse.dyr.loads      to org.junit.platform.commons;
    opens com.powsybl.dynawo.psse.dyr.renewables to org.junit.platform.commons;
    opens com.powsybl.dynawo.psse.dyr.facts      to org.junit.platform.commons;
}
