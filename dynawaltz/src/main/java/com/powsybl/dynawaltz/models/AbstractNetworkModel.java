package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractNetworkModel implements Model {

    private final String staticId;

    protected AbstractNetworkModel(String staticId) {
        this.staticId = Objects.requireNonNull(staticId);
    }

    @Override
    public Optional<String> getStaticId() {
        return Optional.of(staticId);
    }

    @Override
    public List<Pair<String, String>> getMacroConnectToAttributes() {
        return List.of(
                Pair.of("id2", DynaWaltzXmlConstants.NETWORK),
                Pair.of("name2", staticId)
        );
    }
}
