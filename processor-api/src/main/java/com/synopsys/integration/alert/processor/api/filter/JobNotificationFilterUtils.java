package com.synopsys.integration.alert.processor.api.filter;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;

import com.synopsys.integration.alert.common.persistence.model.job.BlackDuckProjectDetailsModel;
import com.synopsys.integration.alert.common.persistence.model.job.FilteredDistributionJobResponseModel;
import com.synopsys.integration.alert.processor.api.detail.DetailedNotificationContent;
import com.synopsys.integration.blackduck.api.manual.enumeration.NotificationType;

public class JobNotificationFilterUtils {
    private static List<String> POLICY_NOTIFICATION_TYPES = List.of(NotificationType.POLICY_OVERRIDE.name(), NotificationType.RULE_VIOLATION.name(), NotificationType.RULE_VIOLATION_CLEARED.name());

    public static boolean doesNotificationApplyToJob(FilteredDistributionJobResponseModel filteredDistributionJobResponseModel, DetailedNotificationContent detailedNotificationContent) {
        String notificationType = detailedNotificationContent.getNotificationContentWrapper().extractNotificationType();
        NotificationType notificationTypeEnum = NotificationType.valueOf(notificationType);

        if (!doesNotificationTypeMatch(filteredDistributionJobResponseModel, notificationType)) {
            return false;
        }
        String projectName = detailedNotificationContent.getProjectName().orElse("");
        if (!doesProjectApplyToJob(filteredDistributionJobResponseModel, projectName)) {
            return false;
        }
        //TODO test if notificationTypeEnum is null
        switch (notificationTypeEnum) {
            case VULNERABILITY:
                List<String> notificationSeverities = detailedNotificationContent.getVulnerabilitySeverities();
                if (!doVulnerabilitySeveritiesApplyToJob(filteredDistributionJobResponseModel, notificationSeverities)) {
                    return false;
                }
                break;
            case POLICY_OVERRIDE:
            case RULE_VIOLATION:
            case RULE_VIOLATION_CLEARED:
                String policyName = detailedNotificationContent.getPolicyName().orElse("");
                if (!doesPolicyApplyToJob(filteredDistributionJobResponseModel, policyName)) {
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    public static boolean doesNotificationTypeMatch(FilteredDistributionJobResponseModel filteredDistributionJobResponseModel, String notificationType) {
        return filteredDistributionJobResponseModel.getNotificationTypes().contains(notificationType);
    }

    public static boolean doesProjectApplyToJob(FilteredDistributionJobResponseModel filteredDistributionJobResponseModel, String projectName) {
        if (!filteredDistributionJobResponseModel.isFilterByProject()) {
            return true;
        }

        String projectNamePattern = filteredDistributionJobResponseModel.getProjectNamePattern();
        if (projectNamePattern != null && Pattern.matches(projectNamePattern, projectName)) {
            return true;
        }

        return filteredDistributionJobResponseModel.getProjectDetails()
                   .stream()
                   .map(BlackDuckProjectDetailsModel::getName)
                   .distinct()
                   .anyMatch(projectName::equals);
    }

    public static boolean doVulnerabilitySeveritiesApplyToJob(FilteredDistributionJobResponseModel filteredDistributionJobResponseModel, List<String> notificationSeverities) {
        List<String> jobVulnerabilitySeverityFilters = filteredDistributionJobResponseModel.getVulnerabilitySeverityNames();
        if (jobVulnerabilitySeverityFilters.isEmpty()) {
            return true;
        }
        return CollectionUtils.containsAny(notificationSeverities, jobVulnerabilitySeverityFilters);
    }

    public static boolean doesPolicyApplyToJob(FilteredDistributionJobResponseModel filteredDistributionJobResponseModel, String policyName) {
        List<String> policyNamesFilters = filteredDistributionJobResponseModel.getPolicyNames();
        if (policyNamesFilters.isEmpty()) {
            return true;
        }
        return policyNamesFilters.contains(policyName);
    }

    private JobNotificationFilterUtils() {
        //this class should not be instantiated
    }
}
