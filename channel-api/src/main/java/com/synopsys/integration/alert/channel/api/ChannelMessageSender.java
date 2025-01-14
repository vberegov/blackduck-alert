/*
 * channel-api
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.channel.api;

import java.util.List;

import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.persistence.model.job.details.DistributionJobDetailsModel;

public interface ChannelMessageSender<D extends DistributionJobDetailsModel, M, R> {
    R sendMessages(D details, List<M> channelMessages) throws AlertException;

}
