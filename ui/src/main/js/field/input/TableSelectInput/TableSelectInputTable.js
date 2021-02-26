import React, { useEffect, useRef, useState } from 'react';
import { BootstrapTable, SearchField, TableHeaderColumn } from 'react-bootstrap-table';
import PropTypes from 'prop-types';
import TableColumn from 'field/input/TableSelectInput/TableColumn';
import DataLoadingSpinner from 'field/input/TableSelectInput/DataLoadingSpinner';
import OkButton from 'field/input/TableSelectInput/OkButton';

const TableSelectInputTable = ({
    retrieveData, columns, defaultSortName, totalPageCount, useRowAsValue, progress, propagateData
}) => {
    const [data, setData] = useState([]);
    const [selectedData, setSelectedData] = useState([]);

    const [currentPage, setCurrentPage] = useState(1);
    const [currentPageSize, setCurrentPageSize] = useState(10);
    const [currentSearchTerm, setCurrentSearchTerm] = useState('');

    const tableRef = useRef();

    useEffect(() => {
        const foundData = retrieveData(currentPage, currentPageSize, currentSearchTerm);
        setData(foundData);
    }, [currentPage, currentPageSize, currentSearchTerm]);

    const onPageChange = (page, sizePerPage) => {
        setCurrentPage(page);
        setCurrentPageSize(sizePerPage);

        const newData = retrieveData(page, sizePerPage, currentSearchTerm);
        setData(newData);
    };

    const onSizePerPageListChange = (sizePerPage) => {
        setCurrentPageSize(sizePerPage);
        const newData = retrieveData(currentPage, sizePerPage, currentSearchTerm);
        setData(newData);
    };

    const onSearchChange = (searchTerm) => {
        setCurrentSearchTerm(searchTerm);
        setCurrentPage(1);
        const newData = retrieveData(1, currentPageSize, searchTerm);
        setData(newData);
    };

    const tableOptions = {
        noDataText: 'No data found',
        clearSearch: true,
        defaultSortName,
        defaultSortOrder: 'asc',

        searchDelayTime: 750,
        searchField: () => (
            <SearchField
                defaultValue={currentSearchTerm}
                placeholder="Search"
            />
        ),

        sizePerPage: currentPageSize,
        page: currentPage,
        onPageChange,
        onSizePerPageListChange,
        onSearchChange
    };

    const tableColumns = columns.map(({
        header, headerLabel, isKey, hidden
    }) => (
        <TableColumn isKey={isKey} header={header} headerLabel={headerLabel} isHidden={hidden} />
    ));

    const tableFetchInfo = {
        dataTotalSize: totalPageCount * currentPageSize
    };

    const createSelectedArray = (selectedArray, row, isSelected) => {
        const keyColumnHeader = columns.find((column) => column.isKey).header;
        const rowKeyValue = row[keyColumnHeader];

        if (isSelected) {
            const itemFound = selectedArray.find((selectedItem) => (useRowAsValue ? selectedItem[keyColumnHeader] === rowKeyValue : selectedItem === rowKeyValue));
            if (!itemFound) {
                const value = useRowAsValue ? row : rowKeyValue;
                selectedArray.push(value);
            }
        } else {
            const index = useRowAsValue ? selectedArray.findIndex((selection) => selection[keyColumnHeader] === rowKeyValue) : selectedArray.indexOf(rowKeyValue);
            if (index >= 0) {
                // if found, remove that element from selected array
                selectedArray.splice(index, 1);
            }
        }
    };

    const onRowSelectedAll = (isSelected, rows) => {
        if (rows) {
            const selected = Object.assign([], selectedData);
            rows.forEach((row) => {
                createSelectedArray(selected, row, isSelected);
            });
            setSelectedData(selected);
        } else {
            setSelectedData([]);
        }
    };

    const onRowSelected = (row, isSelected) => {
        const selected = Object.assign([], selectedData);
        createSelectedArray(selected, row, isSelected);
        setSelectedData(selected);
    };

    const selectRow = () => {
        const keyColumnHeader = columns.find((column) => column.isKey).header;

        const selectedRowData = (selectedData && useRowAsValue) ? selectedData.map((itemData) => itemData[keyColumnHeader]) : selectedData;
        return {
            mode: 'checkbox',
            clickToSelect: true,
            showOnlySelected: true,
            selected: selectedRowData,
            onSelect: onRowSelected,
            onSelectAll: onRowSelectedAll
        };
    };

    return (
        <div>
            <DataLoadingSpinner progress={progress} />
            {!progress
            && (
                <div>
                    <div>
                        <BootstrapTable
                            keyField="num"
                            ref={tableRef}
                            version="4"
                            data={data}
                            containerClass="table"
                            hover
                            condensed
                            selectRow={selectRow()}
                            options={tableOptions}
                            trClassName="tableRow"
                            headerContainerClass="scrollable"
                            bodyContainerClass="tableScrollableBody"
                            search
                            pagination
                            remote
                            fetchInfo={tableFetchInfo}
                        >
                            <TableColumn key="missingHeaderKey" dataField="missing" hidden>Missing Data</TableColumn>
                            {tableColumns}
                        </BootstrapTable>
                        <OkButton onOkClick={propagateData} />
                    </div>
                </div>
            )}
        </div>
    );
};

TableSelectInputTable.prototype = {
    columns: PropTypes.arrayOf(PropTypes.shape({
        header: PropTypes.string.isRequired,
        headerLabel: PropTypes.string.isRequired,
        isKey: PropTypes.bool.isRequired,
        hidden: PropTypes.bool.isRequired
    })).isRequired,
    retrieveData: PropTypes.func.isRequired,
    propagateData: PropTypes.func.isRequired,
    useRowAsValue: PropTypes.bool,
    defaultSortName: PropTypes.string,
    totalPageCount: PropTypes.number,
    progress: PropTypes.bool
};

TableSelectInputTable.defaultProps = {
    useRowAsValue: false,
    defaultSortName: 'id',
    totalPageCount: 1,
    progress: false
};

export default TableSelectInputTable;
