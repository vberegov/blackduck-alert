/*
 * alert-common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.common.persistence.model;

import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;

public class ProviderUserModel extends AlertSerializableModel {
    private String emailAddress;
    private Boolean optOut;

    public ProviderUserModel(String emailAddress, Boolean optOut) {
        this.emailAddress = emailAddress;
        this.optOut = optOut;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Boolean getOptOut() {
        return optOut;
    }

}
