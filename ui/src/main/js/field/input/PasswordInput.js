import React from 'react';
import PropTypes from 'prop-types';
import LabeledField from 'field/LabeledField';

function PasswordInput(props) {
    const {
        readOnly, isSet, inputClass, id, name, value, onChange
    } = props;

    const placeholderText = (isSet) ? '***********' : null;

    const onChangeIfApplicable = (!readOnly) ? onChange : () => null;
    const field = (
        <div className="d-inline-flex flex-column p-2 col-sm-8">
            <input id={id} type="password" readOnly={readOnly} className={inputClass} name={name} value={value} onChange={onChangeIfApplicable} placeholder={placeholderText} />
        </div>
    );
    return (
        <LabeledField field={field} {...props} />
    );
}

PasswordInput.propTypes = {
    id: PropTypes.string,
    isSet: PropTypes.bool,
    readOnly: PropTypes.bool,
    inputClass: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.string,
    onChange: PropTypes.func
};

PasswordInput.defaultProps = {
    id: 'passwordInputId',
    isSet: false,
    value: '',
    readOnly: false,
    inputClass: 'form-control',
    name: 'name',
    onChange: () => true
};

export default PasswordInput;
