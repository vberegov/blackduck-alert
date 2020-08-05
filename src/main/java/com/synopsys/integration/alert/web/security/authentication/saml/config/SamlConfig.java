/**
 * blackduck-alert
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
package com.synopsys.integration.alert.web.security.authentication.saml.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.SAMLBootstrap;

import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.component.authentication.descriptor.AuthenticationDescriptorKey;
import com.synopsys.integration.alert.web.security.authentication.saml.SAMLContext;

@Configuration
public class SamlConfig {
    private final AuthenticationDescriptorKey authenticationDescriptorKey;
    private final ConfigurationAccessor configurationAccessor;

    @Autowired
    public SamlConfig(AuthenticationDescriptorKey authenticationDescriptorKey, ConfigurationAccessor configurationAccessor) {
        this.authenticationDescriptorKey = authenticationDescriptorKey;
        this.configurationAccessor = configurationAccessor;
    }

    @Bean
    public SAMLContext samlContext() {
        return new SAMLContext(authenticationDescriptorKey, configurationAccessor);
    }

    @Bean
    public static SAMLBootstrap SAMLBootstrap() {
        return new SAMLBootstrap();
    }

}
