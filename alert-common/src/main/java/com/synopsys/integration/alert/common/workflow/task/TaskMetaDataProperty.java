/*
 * alert-common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.common.workflow.task;

import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;

public class TaskMetaDataProperty extends AlertSerializableModel {
    private static final long serialVersionUID = -295389156262597054L;
    private String key;
    private String displayName;
    private String value;

    public TaskMetaDataProperty(String key, String displayName, String value) {
        this.key = key;
        this.displayName = displayName;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }
}
