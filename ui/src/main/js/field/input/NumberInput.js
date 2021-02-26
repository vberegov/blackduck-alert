import React from 'react';
import PropTypes from 'prop-types';
import LabeledField from 'field/LabeledField';

function NumberInput(props) {
    const {
        readOnly, inputClass, id, name, value, onChange
    } = props;

    const onChangeIfApplicable = (!readOnly) ? onChange : () => null;
    const field = <div className="d-inline-flex flex-column p-2 col-sm-3"><input id={id} type="number" className={inputClass} readOnly={readOnly} name={name} value={value} onChange={onChangeIfApplicable} /></div>;
    return (
        <LabeledField field={field} {...props} />
    );
}

NumberInput.propTypes = {
    id: PropTypes.string,
    readOnly: PropTypes.bool,
    inputClass: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.string,
    onChange: PropTypes.func
};

NumberInput.defaultProps = {
    id: 'numberInputId',
    value: '',
    readOnly: false,
    inputClass: 'form-control',
    name: 'name',
    onChange: () => true
};

export default NumberInput;
