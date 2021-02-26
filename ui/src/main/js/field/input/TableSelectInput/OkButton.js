import React from 'react';
import * as PropTypes from 'prop-types';
import GeneralButton from 'field/input/GeneralButton';

const OkButton = ({ fieldKey, onOkClick }) => (
    <div>
        <GeneralButton
            id={`${fieldKey}-confirmation`}
            className="tableSelectOkButton"
            onClick={onOkClick}
        >
            OK
        </GeneralButton>
    </div>
);

OkButton.propTypes = {
    onOkClick: PropTypes.func.isRequired,
    fieldKey: PropTypes.string
};

OkButton.defaultProps = {
    fieldKey: 'ok'
};

export default OkButton;
