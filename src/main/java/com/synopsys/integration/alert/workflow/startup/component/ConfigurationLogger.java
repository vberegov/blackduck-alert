/*
 * blackduck-alert
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.workflow.startup.component;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.AlertProperties;
import com.synopsys.integration.alert.common.rest.ProxyManager;

@Component
@Order(40)
public class ConfigurationLogger extends StartupComponent {
    private final Logger logger = LoggerFactory.getLogger(ConfigurationLogger.class);

    private final ProxyManager proxyManager;
    private final AlertProperties alertProperties;

    @Autowired
    public ConfigurationLogger(ProxyManager proxyManager, AlertProperties alertProperties) {
        this.proxyManager = proxyManager;
        this.alertProperties = alertProperties;
    }

    @Override
    protected void initialize() {
        Optional<String> proxyHost = proxyManager.getProxyHost();
        Optional<String> proxyPort = proxyManager.getProxyPort();
        Optional<String> proxyUsername = proxyManager.getProxyUsername();
        Optional<String> proxyPassword = proxyManager.getProxyPassword();

        boolean authenticatedProxy = StringUtils.isNotBlank(proxyPassword.orElse(""));

        logger.info("----------------------------------------");
        logger.info("Alert Configuration: ");
        logger.info("Alert Server URL:          {}", alertProperties.getRootURL());
        logger.info("Logging level:             {}", alertProperties.getLoggingLevel().orElse(""));
        logger.info("Alert Proxy Host:          {}", proxyHost.orElse(""));
        logger.info("Alert Proxy Port:          {}", proxyPort.orElse(""));
        logger.info("Alert Proxy Authenticated: {}", authenticatedProxy);
        logger.info("Alert Proxy User:          {}", proxyUsername.orElse(""));
        logger.info("");
        logger.info("----------------------------------------");
    }
}
