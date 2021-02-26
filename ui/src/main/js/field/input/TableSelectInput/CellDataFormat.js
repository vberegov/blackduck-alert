import React from 'react';
import CellMissingDataFormat from 'field/input/TableSelectInput/CellMissingDataFormat';

const CellDataFormat = (cell, row) => {
    const title = (cell) ? cell.toString() : null;
    return (
        <div title={title}>
            {' '}
            <CellMissingDataFormat missing={row.missing && cell && cell !== ''} cell={cell} />
            {' '}
        </div>
    );
};

export default CellDataFormat;
