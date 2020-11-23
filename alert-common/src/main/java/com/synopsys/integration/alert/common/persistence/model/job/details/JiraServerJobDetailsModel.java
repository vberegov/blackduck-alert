/**
 * alert-common
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.alert.common.persistence.model.job.details;

public class JiraServerJobDetailsModel extends DistributionJobDetailsModel {
    private final boolean addComments;
    private final String issueCreatorUsername;
    private final String projectNameOrKey;
    private final String issueType;
    private final String resolveTransition;
    private final String reopenTransition;

    public JiraServerJobDetailsModel(boolean addComments, String issueCreatorUsername, String projectNameOrKey, String issueType, String resolveTransition, String reopenTransition) {
        super("channel_jira_server");
        this.addComments = addComments;
        this.issueCreatorUsername = issueCreatorUsername;
        this.projectNameOrKey = projectNameOrKey;
        this.issueType = issueType;
        this.resolveTransition = resolveTransition;
        this.reopenTransition = reopenTransition;
    }

    public boolean isAddComments() {
        return addComments;
    }

    public String getIssueCreatorUsername() {
        return issueCreatorUsername;
    }

    public String getProjectNameOrKey() {
        return projectNameOrKey;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getResolveTransition() {
        return resolveTransition;
    }

    public String getReopenTransition() {
        return reopenTransition;
    }

}