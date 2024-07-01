# powsybl-dynawo

[![Actions Status](https://github.com/powsybl/powsybl-dynawo/workflows/CI/badge.svg)](https://github.com/powsybl/powsybl-dynawo/actions)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.powsybl%3Apowsybl-dynawo&metric=coverage)](https://sonarcloud.io/component_measures?id=com.powsybl%3Apowsybl-dynawo&metric=coverage)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.powsybl%3Apowsybl-dynawo&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.powsybl%3Apowsybl-dynawo)
[![MPL-2.0 License](https://img.shields.io/badge/license-MPL_2.0-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)
[![Javadocs](https://www.javadoc.io/badge/com.powsybl/powsybl-dynawo.svg?color=blue)](https://www.javadoc.io/doc/com.powsybl/powsybl-dynawo)
[![Slack](https://img.shields.io/badge/slack-powsybl-blueviolet.svg?logo=slack)](https://join.slack.com/t/powsybl/shared_invite/zt-rzvbuzjk-nxi0boim1RKPS5PjieI0rA)

[Dynawo](https://dynawo.github.io) is an hybrid C++/Modelica open source suite of simulation tools for power systems. This integration module allows to use [DynaFlow](https://dynawo.github.io/about/dynaflow) for [power flow simulations](https://www.powsybl.org/pages/documentation/simulation/powerflow) and [DynaWaltz](https://dynawo.github.io/about/dynawaltz) for [time domain simulations](https://www.powsybl.org/pages/documentation/simulation/timedomain).

## DynaFlow

To use DynaFlow as your power flow engine, add the `com.powsybl:powsybl-dynaflow` module to your dependencies.

```java
Network network = Network.read("/path/to/the/casefile.xiidm");
LoadFlowParameters parameters = LoadFlowParameters.load();
LoadFlow.find("DynaFlow").run(network, parameters);
```

To learn more about the usage of DynaFlow, read the [dedicated page](https://www.powsybl.org/pages/documentation/simulation/powerflow/dynaflow.html) on our website.

## DynaWaltz

To use DynaWaltz as your time domain engine, add the `com.powsybl:powsybl-dynawaltz` and  `com.powsybl:powsybl-dynawaltz-dsl` modules to your dependencies.
To run a dynamic simulation, you need:
- a case file
- a `DynamicModelsSupplier` to associate dynamic models to the equipment of the network
- an `EventModelsSuppler` to configure events simulated during the simulation (optional)
- a `CurvesSupplier` to follow the evolution of dynamic variables during the simulation (optional)
- a set of parameters to configure the simulation (optional)

Thanks to `powsybl-dynawaltz-dsl`, the inputs can be easily configured using Groovy scripts.
The simulation parameters can be configured either in the `config.yml` file or using a Json file.

```java
Network network = Network.read("/path/to/the/casefile.xiidm");

// Load the dynamic models mapping
GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
    Paths.get("/path/to/dynamicModelsMapping.groovy"),
    GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

// Load the events
GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
    Paths.get("/path/to/event.groovy"),
    GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME));

// Configure the curves
GroovyCurvesSupplier curvesSupplier = new GroovyCurvesSupplier(
    Paths.get("/path/to/curves.groovy"),
    GroovyExtension.find(CurveGroovyExtension.class, DynaWaltzProvider.NAME));

// Load the parameters
DynamicSimulationParameters parameters = DynamicSimulationParameters.load();

// Run the simulation and display the results
DynamicSimulationResult result = DynamicSimulation.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier, parameters);
System.out.println(result.getStatus());
System.out.println("Timeline:");
result.getTimeLine().forEach(tl -> System.out.printf("[%.8f] %s (on %s)%n", tl.time(), tl.message(), tl.modelName()));
```

To learn more about the usage of DynaWaltz, read the [dedicated page](https://www.powsybl.org/pages/documentation/simulation/timedomain/dynawo) on our website.

### Examples

This is an example of a dynamic models mapping file:
```groovy
import com.powsybl.iidm.network.Line
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Generator


for (Load load : network.loads) {
    LoadAlphaBeta {
        staticId load.id
        parameterSetId "LAB"
    }
}

for (Generator gen : network.generators) {
    GeneratorSynchronousThreeWindingsProportionalRegulations {
        staticId gen.id
        parameterSetId "GSTWPR"
    }
}

for (Line line : network.lines) {
    OverloadManagementSystem {
        dynamicModelId "BBM_" + line.id
        parameterSetId "CLA"
        controlledBranch line.id
        iMeasurement line.id
        iMeasurementSide TwoSides.TWO
    }
}
```

Note that this mapping file refers to parameter set ids which should be found in the Dynawo parameters file.
For the above example, the corresponding parameter file could be:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<parametersSet xmlns="http://www.rte-france.com/dynawo">
    <set id="LAB">
        <par type="DOUBLE" name="load_alpha" value="1.5"/>
        <par type="DOUBLE" name="load_beta" value="2.5"/>
        <reference type="DOUBLE" name="load_P0Pu" origData="IIDM" origName="p_pu"/>
        <reference type="DOUBLE" name="load_Q0Pu" origData="IIDM" origName="q_pu"/>
        <reference type="DOUBLE" name="load_U0Pu" origData="IIDM" origName="v_pu"/>
        <reference type="DOUBLE" name="load_UPhase0" origData="IIDM" origName="angle_pu"/>
    </set>
    <set id="CLA">
        <par type="INT" name="currentLimitAutomaton_OrderToEmit" value="1"/>
        <par type="BOOL" name="currentLimitAutomaton_Running" value="true"/>
        <par type="DOUBLE" name="currentLimitAutomaton_IMax" value="600"/>
        <par type="DOUBLE" name="currentLimitAutomaton_tLagBeforeActing" value="5"/>
    </set>
    <set id="GSTWPR">
        <par type="INT" name="generator_ExcitationPu" value="1"/>
        <par type="DOUBLE" name="generator_md" value="0.16"/>
        <par type="DOUBLE" name="generator_mq" value="0.16"/>
        <par type="DOUBLE" name="generator_nd" value="5.7"/>
        <par type="DOUBLE" name="generator_nq" value="5.7"/>
        <par type="DOUBLE" name="generator_MdPuEfd" value="0"/>
        <par type="DOUBLE" name="generator_DPu" value="0"/>
        <par type="DOUBLE" name="generator_H" value="4.97"/>
        <par type="DOUBLE" name="generator_RaPu" value="0.004"/>
        <par type="DOUBLE" name="generator_XlPu" value="0.102"/>
        <par type="DOUBLE" name="generator_XdPu" value="0.75"/>
        <par type="DOUBLE" name="generator_XpdPu" value="0.225"/>
        <par type="DOUBLE" name="generator_XppdPu" value="0.154"/>
        <par type="DOUBLE" name="generator_Tpd0" value="3"/>
        <par type="DOUBLE" name="generator_Tppd0" value="0.04"/>
        <par type="DOUBLE" name="generator_XqPu" value="0.45"/>
        <par type="DOUBLE" name="generator_XppqPu" value="0.2"/>
        <par type="DOUBLE" name="generator_Tppq0" value="0.04"/>
        <par type="DOUBLE" name="generator_UNom" value="15"/>
        <par type="DOUBLE" name="generator_PNomTurb" value="74.4"/>
        <par type="DOUBLE" name="generator_PNomAlt" value="74.4"/>
        <par type="DOUBLE" name="generator_SNom" value="80"/>
        <par type="DOUBLE" name="generator_SnTfo" value="80"/>
        <par type="DOUBLE" name="generator_UNomHV" value="15"/>
        <par type="DOUBLE" name="generator_UNomLV" value="15"/>
        <par type="DOUBLE" name="generator_UBaseHV" value="15"/>
        <par type="DOUBLE" name="generator_UBaseLV" value="15"/>
        <par type="DOUBLE" name="generator_RTfPu" value="0.0"/>
        <par type="DOUBLE" name="generator_XTfPu" value="0.0"/>
        <par type="DOUBLE" name="PmPu_ValueIn" value="0"/>
        <par type="DOUBLE" name="EfdPu_ValueIn" value="0"/>
        <par type="DOUBLE" name="voltageRegulator_LagEfdMax" value="0"/>
        <par type="DOUBLE" name="voltageRegulator_LagEfdMin" value="0"/>
        <par type="DOUBLE" name="voltageRegulator_EfdMinPu" value="-5"/>
        <par type="DOUBLE" name="voltageRegulator_EfdMaxPu" value="5"/>
        <par type="DOUBLE" name="voltageRegulator_UsRefMinPu" value="0.8"/>
        <par type="DOUBLE" name="voltageRegulator_UsRefMaxPu" value="1.2"/>
        <par type="DOUBLE" name="voltageRegulator_Gain" value="20"/>
        <par type="DOUBLE" name="governor_KGover" value="5"/>
        <par type="DOUBLE" name="governor_PMin" value="0"/>
        <par type="DOUBLE" name="governor_PMax" value="74.4"/>
        <par type="DOUBLE" name="governor_PNom" value="74.4"/>
        <par type="DOUBLE" name="URef_ValueIn" value="0"/>
        <par type="DOUBLE" name="Pm_ValueIn" value="0"/>
        <reference name="generator_P0Pu" origData="IIDM" origName="p_pu" type="DOUBLE"/>
        <reference name="generator_Q0Pu" origData="IIDM" origName="q_pu" type="DOUBLE"/>
        <reference name="generator_U0Pu" origData="IIDM" origName="v_pu" type="DOUBLE"/>
        <reference name="generator_UPhase0" origData="IIDM" origName="angle_pu" type="DOUBLE"/>
    </set>
</parametersSet>
```
 

Other examples can be found in the [resources](https://github.com/powsybl/powsybl-dynawo/tree/main/dynawaltz-dsl/src/test/resources) of this project.


## Contribute to documentation

The documentation sources for PowSybl Dynawo features are in the `docs` folder.

Please keep them up to date with your developments.  
They are published on https://powsybl.readthedocs.io and pull requests are built and previewed automatically via github CI.

### Preview results
When modifying the website content, you can easily preview the result by building the docs locally.
To do so, run the following commands:
~~~bash
pip install -r docs/requirements.txt
sphinx-build -a docs ./build-docs
~~~
Then open `build-docs/index.html` in your browser.

### Cross-documentation links
If you want to add links to another documentation, add the corresponding repository to the `conf.py` file.
In order to automatically get the version specified in the `pom.xml`, please use the same naming as the version: if you define the
Groovy version with `<groovy.version>`, then use `groovy` as key. The specified URL should start with `https://` and end with `latest/` (the final `/` is mandatory).
For example, to add a link to the documentation of Sphinx, you need to add the following lines:
~~~python
# This parameter might already be present, just add the new value
intersphinx_mapping = {
    "sphinx": ("https://www.sphinx-doc.org/en/master/", None),
}
~~~

Then in your documentation file, you can add links to Sphinx documentation. If you want to link to a whole page,
use one of the following example:
~~~Markdown
- {doc}`sphinx:usage/extensions/intersphinx`
- {doc}`Intersphinx <sphinx:usage/extensions/intersphinx>`
- [Intersphinx](inv:sphinx:std:doc#usage/extensions/intersphinx).
~~~

If you want to link a specific part of a page, use one of those examples:
~~~Markdown
- [Intersphinx roles](inv:#ref-role).
- [Intersphinx roles](inv:sphinx#ref-role).
- [Intersphinx roles](inv:sphinx:std:label:#ref-role).
- [Intersphinx roles](inv:sphinx:*:*:#ref-role).
~~~
*Note 1: for the last examples to work, there need to be a corresponding reference in the external documentation.
For those examples, `(ref-role)=` has been added right before the corresponding title
in the [Cross-referencing syntax page](inv:sphinx:std:doc#usage/referencing). Another way to make it work is to use the `autosectionlabel` module in Sphinx to
automatically generate anchors for each title.*

*Note 2: if the build fails, try with the `-E` option to clear the cache:*
~~~bash
sphinx-build -a -E docs ./build-docs
~~~