/*
 * channel
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.channel.jira.common.distribution.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synopsys.integration.alert.common.exception.AlertRuntimeException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.function.ThrowingSupplier;
import com.synopsys.integration.jira.common.model.response.CustomFieldCreationResponseModel;

public class JiraCustomFieldResolver {
    private static final String CUSTOM_FIELD_TYPE_STRING_VALUE = "string";
    private static final String CUSTOM_FIELD_TYPE_ARRAY_VALUE = "array";
    private static final String CUSTOM_FIELD_TYPE_OPTION_VALUE = "option";
    private static final String CUSTOM_FIELD_TYPE_PRIORITY_VALUE = "priority";
    private static final String CUSTOM_FIELD_TYPE_USER_VALUE = "user";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ThrowingSupplier<List<CustomFieldCreationResponseModel>, IntegrationException> retrieveAvailableFields;
    private final Map<String, CustomFieldCreationResponseModel> fieldCache;
    private boolean isCachePopulated;

    public JiraCustomFieldResolver(ThrowingSupplier<List<CustomFieldCreationResponseModel>, IntegrationException> retrieveAvailableFields) {
        this.retrieveAvailableFields = retrieveAvailableFields;
        this.fieldCache = new HashMap<>();
        this.isCachePopulated = false;
    }

    public final JiraResolvedCustomField resolveCustomField(JiraCustomFieldConfig jiraCustomFieldConfig) {
        CustomFieldDefinitionModel fieldDefinition = retrieveCustomFieldDefinition(jiraCustomFieldConfig);
        Object requestObject = convertValueToRequestObject(fieldDefinition, jiraCustomFieldConfig);
        return new JiraResolvedCustomField(fieldDefinition.getFieldId(), requestObject);
    }

    protected Optional<CustomFieldCreationResponseModel> retrieveFieldDefinition(String fieldName) {
        if (!isCachePopulated) {
            try {
                initializeCache();
            } catch (IntegrationException e) {
                logger.warn("No Jira Cloud user-visible fields found");
                return Optional.empty();
            }
        }
        return Optional.ofNullable(fieldCache.get(fieldName));
    }

    protected CustomFieldDefinitionModel retrieveCustomFieldDefinition(JiraCustomFieldConfig customFieldConfig) {
        String fieldName = customFieldConfig.getFieldName();
        CustomFieldCreationResponseModel fieldResponse = retrieveFieldDefinition(fieldName)
                                                             .orElseThrow(() -> new AlertRuntimeException(String.format("No custom field named '%s' existed", fieldName)));
        return new CustomFieldDefinitionModel(fieldResponse.getId(), fieldResponse.getSchema().getType());
    }

    protected Object convertValueToRequestObject(CustomFieldDefinitionModel fieldDefinition, JiraCustomFieldConfig jiraCustomFieldConfig) {
        String fieldType = fieldDefinition.getFieldType();
        String innerFieldValue = extractUsableInnerValue(jiraCustomFieldConfig);
        switch (fieldType) {
            case CUSTOM_FIELD_TYPE_STRING_VALUE:
                return innerFieldValue;
            case CUSTOM_FIELD_TYPE_ARRAY_VALUE:
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(innerFieldValue);
                return jsonArray;
            case CUSTOM_FIELD_TYPE_OPTION_VALUE:
                return createJsonObject("value", innerFieldValue);
            case CUSTOM_FIELD_TYPE_PRIORITY_VALUE:
                return createJsonObject("name", innerFieldValue);
            case CUSTOM_FIELD_TYPE_USER_VALUE:
                // "name" is used for Jira Server (ignored on Jira Cloud)
                JsonObject createUserObject = createJsonObject("name", innerFieldValue);
                // "accountId" is used for Jira Cloud (ignored on Jira Server)
                createUserObject.addProperty("accountId", innerFieldValue);
                // TODO consider separating this functionality depending on which Jira channel is being used
                return createUserObject;
            default:
                throw new AlertRuntimeException(String.format("Unsupported field type '%s' for field: %s", fieldType, jiraCustomFieldConfig.getFieldName()));
        }
    }

    private String extractUsableInnerValue(JiraCustomFieldConfig jiraCustomFieldConfig) {
        return jiraCustomFieldConfig.getFieldReplacementValue().orElseGet(jiraCustomFieldConfig::getFieldOriginalValue);
    }

    private void initializeCache() throws IntegrationException {
        List<CustomFieldCreationResponseModel> userVisibleFields = retrieveAvailableFields.get();
        for (CustomFieldCreationResponseModel fieldModel : userVisibleFields) {
            fieldCache.put(fieldModel.getName(), fieldModel);
        }
        isCachePopulated = true;
    }

    private JsonObject createJsonObject(String key, String value) {
        JsonObject objectWithName = new JsonObject();
        objectWithName.addProperty(key, value);
        return objectWithName;
    }

}
