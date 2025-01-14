/*
 * alert-common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.common.descriptor.config.ui;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.alert.common.descriptor.config.field.ConfigField;
import com.synopsys.integration.alert.common.enumeration.AccessOperation;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.enumeration.DescriptorType;
import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;
import com.synopsys.integration.alert.descriptor.api.model.DescriptorKey;

public class DescriptorMetadata extends AlertSerializableModel {
    private static final long serialVersionUID = -6213193510077419010L;
    private final String label;
    private final String urlName;
    private final String name;
    private final String description;
    private final DescriptorType type;
    private final ConfigContextEnum context;
    private final boolean automaticallyGenerateUI;
    private final String componentNamespace;
    private List<ConfigField> fields;
    private Set<AccessOperation> operations;
    private boolean readOnly;
    private List<ConfigField> testFields;

    public DescriptorMetadata(DescriptorKey descriptorKey, String label, String urlName, String description, DescriptorType type, ConfigContextEnum context,
        boolean automaticallyGenerateUI, String componentNamespace, List<ConfigField> fields, List<ConfigField> testFields) {
        this.label = label;
        this.urlName = urlName;
        this.name = descriptorKey.getUniversalKey();
        this.description = description;
        this.type = type;
        this.context = context;
        this.automaticallyGenerateUI = automaticallyGenerateUI;
        this.componentNamespace = componentNamespace;
        this.fields = fields;
        this.operations = Set.of();
        this.testFields = testFields;
    }

    public String getLabel() {
        return label;
    }

    public String getUrlName() {
        return urlName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public DescriptorType getType() {
        return type;
    }

    public ConfigContextEnum getContext() {
        return context;
    }

    public boolean isAutomaticallyGenerateUI() {
        return automaticallyGenerateUI;
    }

    public String getComponentNamespace() {
        return componentNamespace;
    }

    public List<ConfigField> getFields() {
        return fields;
    }

    public void setFields(List<ConfigField> fields) {
        this.fields = fields;
    }

    public Set<AccessOperation> getOperations() {
        return operations;
    }

    public void setOperations(Set<AccessOperation> operations) {
        this.operations = operations;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public List<ConfigField> getTestFields() {
        return testFields;
    }

    public void setTestFields(List<ConfigField> testFields) {
        this.testFields = testFields;
    }

}
