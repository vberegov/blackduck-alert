/*
 * channel-api
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.channel.api.issue.search;

import java.io.Serializable;

import com.synopsys.integration.alert.channel.api.issue.model.ProjectIssueModel;
import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;

public class ProjectIssueSearchResult<T extends Serializable> extends AlertSerializableModel {
    private final ExistingIssueDetails<T> existingIssueDetails;
    private final ProjectIssueModel projectIssueModel;

    public ProjectIssueSearchResult(ExistingIssueDetails<T> existingIssueDetails, ProjectIssueModel projectIssueModel) {
        this.existingIssueDetails = existingIssueDetails;
        this.projectIssueModel = projectIssueModel;
    }

    public ExistingIssueDetails<T> getExistingIssueDetails() {
        return existingIssueDetails;
    }

    public ProjectIssueModel getProjectIssueModel() {
        return projectIssueModel;
    }

}
