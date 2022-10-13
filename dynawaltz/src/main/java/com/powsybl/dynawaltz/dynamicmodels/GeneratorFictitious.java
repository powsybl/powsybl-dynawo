package com.powsybl.dynawaltz.dynamicmodels;

public class GeneratorFictitious extends AbstractGeneratorModel {

    public GeneratorFictitious(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId,
                "generator_terminal",
                "generator_switchOffSignal1",
                "generator_switchOffSignal2",
                "generator_switchOffSignal3",
                "generator_fictitious");
    }

    @Override
    public String getLib() {
        return "GeneratorFictitious";
    }
}
