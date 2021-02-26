import React from 'react';
import PropTypes from 'prop-types';
import LabeledField from 'field/LabeledField';

function TextInput(props) {
    const {
        inputClass, id, readOnly, autoFocus, name, value, onChange, optionList
    } = props;

    let listId = null;
    let dataListOptions = null;
    if (optionList) {
        listId = 'listOptions';
        const dataListOptionObjects = optionList.map((currentOption) => (<option key={`${currentOption}Key`} value={currentOption} />));
        dataListOptions = (
            <datalist id={listId}>
                {dataListOptionObjects}
            </datalist>
        );
    }

    const field = (
        <div className="d-inline-flex flex-column p-2 col-sm-8">
            <input
                id={id}
                type="text"
                readOnly={readOnly}
                autoFocus={autoFocus}
                className={inputClass}
                name={name}
                value={value}
                onChange={onChange}
                list={listId}
            />
            {dataListOptions}
        </div>
    );

    return (
        <LabeledField field={field} {...props} />
    );
}

TextInput.propTypes = {
    id: PropTypes.string,
    isSet: PropTypes.bool,
    readOnly: PropTypes.bool,
    autoFocus: PropTypes.bool,
    inputClass: PropTypes.string,
    name: PropTypes.string,
    value: PropTypes.string,
    onChange: PropTypes.func,
    optionList: PropTypes.array
};

TextInput.defaultProps = {
    id: 'textInputId',
    isSet: false,
    value: '',
    readOnly: false,
    autoFocus: false,
    inputClass: 'form-control',
    name: 'name',
    onChange: () => true,
    optionList: null
};

export default TextInput;
