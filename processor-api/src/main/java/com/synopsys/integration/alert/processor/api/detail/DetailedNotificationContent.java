/*
 * processor-api
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.processor.api.detail;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.alert.common.rest.model.AlertNotificationModel;
import com.synopsys.integration.alert.processor.api.filter.NotificationContentWrapper;
import com.synopsys.integration.blackduck.api.manual.component.NotificationContentComponent;
import com.synopsys.integration.util.Stringable;

// NotificationContentWrapper is not serializable, so this class cannot be serializable (and doesn't need to be)
public class DetailedNotificationContent extends Stringable {
    private final String projectName;
    private final String policyName;
    private final List<String> vulnerabilitySeverities;
    private final NotificationContentWrapper notificationContentWrapper;

    public static DetailedNotificationContent vulnerability(
        AlertNotificationModel notificationModel,
        NotificationContentComponent notificationContent,
        String projectName,
        List<String> vulnerabilitySeverities
    ) {
        return new DetailedNotificationContent(notificationModel, notificationContent, projectName, null, vulnerabilitySeverities);
    }

    public static DetailedNotificationContent policy(
        AlertNotificationModel notificationModel,
        NotificationContentComponent notificationContent,
        String projectName,
        String policyName
    ) {
        return new DetailedNotificationContent(notificationModel, notificationContent, projectName, policyName, List.of());
    }

    public static DetailedNotificationContent project(AlertNotificationModel notificationModel, NotificationContentComponent notificationContent, String projectName) {
        return new DetailedNotificationContent(notificationModel, notificationContent, projectName, null, List.of());
    }

    public static DetailedNotificationContent projectless(AlertNotificationModel notificationModel, NotificationContentComponent notificationContent) {
        return new DetailedNotificationContent(notificationModel, notificationContent, null, null, List.of());
    }

    private DetailedNotificationContent(
        AlertNotificationModel alertNotificationModel,
        NotificationContentComponent notificationContent,
        @Nullable String projectName,
        @Nullable String policyName,
        List<String> vulnerabilitySeverities
    ) {
        this.projectName = StringUtils.trimToNull(projectName);
        this.policyName = StringUtils.trimToNull(policyName);
        this.vulnerabilitySeverities = vulnerabilitySeverities;
        this.notificationContentWrapper = new NotificationContentWrapper(alertNotificationModel, notificationContent, notificationContent.getClass());
    }

    public Optional<String> getProjectName() {
        return Optional.ofNullable(projectName);
    }

    public Optional<String> getPolicyName() {
        return Optional.ofNullable(policyName);
    }

    public List<String> getVulnerabilitySeverities() {
        return vulnerabilitySeverities;
    }

    public NotificationContentWrapper getNotificationContentWrapper() {
        return notificationContentWrapper;
    }

}
