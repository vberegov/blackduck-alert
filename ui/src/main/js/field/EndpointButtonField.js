import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import GeneralButton from 'field/input/GeneralButton';
import FieldsPopUp from 'field/FieldsPopUp';
import LabeledField, { LabelFieldPropertyDefaults } from 'field/LabeledField';
import * as FieldModelUtilities from 'util/fieldModelUtilities';
import { createNewConfigurationRequest } from 'util/configurationRequestBuilder';
import StatusMessage from 'field/StatusMessage';
import * as HTTPErrorUtils from 'util/httpErrorUtilities';
import FieldsPanel from './FieldsPanel';

const EndpointButtonField = ({
    id,
    buttonLabel,
    csrfToken,
    currentConfig,
    description,
    endpoint,
    errorValue,
    fieldKey,
    fields,
    label,
    labelClass,
    name,
    onChange,
    readOnly,
    required,
    requiredRelatedFields,
    showDescriptionPlaceHolder,
    statusMessage,
    successBox,
    value
}) => {
    const [showModal, setShowModal] = useState(false);
    const [fieldError, setFieldError] = useState(errorValue);
    const [success, setSuccess] = useState(false);
    const [progress, setProgress] = useState(false);
    const [modalConfig, setModalConfig] = useState({});

    useEffect(() => {
        if (fieldError !== errorValue) {
            setFieldError(errorValue);
            setSuccess(false);
        }
    }, [errorValue]);

    const onSendClick = (event, popupData) => {
        setFieldError(errorValue);
        setProgress(true);
        setSuccess(false);

        const newFieldModel = FieldModelUtilities.createFieldModelFromRequestedFields(currentConfig, requiredRelatedFields);
        const mergedData = popupData ? FieldModelUtilities.combineFieldModels(newFieldModel, popupData) : newFieldModel;
        const request = createNewConfigurationRequest(`/alert${endpoint}/${fieldKey}`, csrfToken, mergedData);
        request.then((response) => {
            setProgress(false);
            if (response.ok) {
                const target = {
                    name: [fieldKey],
                    checked: true,
                    type: 'checkbox'
                };
                onChange({ target });
                setSuccess(true);
            } else {
                response.json()
                    .then((data) => {
                        const target = {
                            name: [fieldKey],
                            checked: false,
                            type: 'checkbox'
                        };
                        onChange({ target });
                        setFieldError(HTTPErrorUtils.createFieldError(data.message));
                    });
            }
        });
    };

    const flipShowModal = () => {
        if (fields.length > 0) {
            setShowModal(!showModal);
        } else {
            onSendClick({});
        }
    };
    return (
        <div>
            <LabeledField
                id={id}
                labelClass={labelClass}
                description={description}
                showDescriptionPlaceHolder={showDescriptionPlaceHolder}
                label={label}
                required={required}
                errorName={fieldKey}
                errorValue={fieldError}
            >
                <div className="d-inline-flex p-2 col-sm-8">
                    <GeneralButton
                        id={fieldKey}
                        onClick={flipShowModal}
                        disabled={readOnly}
                        performingAction={progress}
                    >
                        {buttonLabel}
                    </GeneralButton>
                    {successBox
                    && (
                        <div className="d-inline-flex p-2 checkbox">
                            <input
                                className="form-control"
                                id={`${fieldKey}-confirmation`}
                                type="checkbox"
                                name={name}
                                checked={value}
                                readOnly
                            />
                        </div>
                    )}
                    {success
                    && <StatusMessage id={`${fieldKey}-status-message`} actionMessage={statusMessage} />}

                </div>
            </LabeledField>
            <FieldsPopUp
                onCancel={flipShowModal}
                handleSubmit={onSendClick}
                title={buttonLabel}
                show={showModal}
                okLabel="Send"
            >
                <FieldsPanel
                    descriptorFields={fields}
                    currentConfig={currentConfig}
                    getCurrentState={() => modalConfig}
                    setStateFunction={setModalConfig}
                    csrfToken={csrfToken}
                    fieldErrors={{}}
                />
            </FieldsPopUp>
        </div>

    );
};

EndpointButtonField.propTypes = {
    id: PropTypes.string,
    buttonLabel: PropTypes.string.isRequired,
    csrfToken: PropTypes.string.isRequired,
    currentConfig: PropTypes.object.isRequired,
    endpoint: PropTypes.string.isRequired,
    fields: PropTypes.array,
    fieldKey: PropTypes.string.isRequired,
    name: PropTypes.string,
    onChange: PropTypes.func.isRequired,
    readOnly: PropTypes.bool,
    requiredRelatedFields: PropTypes.array,
    statusMessage: PropTypes.string,
    successBox: PropTypes.bool.isRequired,
    value: PropTypes.bool,
    description: PropTypes.string,
    errorValue: PropTypes.object,
    label: PropTypes.string.isRequired,
    labelClass: PropTypes.string,
    required: PropTypes.bool,
    showDescriptionPlaceHolder: PropTypes.bool
};

EndpointButtonField.defaultProps = {
    id: 'endpointButtonFieldId',
    fields: [],
    name: '',
    readOnly: false,
    requiredRelatedFields: [],
    statusMessage: 'Success',
    value: false,
    description: LabelFieldPropertyDefaults.DESCRIPTION_DEFAULT,
    errorValue: LabelFieldPropertyDefaults.ERROR_VALUE_DEFAULT,
    labelClass: LabelFieldPropertyDefaults.LABEL_CLASS_DEFAULT,
    required: LabelFieldPropertyDefaults.REQUIRED_DEFAULT,
    showDescriptionPlaceHolder: LabelFieldPropertyDefaults.SHOW_DESCRIPTION_PLACEHOLDER_DEFAULT
};

export default EndpointButtonField;
