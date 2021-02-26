import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import PropTypes from 'prop-types';

const CellMissingDataFormat = ({ cell, missing }) => {
    const missingDataCell = (missing)
        ? (
            <span className="missingData">
                <FontAwesomeIcon icon="exclamation-triangle" className="alert-icon" size="lg" />
                {cell}
            </span>
        ) : cell;

    return missingDataCell;
};

CellMissingDataFormat.propTypes = {
    cell: PropTypes.object,
    missing: PropTypes.bool
};

CellMissingDataFormat.defaultProps = {
    cell: null,
    missing: false
};

export default CellMissingDataFormat;
