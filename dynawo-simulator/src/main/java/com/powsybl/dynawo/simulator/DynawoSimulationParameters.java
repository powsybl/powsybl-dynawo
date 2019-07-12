package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

public class DynawoSimulationParameters {

    public DynawoSimulationParameters(Network network, DynawoConfig config) {
        this.network = network;
        this.config = config;
    }

    public void prepareFile() {
        Path parFile = config.getWorkingDir().resolve("dynawoModel.par");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), parameters()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CharSequence parameters() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            "<parametersSet xmlns=\"http://www.ret-france.com/dynawo\">"));
        int id = 1;
        openSet(builder, id);
        globalParameters(builder);
        closeSet(builder);
        for (Load l : network.getLoads()) {
            openSet(builder, id++);
            loadParameters(l, builder);
            closeSet(builder);
        }
        for (Generator g : network.getGenerators()) {
            openSet(builder, id++);
            genParameters(g, builder);
            closeSet(builder);
        }
        openSet(builder, id++);
        omegaRefParameters(builder);
        closeSet(builder);
        openSet(builder, id++);
        eventParameters(builder);
        closeSet(builder);
        builder.append(String.join(System.lineSeparator(),
            "</parametersSet>"));
        return builder.toString();
    }

    private void openSet(StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            "<set id=\"" + id + "\">"));
    }

    private void closeSet(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "</set>"));
    }

    private void omegaRefParameters(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "<par type=\"INT\" name=\"nbGen\" value=\"5\"/>",
            "<par type=\"DOUBLE\" name=\"weight_gen_0\" value=\"1211\"/>",
            "<par type=\"DOUBLE\" name=\"weight_gen_1\" value=\"1120\"/>",
            "<par type=\"DOUBLE\" name=\"weight_gen_2\" value=\"1650\"/>",
            "<par type=\"DOUBLE\" name=\"weight_gen_3\" value=\"80\"/>",
            "<par type=\"DOUBLE\" name=\"weight_gen_4\" value=\"250\"/>"));
    }

    private void globalParameters(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "<par type=\"DOUBLE\" name=\"capacitor_no_reclosing_delay\" value=\"300\"/>",
            "<par type=\"DOUBLE\" name=\"dangling_line_currentLimit_maxTimeOperation\" value=\"90\"/>",
            "<par type=\"DOUBLE\" name=\"line_currentLimit_maxTimeOperation\" value=\"90\"/>",
            "<par type=\"DOUBLE\" name=\"load_Tp\" value=\"90\"/>",
            "<par type=\"DOUBLE\" name=\"load_Tq\" value=\"90\"/>",
            "<par type=\"DOUBLE\" name=\"load_alpha\" value=\"1\"/>",
            "<par type=\"DOUBLE\" name=\"load_alphaLong\" value=\"0\"/>",
            "<par type=\"DOUBLE\" name=\"load_beta\" value=\"2\"/>",
            "<par type=\"DOUBLE\" name=\"load_betaLong\" value=\"0\"/>",
            "<par type=\"BOOL\" name=\"load_isControllable\" value=\"false\"/>",
            "<par type=\"BOOL\" name=\"load_isRestorative\" value=\"false\"/>",
            "<par type=\"DOUBLE\" name=\"load_zPMax\" value=\"100\"/>",
            "<par type=\"DOUBLE\" name=\"load_zQMax\" value=\"100\"/>",
            "<par type=\"DOUBLE\" name=\"reactance_no_reclosing_delay\" value=\"0\"/>",
            "<par type=\"DOUBLE\" name=\"transformer_currentLimit_maxTimeOperation\" value=\"90\"/>",
            "<par type=\"DOUBLE\" name=\"transformer_t1st_HT\" value=\"60\"/>",
            "<par type=\"DOUBLE\" name=\"transformer_t1st_THT\" value=\"30\"/>",
            "<par type=\"DOUBLE\" name=\"transformer_tNext_HT\" value=\"10\"/>",
            "<par type=\"DOUBLE\" name=\"transformer_tNext_THT\" value=\"10\"/>",
            "<par type=\"DOUBLE\" name=\"transformer_tolV\" value=\"0.014999999700000001\"/>"));
    }

    private void eventParameters(StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "<par type=\"DOUBLE\" name=\"event_tEvent\" value=\"1\"/>",
            "<par type=\"BOOL\" name=\"event_disconnectOrigin\" value=\"false\"/>",
            "<par type=\"BOOL\" name=\"event_disconnectExtremity\" value=\"true\"/>"));
    }

    private void loadParameters(Load l, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "<par type=\"DOUBLE\" name=\"load_alpha\" value=\"1.5\"/>",
            "<par type=\"DOUBLE\" name=\"load_beta\" value=\"2.5\"/>",
            "<reference type=\"DOUBLE\" name=\"load_P0PU\" origData=\"IIDM\" origName=\"p_pu\"/>",
            "<reference type=\"DOUBLE\" name=\"load_Q0PU\" origData=\"IIDM\" origName=\"q_pu\"/>",
            "<reference type=\"DOUBLE\" name=\"load_U0PU\" origData=\"IIDM\" origName=\"v_pu\"/>",
            "<reference type=\"DOUBLE\" name=\"load_UPhase\" origData=\"IIDM\" origName=\"angle_pu\"/>"));
    }

    private void genParameters(Generator g, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "<par type=\"INT\" name=\"generator_ExcitationPu\" value=\"1\"/>",
            "<par type=\"DOUBLE\" name=\"generator_DPu\" value=\"0\"/>",
            "<par type=\"DOUBLE\" name=\"generator_H\" value=\"5.4000000000000004\"/>",
            "<par type=\"DOUBLE\" name=\"generator_RaPu\" value=\"0.0027959999999999999\"/>",
            "<par type=\"DOUBLE\" name=\"generator_XlPu\" value=\"0.20200000000000001\"/>",
            "<par type=\"DOUBLE\" name=\"generator_XdPu\" value=\"2.2200000000000002\"/>",
            "<par type=\"DOUBLE\" name=\"generator_XpdPu\" value=\"0.38400000000000001\"/>",
            "<par type=\"DOUBLE\" name=\"generator_XppdPu\" value=\"0.26400000000000001\"/>",
            "<par type=\"DOUBLE\" name=\"generator_Tpd0\" value=\"8.0939999999999994\"/>",
            "<par type=\"DOUBLE\" name=\"generator_Tppd0\" value=\"0.080000000000000002\"/>",
            "<par type=\"DOUBLE\" name=\"generator_XqPu\" value=\"2.2200000000000002\"/>",
            "<par type=\"DOUBLE\" name=\"generator_XpqPu\" value=\"0.39300000000000002\"/>",
            "<par type=\"DOUBLE\" name=\"generator_XppqPu\" value=\"0.26200000000000001\"/>",
            "<par type=\"DOUBLE\" name=\"generator_Tpq0\" value=\"1.5720000000000001\"/>",
            "<par type=\"DOUBLE\" name=\"generator_Tppq0\" value=\"0.084000000000000005\"/>",
            "<par type=\"DOUBLE\" name=\"generator_UNom\" value=\"24\"/>",
            "<par type=\"DOUBLE\" name=\"generator_SNom\" value=\"1211\"/>",
            "<par type=\"DOUBLE\" name=\"generator_PNom\" value=\"1090\"/>",
            "<par type=\"DOUBLE\" name=\"generator_SnTfo\" value=\"1211\"/>",
            "<par type=\"DOUBLE\" name=\"generator_UNomHV\" value=\"69\"/>",
            "<par type=\"DOUBLE\" name=\"generator_UNomLV\" value=\"24\"/>",
            "<par type=\"DOUBLE\" name=\"generator_UBaseHV\" value=\"69\"/>",
            "<par type=\"DOUBLE\" name=\"generator_UBaseLV\" value=\"24\"/>",
            "<par type=\"DOUBLE\" name=\"generator_RTfPu\" value=\"0.0\"/>",
            "<par type=\"DOUBLE\" name=\"generator_XTfPu\" value=\"0.1\"/>",
            "<par type=\"DOUBLE\" name=\"voltageRegulator_LagEfdMax\" value=\"0\"/>",
            "<par type=\"DOUBLE\" name=\"voltageRegulator_LagEfdMin\" value=\"0\"/>",
            "<par type=\"DOUBLE\" name=\"voltageRegulator_EfdMinPu\" value=\"-5\"/>",
            "<par type=\"DOUBLE\" name=\"voltageRegulator_EfdMaxPu\" value=\"5\"/>",
            "<par type=\"DOUBLE\" name=\"voltageRegulator_Gain\" value=\"20\"/>",
            "<par type=\"DOUBLE\" name=\"governor_KGover\" value=\"5\"/>",
            "<par type=\"DOUBLE\" name=\"governor_PMin\" value=\"0\"/>",
            "<par type=\"DOUBLE\" name=\"governor_PMax\" value=\"1090\"/>",
            "<par type=\"DOUBLE\" name=\"governor_PNom\" value=\"1090\"/>",
            "<par type=\"DOUBLE\" name=\"URef_ValueIn\" value=\"0\"/>",
            "<par type=\"DOUBLE\" name=\"Pm_ValueIn\" value=\"0\"/>",
            "<reference name=\"generator_P0Pu\" origData=\"IIDM\" origName=\"p_pu\" type=\"DOUBLE\"/>",
            "<reference name=\"generator_Q0Pu\" origData=\"IIDM\" origName=\"q_pu\" type=\"DOUBLE\"/>",
            "<reference name=\"generator_U0Pu\" origData=\"IIDM\" origName=\"v_pu\" type=\"DOUBLE\"/>",
            "<reference name=\"generator_UPhase0\" origData=\"IIDM\" origName=\"angle_pu\" type=\"DOUBLE\"/>"));
    }

    private final Network network;
    private final DynawoConfig config;
}
