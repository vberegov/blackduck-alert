/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.alert.web.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.blackducksoftware.integration.hub.alert.datasource.entity.distribution.SlackDistributionConfigEntity;
import com.blackducksoftware.integration.hub.alert.datasource.entity.global.GlobalSlackConfigEntity;
import com.blackducksoftware.integration.hub.alert.mock.SlackMockUtils;
import com.blackducksoftware.integration.hub.alert.web.model.distribution.SlackDistributionRestModel;

public class SlackConfigRestModelTest extends RestModelTest<SlackDistributionRestModel, ConfigRestModel, SlackDistributionConfigEntity, GlobalSlackConfigEntity> {
    private final static SlackMockUtils mockUtils = new SlackMockUtils();

    public SlackConfigRestModelTest() {
        super(mockUtils);
    }

    @Override
    public void assertRestModelFieldsNull(final SlackDistributionRestModel restModel) {
        assertNull(restModel.getChannelName());
        assertNull(restModel.getChannelUsername());
        assertNull(restModel.getWebhook());
    }

    @Override
    public long emptyRestModelSerialId() {
        return 3607759169675906880L;
    }

    @Override
    public int emptyRestModelHashCode() {
        return -2120005431;
    }

    @Override
    public void assertRestModelFieldsFull(final SlackDistributionRestModel restModel) {
        assertEquals(mockUtils.getWebhook(), restModel.getWebhook());
        assertEquals(mockUtils.getChannelName(), restModel.getChannelName());
        assertEquals(mockUtils.getChannelUsername(), restModel.getChannelUsername());
    }

    @Override
    public int restModelHashCode() {
        return 1336371893;
    }

}
