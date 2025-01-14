/*
 * alert-common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.common.rest.model;

import java.util.List;

public class MultiFieldModel extends MultiResponseModel<FieldModel> {
    public MultiFieldModel(List<FieldModel> models) {
        super(models);
    }

    public List<FieldModel> getFieldModels() {
        return getModels();
    }

}
