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
package com.synopsys.integration.alert.web.api.functions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.synopsys.integration.alert.common.action.ActionResponse;
import com.synopsys.integration.alert.common.action.CustomFunctionAction;
import com.synopsys.integration.alert.common.rest.HttpServletContentWrapper;
import com.synopsys.integration.alert.common.rest.ResponseFactory;
import com.synopsys.integration.alert.common.rest.model.FieldModel;
import com.synopsys.integration.alert.web.common.BaseController;

public abstract class AbstractFunctionController<T> extends BaseController {
    public static final String API_FUNCTION_URL = CustomFunctionAction.API_FUNCTION_URL;
    private final CustomFunctionAction<T> functionAction;

    public AbstractFunctionController(CustomFunctionAction<T> functionAction) {
        this.functionAction = functionAction;
    }

    @PostMapping
    public ResponseEntity<T> postConfig(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestBody FieldModel restModel) {
        HttpServletContentWrapper servletContentWrapper = new HttpServletContentWrapper(httpRequest, httpResponse);
        ActionResponse<T> result = functionAction.createResponse(restModel, servletContentWrapper);
        if (result.isSuccessful()) {
            return ResponseFactory.createResponseFromAction(result);
        } else {
            throw ResponseFactory.createStatusException(result);
        }
    }
}
