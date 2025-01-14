/*
 * azure-boards-common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.azure.boards.common.service.comment;

import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.synopsys.integration.azure.boards.common.http.AzureApiVersionAppender;
import com.synopsys.integration.azure.boards.common.http.AzureHttpService;
import com.synopsys.integration.azure.boards.common.http.HttpServiceException;
import com.synopsys.integration.azure.boards.common.service.comment.model.WorkItemCommentRequestModel;
import com.synopsys.integration.azure.boards.common.service.comment.model.WorkItemCommentResponseModel;
import com.synopsys.integration.azure.boards.common.service.comment.model.WorkItemMultiCommentResponseModel;

public class AzureWorkItemCommentService {
    private final AzureHttpService azureHttpService;
    private final AzureApiVersionAppender azureApiVersionAppender;

    public AzureWorkItemCommentService(AzureHttpService azureHttpService, AzureApiVersionAppender azureApiVersionAppender) {
        this.azureHttpService = azureHttpService;
        this.azureApiVersionAppender = azureApiVersionAppender;
    }

    public WorkItemMultiCommentResponseModel getComments(String organizationName, String projectIdOrName, Integer workItemId) throws HttpServiceException {
        return getComments(organizationName, projectIdOrName, workItemId, null);
    }

    public WorkItemMultiCommentResponseModel getComments(String organizationName, String projectIdOrName, Integer workItemId, @Nullable String continuationToken) throws HttpServiceException {
        String requestSpec = createCommentsSpec(organizationName, projectIdOrName, workItemId);
        if (StringUtils.isNotBlank(continuationToken)) {
            requestSpec += String.format("&continuationToken=%s", continuationToken);
        }
        return azureHttpService.get(requestSpec, WorkItemMultiCommentResponseModel.class);
    }

    public WorkItemCommentResponseModel addComment(String organizationName, String projectIdOrName, Integer workItemId, String commentText) throws HttpServiceException {
        String requestSpec = createCommentsSpec(organizationName, projectIdOrName, workItemId);
        GenericUrl requestUrl = azureHttpService.constructRequestUrl(requestSpec);
        try {
            WorkItemCommentRequestModel requestModel = new WorkItemCommentRequestModel(commentText);
            HttpRequest httpRequest = azureHttpService.buildRequestWithDefaultHeaders(HttpMethods.POST, requestUrl, requestModel);
            return azureHttpService.executeRequestAndParseResponse(httpRequest, WorkItemCommentResponseModel.class);
        } catch (IOException e) {
            throw HttpServiceException.internalServerError(e);
        }
    }

    private String createCommentsSpec(String organizationName, String projectIdOrName, Integer workItemId) {
        String spec = String.format("/%s/%s/_apis/wit/workItems/%s/comments", organizationName, projectIdOrName, workItemId);
        return azureApiVersionAppender.appendApiVersion5_1_Preview_3(spec);
    }
}
