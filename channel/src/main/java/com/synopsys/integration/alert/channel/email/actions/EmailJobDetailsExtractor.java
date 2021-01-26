/**
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
package com.synopsys.integration.alert.channel.email.actions;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.channel.email.descriptor.EmailDescriptor;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.common.persistence.model.job.details.DistributionJobDetailsModel;
import com.synopsys.integration.alert.common.persistence.model.job.details.EmailJobDetailsModel;
import com.synopsys.integration.alert.common.persistence.model.job.details.processor.JobDetailsExtractor;

@Component
public class EmailJobDetailsExtractor extends JobDetailsExtractor {
    @Override
    protected DistributionJobDetailsModel convertToChannelJobDetails(UUID jobId, Map<String, ConfigurationFieldModel> configuredFieldsMap) {
        return new EmailJobDetailsModel(
            jobId,
            extractFieldValueOrEmptyString(EmailDescriptor.KEY_SUBJECT_LINE, configuredFieldsMap),
            extractFieldValue(EmailDescriptor.KEY_PROJECT_OWNER_ONLY, configuredFieldsMap).map(Boolean::valueOf).orElse(false),
            extractFieldValue(EmailDescriptor.KEY_EMAIL_ADDITIONAL_ADDRESSES_ONLY, configuredFieldsMap).map(Boolean::valueOf).orElse(false),
            extractFieldValueOrEmptyString(EmailDescriptor.KEY_EMAIL_ATTACHMENT_FORMAT, configuredFieldsMap),
            extractFieldValues(EmailDescriptor.KEY_EMAIL_ADDITIONAL_ADDRESSES, configuredFieldsMap)
        );
    }

}
