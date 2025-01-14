/*
 * component
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.component.tasks.web;

import java.util.List;

import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;
import com.synopsys.integration.alert.common.workflow.task.TaskMetaData;

public class MultiTaskMetaDataModel extends AlertSerializableModel {
    private List<TaskMetaData> tasks;

    public MultiTaskMetaDataModel(List<TaskMetaData> tasks) {
        this.tasks = tasks;
    }

    public List<TaskMetaData> getTasks() {
        return tasks;
    }
}
