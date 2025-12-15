/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.generator;

import com.powsybl.dynawo.extensions.api.generator.RpclType;
import com.powsybl.dynawo.extensions.api.generator.SynchronousGeneratorProperties;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.impl.AbstractMultiVariantIdentifiableExtension;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public class SynchronousGeneratorPropertiesImpl
        extends AbstractMultiVariantIdentifiableExtension<Generator>
        implements SynchronousGeneratorProperties {

    private final ArrayList<Windings> numberOfWindingsByVariant;
    private final ArrayList<String> governorByVariant;
    private final ArrayList<String> voltageRegulatorByVariant;
    private final ArrayList<String> pssByVariant;
    private final ArrayList<Boolean> auxiliariesByVariant;
    private final ArrayList<Boolean> internalTransformerByVariant;
    private final ArrayList<RpclType> rpclByVariant;
    private final ArrayList<Uva> uvaByVariant;
    private final ArrayList<Boolean> aggregatedByVariant;
    private final ArrayList<Boolean> qlimByVariant;

    public SynchronousGeneratorPropertiesImpl(Generator generator,
                                              Windings numberOfWindings,
                                              String governor,
                                              String voltageRegulator,
                                              String pss,
                                              boolean auxiliaries,
                                              boolean internalTransformer,
                                              RpclType rpcl,
                                              SynchronousGeneratorProperties.Uva uva,
                                              boolean aggregated,
                                              boolean qlim) {
        super(generator);
        int variantArraySize = getVariantManagerHolder().getVariantManager().getVariantArraySize();

        this.numberOfWindingsByVariant = new ArrayList<>(variantArraySize);
        this.governorByVariant = new ArrayList<>(variantArraySize);
        this.voltageRegulatorByVariant = new ArrayList<>(variantArraySize);
        this.pssByVariant = new ArrayList<>(variantArraySize);
        this.auxiliariesByVariant = new ArrayList<>(variantArraySize);
        this.internalTransformerByVariant = new ArrayList<>(variantArraySize);
        this.rpclByVariant = new ArrayList<>(variantArraySize);
        this.uvaByVariant = new ArrayList<>(variantArraySize);
        this.aggregatedByVariant = new ArrayList<>(variantArraySize);
        this.qlimByVariant = new ArrayList<>(variantArraySize);

        for (int i = 0; i < variantArraySize; i++) {
            this.numberOfWindingsByVariant.add(numberOfWindings);
            this.governorByVariant.add(governor);
            this.voltageRegulatorByVariant.add(voltageRegulator);
            this.pssByVariant.add(pss);
            this.auxiliariesByVariant.add(auxiliaries);
            this.internalTransformerByVariant.add(internalTransformer);
            this.rpclByVariant.add(rpcl);
            this.uvaByVariant.add(uva);
            this.aggregatedByVariant.add(aggregated);
            this.qlimByVariant.add(qlim);
        }
    }

    @Override
    public Windings getNumberOfWindings() {
        return numberOfWindingsByVariant.get(getVariantIndex());
    }

    @Override
    public void setNumberOfWindings(Windings numberOfWindings) {
        numberOfWindingsByVariant.set(getVariantIndex(), Objects.requireNonNull(numberOfWindings, "numberOfWindings"));
    }

    @Override
    public String getGovernor() {
        return governorByVariant.get(getVariantIndex());
    }

    @Override
    public void setGovernor(String governor) {
        governorByVariant.set(getVariantIndex(), governor);
    }

    @Override
    public String getVoltageRegulator() {
        return voltageRegulatorByVariant.get(getVariantIndex());
    }

    @Override
    public void setVoltageRegulator(String voltageRegulator) {
        voltageRegulatorByVariant.set(getVariantIndex(), voltageRegulator);
    }

    @Override
    public String getPss() {
        return pssByVariant.get(getVariantIndex());
    }

    @Override
    public void setPss(String pss) {
        pssByVariant.set(getVariantIndex(), pss);
    }

    @Override
    public boolean isAuxiliaries() {
        return auxiliariesByVariant.get(getVariantIndex());
    }

    @Override
    public void setAuxiliaries(boolean auxiliaries) {
        auxiliariesByVariant.set(getVariantIndex(), auxiliaries);
    }

    @Override
    public boolean isInternalTransformer() {
        return internalTransformerByVariant.get(getVariantIndex());
    }

    @Override
    public void setInternalTransformer(boolean internalTransformer) {
        internalTransformerByVariant.set(getVariantIndex(), internalTransformer);
    }

    @Override
    public boolean isRpcl1() {
        RpclType r = rpclByVariant.get(getVariantIndex());
        return r.isRpcl1();
    }

    @Override
    public boolean isRpcl2() {
        RpclType r = rpclByVariant.get(getVariantIndex());
        return r.isRpcl2();
    }

    @Override
    public void setRpcl(RpclType rpcl) {
        rpclByVariant.set(getVariantIndex(), rpcl);
    }

    @Override
    public RpclType getRpcl() {
        return rpclByVariant.get(getVariantIndex());
    }

    @Override
    public Uva getUva() {
        return uvaByVariant.get(getVariantIndex());
    }

    @Override
    public void setUva(Uva uva) {
        uvaByVariant.set(getVariantIndex(), uva);
    }

    @Override
    public boolean isAggregated() {
        return aggregatedByVariant.get(getVariantIndex());
    }

    @Override
    public void setAggregated(boolean aggregated) {
        aggregatedByVariant.set(getVariantIndex(), aggregated);
    }

    @Override
    public boolean isQlim() {
        return qlimByVariant.get(getVariantIndex());
    }

    @Override
    public void setQlim(boolean qlim) {
        qlimByVariant.set(getVariantIndex(), qlim);
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        numberOfWindingsByVariant.ensureCapacity(numberOfWindingsByVariant.size() + number);
        governorByVariant.ensureCapacity(governorByVariant.size() + number);
        voltageRegulatorByVariant.ensureCapacity(voltageRegulatorByVariant.size() + number);
        pssByVariant.ensureCapacity(pssByVariant.size() + number);
        auxiliariesByVariant.ensureCapacity(auxiliariesByVariant.size() + number);
        internalTransformerByVariant.ensureCapacity(internalTransformerByVariant.size() + number);
        rpclByVariant.ensureCapacity(rpclByVariant.size() + number);
        uvaByVariant.ensureCapacity(uvaByVariant.size() + number);
        aggregatedByVariant.ensureCapacity(aggregatedByVariant.size() + number);
        qlimByVariant.ensureCapacity(qlimByVariant.size() + number);

        Windings w = numberOfWindingsByVariant.get(sourceIndex);
        String gov = governorByVariant.get(sourceIndex);
        String vr = voltageRegulatorByVariant.get(sourceIndex);
        String p = pssByVariant.get(sourceIndex);
        Boolean aux = auxiliariesByVariant.get(sourceIndex);
        Boolean it = internalTransformerByVariant.get(sourceIndex);
        RpclType r = rpclByVariant.get(sourceIndex);
        Uva u = uvaByVariant.get(sourceIndex);
        Boolean agg = aggregatedByVariant.get(sourceIndex);
        Boolean ql = qlimByVariant.get(sourceIndex);

        for (int i = 0; i < number; i++) {
            numberOfWindingsByVariant.add(w);
            governorByVariant.add(gov);
            voltageRegulatorByVariant.add(vr);
            pssByVariant.add(p);
            auxiliariesByVariant.add(aux);
            internalTransformerByVariant.add(it);
            rpclByVariant.add(r);
            uvaByVariant.add(u);
            aggregatedByVariant.add(agg);
            qlimByVariant.add(ql);
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        for (int i = 0; i < number; i++) {
            int lastIdx = numberOfWindingsByVariant.size() - 1;
            numberOfWindingsByVariant.remove(lastIdx);
            governorByVariant.remove(lastIdx);
            voltageRegulatorByVariant.remove(lastIdx);
            pssByVariant.remove(lastIdx);
            auxiliariesByVariant.remove(lastIdx);
            internalTransformerByVariant.remove(lastIdx);
            rpclByVariant.remove(lastIdx);
            uvaByVariant.remove(lastIdx);
            aggregatedByVariant.remove(lastIdx);
            qlimByVariant.remove(lastIdx);
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        // Nothing to do
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        Windings w = numberOfWindingsByVariant.get(sourceIndex);
        String gov = governorByVariant.get(sourceIndex);
        String vr = voltageRegulatorByVariant.get(sourceIndex);
        String p = pssByVariant.get(sourceIndex);
        Boolean aux = auxiliariesByVariant.get(sourceIndex);
        Boolean it = internalTransformerByVariant.get(sourceIndex);
        RpclType r = rpclByVariant.get(sourceIndex);
        Uva u = uvaByVariant.get(sourceIndex);
        Boolean agg = aggregatedByVariant.get(sourceIndex);
        Boolean ql = qlimByVariant.get(sourceIndex);

        for (int idx : indexes) {
            numberOfWindingsByVariant.set(idx, w);
            governorByVariant.set(idx, gov);
            voltageRegulatorByVariant.set(idx, vr);
            pssByVariant.set(idx, p);
            auxiliariesByVariant.set(idx, aux);
            internalTransformerByVariant.set(idx, it);
            rpclByVariant.set(idx, r);
            uvaByVariant.set(idx, u);
            aggregatedByVariant.set(idx, agg);
            qlimByVariant.set(idx, ql);
        }
    }
}
