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
package com.synopsys.integration.alert.common.descriptor.config.field.validators;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class ValidationResult {

    private Collection<String> errors;

    public static ValidationResult success() {
        return new ValidationResult();
    }

    public static ValidationResult errors(Collection<String> errors) {
        return new ValidationResult(errors);
    }

    public static ValidationResult errors(String... errors) {
        return new ValidationResult(Arrays.asList(errors));
    }

    public static ValidationResult of(ValidationResult... validationResults) {
        Collection<String> validationErrors = Arrays.stream(validationResults)
                                                  .map(ValidationResult::getErrors)
                                                  .flatMap(Collection::stream)
                                                  .collect(Collectors.toList());
        return new ValidationResult(validationErrors);
    }

    private ValidationResult() {
        this.errors = List.of();
    }

    private ValidationResult(Collection<String> errors) {
        this.errors = errors;
    }

    public Collection<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String combineErrorMessages() {
        return StringUtils.join(errors, ", ");
    }
}