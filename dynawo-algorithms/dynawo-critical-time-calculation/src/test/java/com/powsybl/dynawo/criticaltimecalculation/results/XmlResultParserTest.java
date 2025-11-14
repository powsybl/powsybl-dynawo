package com.powsybl.dynawo.criticaltimecalculation.results;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.contingency.results.Status.RESULT_FOUND;
import static com.powsybl.dynawo.contingency.results.Status.CT_ABOVE_MAX_BOUND;
import static com.powsybl.dynawo.contingency.results.Status.CT_BELOW_MIN_BOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlResultParserTest {
    @Test
    void test() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/result.xml")));
        CriticalTimeCalculationResults results = new CriticalTimeCalculationResults(new XmlCriticalTimeCalculationResultsParser().parse(xml));

        assertThat(results.getCriticalTimeCalculationResults()).containsExactly(
                new CriticalTimeCalculationResult("MyFirstScenario", RESULT_FOUND, 1),
                new CriticalTimeCalculationResult("MySecondScenario", CT_BELOW_MIN_BOUND),
                new CriticalTimeCalculationResult("MyThirdScenario", CT_ABOVE_MAX_BOUND));
    }

    @Test
    void parseFromPath() throws URISyntaxException {
        List<CriticalTimeCalculationResult> result = new ArrayList<>();
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/result.xml")).toURI());
        new XmlCriticalTimeCalculationResultsParser().parse(path, result::add);
        assertEquals(3, result.size());
    }

    @Test
    void testInconsistentFile() throws XMLStreamException {
        InputStreamReader xml = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/wrongResult.xml")));
        List<CriticalTimeCalculationResult> result = new XmlCriticalTimeCalculationResultsParser().parse(xml);
        assertEquals(1, result.size());
    }
}
