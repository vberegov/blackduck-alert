/**
 * alert-common
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
package com.synopsys.integration.alert.common.action.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.synopsys.integration.alert.common.action.ActionResponse;
import com.synopsys.integration.alert.common.action.ValidationActionResponse;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.persistence.accessor.DescriptorAccessor;
import com.synopsys.integration.alert.common.persistence.model.RegisteredDescriptorModel;
import com.synopsys.integration.alert.common.rest.model.FieldModel;
import com.synopsys.integration.alert.common.rest.model.ValidationResponseModel;
import com.synopsys.integration.alert.common.security.authorization.AuthorizationManager;

public abstract class AbstractConfigResourceActions implements LongIdResourceActions<FieldModel>, TestAction<FieldModel>, ValidateAction<FieldModel> {
    private AuthorizationManager authorizationManager;
    private DescriptorAccessor descriptorAccessor;

    public AbstractConfigResourceActions(AuthorizationManager authorizationManager, DescriptorAccessor descriptorAccessor) {
        this.authorizationManager = authorizationManager;
        this.descriptorAccessor = descriptorAccessor;
    }

    protected abstract ActionResponse<FieldModel> createWithoutChecks(FieldModel resource);

    protected abstract ActionResponse<FieldModel> deleteWithoutChecks(Long id);

    protected abstract ActionResponse<List<FieldModel>> readAllWithoutChecks();

    protected abstract ActionResponse<List<FieldModel>> readAllByContextAndDescriptorWithoutChecks(String context, String descriptorName);

    protected abstract Optional<FieldModel> findFieldModel(Long id);

    protected abstract ValidationActionResponse testWithoutChecks(FieldModel resource);

    protected abstract ActionResponse<FieldModel> updateWithoutChecks(Long id, FieldModel resource);

    protected abstract ValidationActionResponse validateWithoutChecks(FieldModel resource);

    public final ActionResponse<List<FieldModel>> getAllByContextAndDescriptor(String context, String descriptorName) {
        if (!authorizationManager.hasReadPermission(context, descriptorName)) {
            return ActionResponse.createForbiddenResponse();
        }
        return readAllByContextAndDescriptorWithoutChecks(context, descriptorName);
    }

    @Override
    public final ActionResponse<FieldModel> create(FieldModel resource) {
        if (!authorizationManager.hasCreatePermission(resource.getContext(), resource.getDescriptorName())) {
            return ActionResponse.createForbiddenResponse();
        }
        ValidationActionResponse validationResponse = validateWithoutChecks(resource);
        if (validationResponse.isError()) {
            return new ActionResponse<>(validationResponse.getHttpStatus(), validationResponse.getMessage().orElse(null));
        }
        return createWithoutChecks(resource);
    }

    @Override
    public final ActionResponse<List<FieldModel>> getAll() {
        try {
            Set<String> descriptorNames = descriptorAccessor.getRegisteredDescriptors()
                                              .stream()
                                              .map(RegisteredDescriptorModel::getName)
                                              .collect(Collectors.toSet());
            if (!authorizationManager.anyReadPermission(List.of(ConfigContextEnum.DISTRIBUTION.name(), ConfigContextEnum.GLOBAL.name()), descriptorNames)) {
                return ActionResponse.createForbiddenResponse();
            }
            return readAllWithoutChecks();
        } catch (AlertException ex) {
            return new ActionResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error reading configurations: %s", ex.getMessage()));
        }
    }

    @Override
    public final ActionResponse<FieldModel> getOne(Long id) {
        Optional<FieldModel> fieldModel = findFieldModel(id);
        if (fieldModel.isPresent()) {
            FieldModel model = fieldModel.get();
            if (!authorizationManager.hasReadPermission(model.getContext(), model.getDescriptorName())) {
                return ActionResponse.createForbiddenResponse();
            }

            return new ActionResponse<>(HttpStatus.OK, model);
        }
        return new ActionResponse<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public final ActionResponse<FieldModel> update(Long id, FieldModel resource) {
        if (!authorizationManager.hasWritePermission(resource.getContext(), resource.getDescriptorName())) {
            return ActionResponse.createForbiddenResponse();
        }

        Optional<FieldModel> existingModel = findFieldModel(id);
        if (existingModel.isEmpty()) {
            return new ActionResponse<>(HttpStatus.NOT_FOUND);
        }

        ValidationActionResponse validationResponse = validateWithoutChecks(resource);
        if (validationResponse.isError()) {
            return new ActionResponse<>(validationResponse.getHttpStatus(), validationResponse.getMessage().orElse(null));
        }
        return updateWithoutChecks(id, resource);
    }

    @Override
    public final ActionResponse<FieldModel> delete(Long id) {
        Optional<FieldModel> fieldModel = findFieldModel(id);
        if (fieldModel.isPresent()) {
            FieldModel model = fieldModel.get();
            if (!authorizationManager.hasDeletePermission(model.getContext(), model.getDescriptorName())) {
                return ActionResponse.createForbiddenResponse();
            }
        }

        Optional<FieldModel> existingModel = findFieldModel(id);
        if (existingModel.isEmpty()) {
            return new ActionResponse<>(HttpStatus.NOT_FOUND);
        }
        return deleteWithoutChecks(id);
    }

    @Override
    public final ValidationActionResponse test(FieldModel resource) {
        if (!authorizationManager.hasExecutePermission(resource.getContext(), resource.getDescriptorName())) {
            ValidationResponseModel responseModel = ValidationResponseModel.withoutFieldStatuses(ActionResponse.FORBIDDEN_MESSAGE);
            return new ValidationActionResponse(HttpStatus.FORBIDDEN, responseModel);
        }
        ValidationActionResponse validationResponse = validateWithoutChecks(resource);
        if (validationResponse.isError()) {
            return ValidationActionResponse.createOKResponseWithContent(validationResponse);
        }
        ValidationActionResponse response = testWithoutChecks(resource);
        return ValidationActionResponse.createOKResponseWithContent(response);
    }

    @Override
    public final ValidationActionResponse validate(FieldModel resource) {
        if (!authorizationManager.hasExecutePermission(resource.getContext(), resource.getDescriptorName())) {
            ValidationResponseModel responseModel = ValidationResponseModel.withoutFieldStatuses(ActionResponse.FORBIDDEN_MESSAGE);
            return new ValidationActionResponse(HttpStatus.FORBIDDEN, responseModel);
        }
        ValidationActionResponse response = validateWithoutChecks(resource);
        return ValidationActionResponse.createOKResponseWithContent(response);
    }

    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    public DescriptorAccessor getDescriptorAccessor() {
        return descriptorAccessor;
    }
}