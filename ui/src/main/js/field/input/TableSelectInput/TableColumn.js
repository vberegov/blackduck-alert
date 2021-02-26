import React from 'react';
import CellDataFormat from 'field/input/TableSelectInput/CellDataFormat';
import { TableHeaderColumn } from 'react-bootstrap-table';
import * as PropTypes from 'prop-types';

const TableColumn = ({
    header, headerLabel, isKey, isHidden
}) => (
    <TableHeaderColumn
        key={header}
        dataField={header}
        isKey={isKey}
        dataSort
        columnClassName="tableCell"
        tdStyle={{ whiteSpace: 'normal' }}
        dataFormat={CellDataFormat}
        hidden={isHidden}
    >
        {headerLabel}
    </TableHeaderColumn>
);

TableColumn.prototype = {
    header: PropTypes.string.isRequired,
    headerLabel: PropTypes.string.isRequired,
    isKey: PropTypes.bool,
    isHidden: PropTypes.bool
};

TableColumn.defaultProps = {
    isKey: false,
    isHidden: false
};

export default TableColumn;
