import React from 'react';
import PropTypes from 'prop-types';
import LabeledField from 'field/LabeledField';

function TextArea(props) {
    const {
        inputClass, sizeClass, readOnly, name, value, onChange, id
    } = props;
    const divClasses = `${sizeClass} d-inline-flex`;

    const onChangeIfApplicable = (!readOnly) ? onChange : () => null;
    const field = (
        <div className={divClasses}>
            <textarea id={id} rows="8" cols="60" readOnly={readOnly} className={inputClass} name={name} value={value} onChange={onChangeIfApplicable} />
        </div>
    );

    return (
        <LabeledField field={field} {...props} />
    );
}

TextArea.propTypes = {
    id: PropTypes.string,
    readOnly: PropTypes.bool,
    inputClass: PropTypes.string,
    sizeClass: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.string,
    onChange: PropTypes.func
};

TextArea.defaultProps = {
    id: 'textAreaId',
    value: '',
    readOnly: false,
    inputClass: 'form-control',
    sizeClass: 'col-sm-8',
    name: 'name',
    onChange: () => true
};

export default TextArea;
