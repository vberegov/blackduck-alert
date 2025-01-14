/*
 * alert-common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.common.persistence.model;

import java.util.Collection;
import java.util.Collections;

import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;

public class DefinedFieldModel extends AlertSerializableModel {
    private final String key;
    private final Collection<ConfigContextEnum> contexts;
    private final Boolean sensitive;

    public static DefinedFieldModel createDistributionField(final String key) {
        return new DefinedFieldModel(key, ConfigContextEnum.DISTRIBUTION, false);
    }

    public static DefinedFieldModel createDistributionSensitiveField(final String key) {
        return new DefinedFieldModel(key, ConfigContextEnum.DISTRIBUTION, true);
    }

    public static DefinedFieldModel createGlobalField(final String key) {
        return new DefinedFieldModel(key, ConfigContextEnum.GLOBAL, false);
    }

    public static DefinedFieldModel createGlobalSensitiveField(final String key) {
        return new DefinedFieldModel(key, ConfigContextEnum.GLOBAL, true);
    }

    public DefinedFieldModel(final String key, final ConfigContextEnum context, final Boolean sensitive) {
        this.key = key;
        contexts = Collections.singleton(context);
        this.sensitive = sensitive;
    }

    public DefinedFieldModel(final String key, final Collection<ConfigContextEnum> contexts, final Boolean sensitive) {
        this.key = key;
        this.contexts = contexts;
        this.sensitive = sensitive;
    }

    public String getKey() {
        return key;
    }

    public Boolean getSensitive() {
        return sensitive;
    }

    public Collection<ConfigContextEnum> getContexts() {
        return contexts;
    }
}
