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

import java.util.Arrays;
import java.util.Collections;

import org.opensaml.saml2.core.NameIDType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;

import com.synopsys.integration.alert.web.security.authentication.saml.AlertSAMLMetadataGenerator;
import com.synopsys.integration.alert.web.security.authentication.saml.AlertSAMLMetadataGeneratorFilter;
import com.synopsys.integration.alert.web.security.authentication.saml.SAMLContext;

@Configuration
public class SAMLMetadataConfig {
    private final SAMLContext samlContext;
    private final KeyManager keyManager;

    @Autowired
    public SAMLMetadataConfig(SAMLContext samlContext, KeyManager keyManager) {
        this.samlContext = samlContext;
        this.keyManager = keyManager;
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter(SAMLContext samlContext) {
        return new AlertSAMLMetadataGeneratorFilter(metadataGenerator(), samlContext);
    }

    @Bean
    public MetadataGenerator metadataGenerator() {
        AlertSAMLMetadataGenerator metadataGenerator = new AlertSAMLMetadataGenerator(samlContext);
        metadataGenerator.setExtendedMetadata(extendedMetadata());
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setKeyManager(keyManager);
        metadataGenerator.setRequestSigned(false);
        metadataGenerator.setWantAssertionSigned(false);
        metadataGenerator.setBindingsSLO(Collections.emptyList());
        metadataGenerator.setBindingsSSO(Arrays.asList("post"));
        metadataGenerator.setNameID(Arrays.asList(NameIDType.UNSPECIFIED));

        return metadataGenerator;
    }

    @Bean
    public ExtendedMetadata extendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setSignMetadata(false);
        extendedMetadata.setEcpEnabled(true);
        extendedMetadata.setRequireLogoutRequestSigned(false);
        return extendedMetadata;
    }
}
