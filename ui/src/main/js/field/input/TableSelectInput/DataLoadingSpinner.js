import React from 'react';
import PropTypes from 'prop-types';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const DataLoadingSpinner = ({ progress }) => {
    const spinningProgress = (progress)
        ? (
            <div className="progressIcon">
                <span className="fa-layers fa-fw">
                    <FontAwesomeIcon icon="spinner" className="alert-icon" size="lg" spin />
                </span>
            </div>
        )
        : null;

    return spinningProgress;
};

DataLoadingSpinner.prototype = {
    inProgress: PropTypes.bool.isRequired
};

export default DataLoadingSpinner;
