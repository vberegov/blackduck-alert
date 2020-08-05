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

import org.opensaml.xml.parse.ParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataManager;

import com.synopsys.integration.alert.common.persistence.util.FilePersistenceUtil;
import com.synopsys.integration.alert.web.security.authentication.saml.SAMLContext;
import com.synopsys.integration.alert.web.security.authentication.saml.SAMLManager;

@Configuration
public class SAMLManagerConfig {
    private final FilePersistenceUtil filePersistenceUtil;
    private final SAMLContext samlContext;
    private final MetadataGenerator metadataGenerator;
    private final ExtendedMetadata extendedMetadata;
    private final ParserPool parserPool;
    private final MetadataManager metadata;

    @Autowired
    public SAMLManagerConfig(FilePersistenceUtil filePersistenceUtil, SAMLContext samlContext, MetadataGenerator metadataGenerator, ExtendedMetadata extendedMetadata, ParserPool parserPool,
        MetadataManager metadata) {
        this.filePersistenceUtil = filePersistenceUtil;
        this.samlContext = samlContext;
        this.metadataGenerator = metadataGenerator;
        this.extendedMetadata = extendedMetadata;
        this.parserPool = parserPool;
        this.metadata = metadata;
    }

    @Bean
    public SAMLManager samlManager() {
        return new SAMLManager(parserPool, extendedMetadata, metadata, metadataGenerator, filePersistenceUtil, samlContext);
    }
}
