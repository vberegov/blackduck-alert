/*
 * provider
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.provider.blackduck.descriptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.descriptor.ProviderDescriptor;
import com.synopsys.integration.alert.descriptor.api.BlackDuckProviderKey;

@Component
public class BlackDuckDescriptor extends ProviderDescriptor {
    public static final String KEY_BLACKDUCK_URL = "blackduck.url";
    public static final String KEY_BLACKDUCK_API_KEY = "blackduck.api.key";
    public static final String KEY_BLACKDUCK_TIMEOUT = "blackduck.timeout";
    public static final String KEY_BLACKDUCK_POLICY_NOTIFICATION_TYPE_FILTER = "blackduck.policy.notification.filter";
    public static final String KEY_BLACKDUCK_VULNERABILITY_NOTIFICATION_TYPE_FILTER = "blackduck.vulnerability.notification.filter";

    public static final String BLACKDUCK_LABEL = "Black Duck";
    public static final String BLACKDUCK_URL = "blackduck";
    public static final String BLACKDUCK_DESCRIPTION = "This is the configuration to connect to the Black Duck server. Configuring this will cause Alert to start pulling data from Black Duck.";

    @Autowired
    public BlackDuckDescriptor(BlackDuckProviderKey blackDuckProviderKey, BlackDuckProviderUIConfig blackDuckProviderUIConfig, BlackDuckDistributionUIConfig blackDuckDistributionUIConfig) {
        super(blackDuckProviderKey, blackDuckProviderUIConfig, blackDuckDistributionUIConfig);
    }

}
