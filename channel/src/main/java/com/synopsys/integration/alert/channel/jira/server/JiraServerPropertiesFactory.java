/**
 * channel
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
package com.synopsys.integration.alert.channel.jira.server;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.channel.jira.server.descriptor.JiraServerDescriptor;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.exception.AlertDatabaseConstraintException;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.accessor.FieldUtility;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.common.rest.ProxyManager;
import com.synopsys.integration.alert.common.rest.model.FieldModel;
import com.synopsys.integration.alert.common.rest.model.FieldValueModel;
import com.synopsys.integration.alert.descriptor.api.JiraServerChannelKey;

@Component
public class JiraServerPropertiesFactory {
    private final Logger logger = LoggerFactory.getLogger(JiraServerPropertiesFactory.class);

    private final JiraServerChannelKey jiraChannelKey;
    private final ProxyManager proxyManager;
    private final ConfigurationAccessor configurationAccessor;

    @Autowired
    public JiraServerPropertiesFactory(JiraServerChannelKey jiraChannelKey, ProxyManager proxyManager, ConfigurationAccessor configurationAccessor) {
        this.jiraChannelKey = jiraChannelKey;
        this.proxyManager = proxyManager;
        this.configurationAccessor = configurationAccessor;
    }

    public JiraServerProperties createJiraProperties(FieldUtility fieldUtility) {
        String url = fieldUtility.getStringOrNull(JiraServerDescriptor.KEY_SERVER_URL);
        String username = fieldUtility.getStringOrNull(JiraServerDescriptor.KEY_SERVER_USERNAME);
        String password = fieldUtility.getStringOrNull(JiraServerDescriptor.KEY_SERVER_PASSWORD);
        boolean pluginCheckDisabled = fieldUtility.getBooleanOrFalse(JiraServerDescriptor.KEY_JIRA_DISABLE_PLUGIN_CHECK);
        return new JiraServerProperties(url, password, username, pluginCheckDisabled, proxyManager.createProxyInfo());
    }

    public JiraServerProperties createJiraProperties(FieldModel fieldModel) {
        String url = fieldModel.getFieldValue(JiraServerDescriptor.KEY_SERVER_URL).orElse("");
        String username = fieldModel.getFieldValue(JiraServerDescriptor.KEY_SERVER_USERNAME).orElse("");
        String password = fieldModel.getFieldValueModel(JiraServerDescriptor.KEY_SERVER_PASSWORD)
                              .map(this::getAppropriateAccessToken)
                              .orElse("");
        boolean pluginCheckDisabled = fieldModel.getFieldValue(JiraServerDescriptor.KEY_JIRA_DISABLE_PLUGIN_CHECK).map(Boolean::parseBoolean).orElse(false);

        return new JiraServerProperties(url, password, username, pluginCheckDisabled, proxyManager.createProxyInfo());
    }

    private String getAppropriateAccessToken(FieldValueModel fieldAccessToken) {
        String accessToken = fieldAccessToken.getValue().orElse("");
        boolean accessTokenSet = fieldAccessToken.getIsSet();
        if (StringUtils.isBlank(accessToken) && accessTokenSet) {
            try {
                return configurationAccessor.getConfigurationsByDescriptorKeyAndContext(jiraChannelKey, ConfigContextEnum.GLOBAL)
                           .stream()
                           .findFirst()
                           .flatMap(configurationModel -> configurationModel.getField(JiraServerDescriptor.KEY_SERVER_PASSWORD))
                           .flatMap(ConfigurationFieldModel::getFieldValue)
                           .orElse("");

            } catch (AlertDatabaseConstraintException e) {
                logger.error("Unable to retrieve existing Jira configuration.");
            }
        }

        return accessToken;
    }
}