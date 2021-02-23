/*
 * channel
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.alert.channel.msteams2;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;
import com.synopsys.integration.alert.processor.api.extract.model.ProviderDetails;

public class MSTeamsChannelMessageModel extends AlertSerializableModel {
    private final ProviderDetails providerDetails;
    @Nullable
    private final String messageTitle;
    private final String messageContent;

    public MSTeamsChannelMessageModel(ProviderDetails providerDetails, @Nullable String messageTitle, String messageContent) {
        this.providerDetails = providerDetails;
        this.messageTitle = messageTitle;
        this.messageContent = messageContent;
    }

    public ProviderDetails getProviderDetails() {
        return providerDetails;
    }

    public Optional<String> getMessageTitle() {
        return Optional.ofNullable(messageTitle);
    }

    public String getMessageContent() {
        return messageContent;
    }

}
