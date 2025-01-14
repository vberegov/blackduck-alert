/*
 * channel
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.channel.jira.cloud.distribution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.synopsys.integration.alert.channel.api.issue.callback.IssueTrackerCallbackInfoCreator;
import com.synopsys.integration.alert.channel.api.issue.send.IssueTrackerIssueResponseCreator;
import com.synopsys.integration.alert.channel.api.issue.send.IssueTrackerMessageSender;
import com.synopsys.integration.alert.channel.api.issue.send.IssueTrackerMessageSenderFactory;
import com.synopsys.integration.alert.channel.jira.cloud.JiraCloudProperties;
import com.synopsys.integration.alert.channel.jira.cloud.JiraCloudPropertiesFactory;
import com.synopsys.integration.alert.channel.jira.cloud.distribution.delegate.JiraCloudIssueCommenter;
import com.synopsys.integration.alert.channel.jira.cloud.distribution.delegate.JiraCloudIssueCreator;
import com.synopsys.integration.alert.channel.jira.cloud.distribution.delegate.JiraCloudIssueTransitioner;
import com.synopsys.integration.alert.channel.jira.common.distribution.JiraErrorMessageUtility;
import com.synopsys.integration.alert.channel.jira.common.distribution.JiraIssueCreationRequestCreator;
import com.synopsys.integration.alert.channel.jira.common.distribution.custom.JiraCustomFieldResolver;
import com.synopsys.integration.alert.channel.jira.common.distribution.search.JiraIssueAlertPropertiesManager;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.persistence.model.job.details.JiraCloudJobDetailsModel;
import com.synopsys.integration.alert.descriptor.api.JiraCloudChannelKey;
import com.synopsys.integration.jira.common.cloud.service.FieldService;
import com.synopsys.integration.jira.common.cloud.service.IssueService;
import com.synopsys.integration.jira.common.cloud.service.JiraCloudServiceFactory;
import com.synopsys.integration.jira.common.cloud.service.ProjectService;
import com.synopsys.integration.jira.common.rest.service.IssuePropertyService;

@Component
public class JiraCloudMessageSenderFactory implements IssueTrackerMessageSenderFactory<JiraCloudJobDetailsModel, String> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Gson gson;
    private final JiraCloudChannelKey channelKey;
    private final JiraCloudPropertiesFactory jiraCloudPropertiesFactory;
    private final IssueTrackerCallbackInfoCreator callbackInfoCreator;
    private final JiraErrorMessageUtility jiraErrorMessageUtility;

    @Autowired
    public JiraCloudMessageSenderFactory(
        Gson gson,
        JiraCloudChannelKey channelKey,
        JiraCloudPropertiesFactory jiraCloudPropertiesFactory,
        IssueTrackerCallbackInfoCreator callbackInfoCreator,
        JiraErrorMessageUtility jiraErrorMessageUtility
    ) {
        this.gson = gson;
        this.channelKey = channelKey;
        this.jiraCloudPropertiesFactory = jiraCloudPropertiesFactory;
        this.callbackInfoCreator = callbackInfoCreator;
        this.jiraErrorMessageUtility = jiraErrorMessageUtility;
    }

    @Override
    public IssueTrackerMessageSender<String> createMessageSender(JiraCloudJobDetailsModel distributionDetails) throws AlertException {
        JiraCloudProperties jiraCloudProperties = jiraCloudPropertiesFactory.createJiraProperties();
        JiraCloudServiceFactory jiraCloudServiceFactory = jiraCloudProperties.createJiraServicesCloudFactory(logger, gson);

        // Jira Services
        IssueService issueService = jiraCloudServiceFactory.createIssueService();
        IssuePropertyService issuePropertyService = jiraCloudServiceFactory.createIssuePropertyService();

        // Common Helpers
        JiraIssueAlertPropertiesManager issuePropertiesManager = new JiraIssueAlertPropertiesManager(gson, issuePropertyService);

        return createMessageSender(issueService, distributionDetails, jiraCloudServiceFactory, issuePropertiesManager);
    }

    public IssueTrackerMessageSender<String> createMessageSender(
        IssueService issueService,
        JiraCloudJobDetailsModel distributionDetails,
        JiraCloudServiceFactory jiraCloudServiceFactory,
        JiraIssueAlertPropertiesManager issuePropertiesManager
    ) {
        // Jira Services
        IssueTrackerIssueResponseCreator issueResponseCreator = new IssueTrackerIssueResponseCreator(callbackInfoCreator);

        // Message Sender Requirements
        JiraCloudIssueCommenter issueCommenter = new JiraCloudIssueCommenter(issueResponseCreator, issueService, distributionDetails);
        JiraCloudIssueTransitioner issueTransitioner = new JiraCloudIssueTransitioner(issueCommenter, issueResponseCreator, distributionDetails, issueService);
        JiraCloudIssueCreator issueCreator = createIssueCreator(distributionDetails, jiraCloudServiceFactory, issuePropertiesManager, issueService, issueCommenter);

        return new IssueTrackerMessageSender<>(issueCreator, issueTransitioner, issueCommenter);
    }

    private JiraCloudIssueCreator createIssueCreator(
        JiraCloudJobDetailsModel distributionDetails,
        JiraCloudServiceFactory jiraCloudServiceFactory,
        JiraIssueAlertPropertiesManager issuePropertiesManager,
        IssueService issueService,
        JiraCloudIssueCommenter issueCommenter
    ) {
        ProjectService projectService = jiraCloudServiceFactory.createProjectService();
        FieldService fieldService = jiraCloudServiceFactory.createFieldService();

        JiraCustomFieldResolver customFieldResolver = new JiraCustomFieldResolver(fieldService::getUserVisibleFields);
        JiraIssueCreationRequestCreator issueCreationRequestCreator = new JiraIssueCreationRequestCreator(customFieldResolver);

        return new JiraCloudIssueCreator(
            channelKey,
            issueCommenter,
            callbackInfoCreator,
            distributionDetails,
            issueService,
            projectService,
            issueCreationRequestCreator,
            issuePropertiesManager,
            jiraErrorMessageUtility
        );
    }
}
