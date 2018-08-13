/**
 * blackduck-alert
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.alert.channel.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import com.google.gson.Gson;
import com.synopsys.integration.alert.channel.DistributionChannel;
import com.synopsys.integration.alert.channel.event.ChannelEvent;
import com.synopsys.integration.alert.common.AlertProperties;
import com.synopsys.integration.alert.common.ContentConverter;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.database.audit.AuditEntryRepository;
import com.synopsys.integration.alert.database.entity.channel.DistributionChannelConfigEntity;
import com.synopsys.integration.alert.database.entity.channel.GlobalChannelConfigEntity;
import com.synopsys.integration.alert.database.entity.repository.CommonDistributionRepository;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProperties;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.body.BodyContent;
import com.synopsys.integration.rest.body.StringBodyContent;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;

public abstract class RestDistributionChannel<G extends GlobalChannelConfigEntity, C extends DistributionChannelConfigEntity> extends DistributionChannel<G, C> {
    private final ChannelRestConnectionFactory channelRestConnectionFactory;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public RestDistributionChannel(final Gson gson, final AlertProperties alertProperties, final BlackDuckProperties blackDuckProperties, final AuditEntryRepository auditEntryRepository, final JpaRepository<G, Long> globalRepository,
            final JpaRepository<C, Long> distributionRepository,
            final CommonDistributionRepository commonDistributionRepository, final ChannelRestConnectionFactory channelRestConnectionFactory, final ContentConverter contentExtractor) {
        super(gson, alertProperties, blackDuckProperties, auditEntryRepository, globalRepository, distributionRepository, commonDistributionRepository, contentExtractor);
        this.channelRestConnectionFactory = channelRestConnectionFactory;
    }

    @Override
    public void sendMessage(final ChannelEvent event, final C config) throws IntegrationException {
        final G globalConfig = getGlobalConfigEntity();
        try (final RestConnection restConnection = channelRestConnectionFactory.createUnauthenticatedRestConnection(getApiUrl(globalConfig))) {
            final List<Request> requests = createRequests(config, globalConfig, event);
            for (final Request request : requests) {
                sendMessageRequest(restConnection, request, event.getDestination());
            }
        } catch (final IOException ex) {
            throw new AlertException(ex);
        }
    }

    public Request createPostMessageRequest(final String url, final Map<String, String> headers, final String jsonString) {
        Request.Builder requestBuilder = new Request.Builder();
        final BodyContent bodyContent = new StringBodyContent(jsonString);
        requestBuilder = requestBuilder.method(HttpMethod.POST).uri(url).additionalHeaders(headers).bodyContent(bodyContent);
        final Request request = requestBuilder.build();
        return request;
    }

    public Request createPostMessageRequest(final String url, final Map<String, String> headers, final Map<String, Set<String>> queryParameters, final String jsonString) {
        Request.Builder requestBuilder = new Request.Builder();
        final BodyContent bodyContent = new StringBodyContent(jsonString);
        requestBuilder = requestBuilder.method(HttpMethod.POST).uri(url).additionalHeaders(headers).queryParameters(queryParameters).bodyContent(bodyContent);
        final Request request = requestBuilder.build();
        return request;
    }

    public void sendMessageRequest(final RestConnection restConnection, final Request request, final String messageType) throws IntegrationException {
        logger.info("Attempting to send a {} message...", messageType);
        final Response response = sendGenericRequest(restConnection, request);
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 400) {
            logger.info("Successfully sent a {} message!", messageType);
        }
    }

    public Response sendGenericRequest(final RestConnection restConnection, final Request request) throws IntegrationException {
        try {
            final Response response = restConnection.executeRequest(request);
            logger.trace("Response: " + response.toString());
            return response;
        } catch (final Exception generalException) {
            logger.error("Error sending request", generalException);
            throw new AlertException(generalException.getMessage());
        }
    }

    public ChannelRestConnectionFactory getChannelRestConnectionFactory() {
        return channelRestConnectionFactory;
    }

    public abstract String getApiUrl(G globalConfig);

    public abstract List<Request> createRequests(final C config, G globalConfig, final ChannelEvent event) throws IntegrationException;

}

