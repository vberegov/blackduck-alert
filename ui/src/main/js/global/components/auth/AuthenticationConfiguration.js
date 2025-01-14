import React, { useState } from 'react';
import CommonGlobalConfigurationForm from 'global/CommonGlobalConfigurationForm';
import * as FieldModelUtilities from 'util/fieldModelUtilities';
import { CONTEXT_TYPE } from 'util/descriptorUtilities';
import * as PropTypes from 'prop-types';
import CommonGlobalConfiguration from 'global/CommonGlobalConfiguration';
import TextInput from 'field/input/TextInput';
import {
    AUTHENTICATION_INFO, AUTHENTICATION_LDAP_FIELD_KEYS, AUTHENTICATION_SAML_FIELD_KEYS, AUTHENTICATION_TEST_FIELD_KEYS
} from 'global/components/auth/AuthenticationModel';
import CheckboxInput from 'field/input/CheckboxInput';
import PasswordInput from 'field/input/PasswordInput';
import DynamicSelectInput from 'field/input/DynamicSelectInput';
import CollapsiblePane from 'component/common/CollapsiblePane';
import UploadFileButtonField from 'field/UploadFileButtonField';
import ReadOnlyField from 'field/ReadOnlyField';

const AuthenticationConfiguration = ({ csrfToken, readonly }) => {
    const [formData, setFormData] = useState(FieldModelUtilities.createEmptyFieldModel([], CONTEXT_TYPE.GLOBAL, AUTHENTICATION_INFO.key));
    const [errors, setErrors] = useState({});
    const [testFieldData, setTestFieldData] = useState({});

    const testFields = (
        <div>
            <h2>LDAP Configuration</h2>
            <TextInput
                key={AUTHENTICATION_TEST_FIELD_KEYS.username}
                name={AUTHENTICATION_TEST_FIELD_KEYS.username}
                label="User Name"
                description="The user name to test LDAP authentication; if LDAP authentication is enabled."
                readOnly={readonly}
                onChange={FieldModelUtilities.handleTestChange(testFieldData, setTestFieldData)}
                value={testFieldData[AUTHENTICATION_TEST_FIELD_KEYS.username]}
            />
            <PasswordInput
                key={AUTHENTICATION_TEST_FIELD_KEYS.password}
                name={AUTHENTICATION_TEST_FIELD_KEYS.password}
                label="Password"
                description="The password to test LDAP authentication; if LDAP authentication is enabled."
                readOnly={readonly}
                onChange={FieldModelUtilities.handleTestChange(testFieldData, setTestFieldData)}
                value={testFieldData[AUTHENTICATION_TEST_FIELD_KEYS.password]}
            />
            <h2>SAML Configuration</h2>
            <ReadOnlyField
                key={AUTHENTICATION_TEST_FIELD_KEYS.noInput}
                name={AUTHENTICATION_TEST_FIELD_KEYS.noInput}
                label="No Input Required"
                description="No input required here. SAML metadata fields will be tested by the server."
            />
        </div>
    );

    const authTypes = [
        { label: 'Simple', value: 'simple' },
        { label: 'None', value: 'none' },
        { label: 'Digest-MD5', value: 'digest' }
    ];

    const referralTypes = [
        { label: 'Ignore', value: 'ignore' },
        { label: 'Follow', value: 'follow' },
        { label: 'Throw', value: 'throw' }
    ];

    const hasLdapConfig = Object.keys(AUTHENTICATION_LDAP_FIELD_KEYS).some((key) => FieldModelUtilities.hasValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS[key]));
    const hasSamlConfig = Object.keys(AUTHENTICATION_SAML_FIELD_KEYS).some((key) => FieldModelUtilities.hasValue(formData, AUTHENTICATION_SAML_FIELD_KEYS[key]));

    return (
        <CommonGlobalConfiguration
            label={AUTHENTICATION_INFO.label}
            description="This page allows you to configure user authentication for Alert."
            lastUpdated={formData.lastUpdated}
        >
            <CommonGlobalConfigurationForm
                setErrors={(error) => setErrors(error)}
                formData={formData}
                setFormData={(data) => setFormData(data)}
                csrfToken={csrfToken}
                displayDelete={false}
                testFields={testFields}
                testFormData={testFieldData}
                setTestFormData={(values) => setTestFieldData(values)}
            >
                <CollapsiblePane
                    title="LDAP Configuration"
                    expanded={hasLdapConfig}
                >
                    <h2>LDAP Configuration</h2>
                    <CheckboxInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.enabled}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.enabled}
                        label="LDAP Enabled"
                        description="If true, Alert with attempt to authenticate using the LDAP configuration."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        isChecked={FieldModelUtilities.getFieldModelBooleanValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.enabled)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.enabled)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.enabled]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.server}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.server}
                        label="LDAP Server"
                        description="The URL of the LDAP server."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.server)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.server)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.server]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.managerDn}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.managerDn}
                        label="LDAP Manager DN"
                        description="The distinguished name of the LDAP manager."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.managerDn)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.managerDn)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.managerDn]}
                    />
                    <PasswordInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.managerPassword}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.managerPassword}
                        label="LDAP Manager Password"
                        description="The password of the LDAP manager."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.managerPassword)}
                        isSet={FieldModelUtilities.isFieldModelValueSet(formData, AUTHENTICATION_LDAP_FIELD_KEYS.managerPassword)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.managerPassword)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.managerPassword]}
                    />
                    <DynamicSelectInput
                        id={AUTHENTICATION_LDAP_FIELD_KEYS.authenticationType}
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.authenticationType}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.authenticationType}
                        label="LDAP Authentication Type"
                        description="The type of authentication required to connect to the LDAP server."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        options={authTypes}
                        value={FieldModelUtilities.getFieldModelValues(formData, AUTHENTICATION_LDAP_FIELD_KEYS.authenticationType)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.authenticationType)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.authenticationType]}
                    />
                    <DynamicSelectInput
                        id={AUTHENTICATION_LDAP_FIELD_KEYS.referral}
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.referral}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.referral}
                        label="LDAP Referral"
                        description="Set the method to handle referrals."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        options={referralTypes}
                        value={FieldModelUtilities.getFieldModelValues(formData, AUTHENTICATION_LDAP_FIELD_KEYS.referral)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.referral)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.referral]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.userSearchBase}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.userSearchBase}
                        label="LDAP User Search Base"
                        description="The part of the LDAP directory in which user searches should be done."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.userSearchBase)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.userSearchBase)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.userSearchBase]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.userSearchFilter}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.userSearchFilter}
                        label="LDAP User Search Filter"
                        description="The filter used to search for user membership."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.userSearchFilter)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.userSearchFilter)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.userSearchFilter]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.userDnPatterns}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.userDnPatterns}
                        label="LDAP User DN Patterns"
                        description="The pattern used used to supply a DN for the user. The pattern should be the name relative to the root DN."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.userDnPatterns)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.userDnPatterns)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.userDnPatterns]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.userAttributes}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.userAttributes}
                        label="LDAP User Attributes"
                        description="User attributes to retrieve for users."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.userAttributes)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.userAttributes)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.userAttributes]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchBase}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchBase}
                        label="LDAP Group Search Base"
                        description="The part of the LDAP directory in which group searches should be done."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchBase)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchBase)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchBase]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchFilter}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchFilter}
                        label="LDAP Group Search Filter"
                        description="The filter used to search for group membership."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValueOrDefault(formData, AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchFilter, 'uniqueMember={0}')}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchFilter)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.groupSearchFilter]}
                    />
                    <TextInput
                        key={AUTHENTICATION_LDAP_FIELD_KEYS.groupRoleAttribute}
                        name={AUTHENTICATION_LDAP_FIELD_KEYS.groupRoleAttribute}
                        label="LDAP Group Role Attribute"
                        description="The ID of the attribute which contains the role name for a group."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValueOrDefault(formData, AUTHENTICATION_LDAP_FIELD_KEYS.groupRoleAttribute)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_LDAP_FIELD_KEYS.groupRoleAttribute)}
                        errorValue={errors[AUTHENTICATION_LDAP_FIELD_KEYS.groupRoleAttribute]}
                    />
                </CollapsiblePane>
                <CollapsiblePane
                    title="SAML Configuration"
                    expanded={hasSamlConfig}
                >
                    <h2>SAML Configuration</h2>
                    <CheckboxInput
                        key={AUTHENTICATION_SAML_FIELD_KEYS.enabled}
                        name={AUTHENTICATION_SAML_FIELD_KEYS.enabled}
                        label="SAML Enabled"
                        description="If true, Alert will attempt to authenticate using the SAML configuration."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        isChecked={FieldModelUtilities.getFieldModelBooleanValue(formData, AUTHENTICATION_SAML_FIELD_KEYS.enabled)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_SAML_FIELD_KEYS.enabled)}
                        errorValue={errors[AUTHENTICATION_SAML_FIELD_KEYS.enabled]}
                    />
                    <CheckboxInput
                        key={AUTHENTICATION_SAML_FIELD_KEYS.forceAuth}
                        name={AUTHENTICATION_SAML_FIELD_KEYS.forceAuth}
                        label="Force Auth"
                        description="If true, the forceAuthn flag is set to true in the SAML request to the IDP. Please check the IDP if this is supported."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        isChecked={FieldModelUtilities.getFieldModelBooleanValue(formData, AUTHENTICATION_SAML_FIELD_KEYS.forceAuth)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_SAML_FIELD_KEYS.forceAuth)}
                        errorValue={errors[AUTHENTICATION_SAML_FIELD_KEYS.forceAuth]}
                    />
                    <TextInput
                        key={AUTHENTICATION_SAML_FIELD_KEYS.metadataUrl}
                        name={AUTHENTICATION_SAML_FIELD_KEYS.metadataUrl}
                        label="Identity Provider Metadata URL"
                        description="The Metadata URL from the external Identity Provider."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_SAML_FIELD_KEYS.metadataUrl)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_SAML_FIELD_KEYS.metadataUrl)}
                        errorValue={errors[AUTHENTICATION_SAML_FIELD_KEYS.metadataUrl]}
                    />
                    <UploadFileButtonField
                        key={AUTHENTICATION_SAML_FIELD_KEYS.metadataFile}
                        name={AUTHENTICATION_SAML_FIELD_KEYS.metadataFile}
                        fieldKey={AUTHENTICATION_SAML_FIELD_KEYS.metadataFile}
                        label="Identity Provider Metadata File"
                        description="The file to upload to the server containing the Metadata from the external Identity Provider."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        buttonLabel="Upload"
                        endpoint="/api/function/upload"
                        csrfToken={csrfToken}
                        capture=""
                        multiple={false}
                        accept={[
                            'text/xml',
                            'application/xml',
                            '.xml'
                        ]}
                        currentConfig={formData}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_SAML_FIELD_KEYS.metadataFile)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_SAML_FIELD_KEYS.metadataFile)}
                        errorValue={errors[AUTHENTICATION_SAML_FIELD_KEYS.metadataFile]}
                    />
                    <TextInput
                        key={AUTHENTICATION_SAML_FIELD_KEYS.entityId}
                        name={AUTHENTICATION_SAML_FIELD_KEYS.entityId}
                        label="Entity ID"
                        description="The Entity ID of the Service Provider. EX: This should be the Audience defined in Okta."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_SAML_FIELD_KEYS.entityId)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_SAML_FIELD_KEYS.entityId)}
                        errorValue={errors[AUTHENTICATION_SAML_FIELD_KEYS.entityId]}
                    />
                    <TextInput
                        key={AUTHENTICATION_SAML_FIELD_KEYS.entityBaseUrl}
                        name={AUTHENTICATION_SAML_FIELD_KEYS.entityBaseUrl}
                        label="Entity Base URL"
                        description="This should be the URL of the Alert system."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_SAML_FIELD_KEYS.entityBaseUrl)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_SAML_FIELD_KEYS.entityBaseUrl)}
                        errorValue={errors[AUTHENTICATION_SAML_FIELD_KEYS.entityBaseUrl]}
                    />
                    <TextInput
                        key={AUTHENTICATION_SAML_FIELD_KEYS.roleAttributeMapping}
                        name={AUTHENTICATION_SAML_FIELD_KEYS.roleAttributeMapping}
                        label="SAML Role Attribute Mapping"
                        description="The SAML attribute in the Attribute Statements that contains the roles for the user logged into Alert.  The roles contained in the Attribute Statement can be the role names defined in the mapping fields above."
                        readOnly={readonly}
                        onChange={FieldModelUtilities.handleChange(formData, setFormData)}
                        value={FieldModelUtilities.getFieldModelSingleValue(formData, AUTHENTICATION_SAML_FIELD_KEYS.roleAttributeMapping)}
                        errorName={FieldModelUtilities.createFieldModelErrorKey(AUTHENTICATION_SAML_FIELD_KEYS.roleAttributeMapping)}
                        errorValue={errors[AUTHENTICATION_SAML_FIELD_KEYS.roleAttributeMapping]}
                    />
                </CollapsiblePane>
            </CommonGlobalConfigurationForm>
        </CommonGlobalConfiguration>
    );
};

AuthenticationConfiguration.propTypes = {
    csrfToken: PropTypes.string.isRequired,
    // Pass this in for now while we have all descriptors in global state, otherwise retrieve this in this component
    readonly: PropTypes.bool
};

AuthenticationConfiguration.defaultProps = {
    readonly: false
};

export default AuthenticationConfiguration;
