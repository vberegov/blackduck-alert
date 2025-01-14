/*
 * provider
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.provider.blackduck.processor.model;

import com.synopsys.integration.blackduck.api.manual.component.NotificationContentComponent;

public abstract class AbstractProjectVersionNotificationContent extends NotificationContentComponent {
    private final String projectName;
    private final String projectVersionName;
    private final String projectVersion;

    public AbstractProjectVersionNotificationContent(String projectName, String projectVersionName, String projectVersion) {
        this.projectName = projectName;
        this.projectVersionName = projectVersionName;
        this.projectVersion = projectVersion;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

}
