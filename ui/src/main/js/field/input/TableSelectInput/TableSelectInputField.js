import React, { useState } from 'react';
import PropTypes from 'prop-types';
import Select from 'react-select';
import LabeledField from 'field/LabeledField';
import GeneralButton from 'field/input/GeneralButton';
import ConfirmModal from 'component/common/ConfirmModal';
import TableSelectInputTable from 'field/input/TableSelectInput/TableSelectInputTable';
import * as FieldModelUtilities from 'util/fieldModelUtilities';
import { createNewConfigurationRequest } from 'util/configurationRequestBuilder';
// import TypeLabel from 'field/input/TableSelectInput/TypeLabel';
// import Container from 'field/input/TableSelectInput/Container';

const TableSelectInputField = (props) => {
    const [showClearConfirm, setShowClearConfirm] = useState();
    const [showTable, setShowTable] = useState();
    const [displayedData, setDisplayedData] = useState();
    const [progress, setProgress] = useState();
    const [totalPageCount, setTotalPageCount] = useState();

    const handleClearClick = () => {
        setDisplayedData([]);
        setShowClearConfirm(false);

        props.onChange({
            target: {
                name: props.fieldKey,
                value: []
            }
        });
    };

    const retrieveTableData = async (uiPageNumber, pageSize, searchTerm) => {
        setProgress(true);
        const {
            fieldKey, csrfToken, currentConfig, endpoint, requiredRelatedFields
        } = props;

        const newFieldModel = FieldModelUtilities.createFieldModelFromRequestedFields(currentConfig, requiredRelatedFields);
        const pageNumber = uiPageNumber ? uiPageNumber - 1 : 0;
        const encodedSearchTerm = encodeURIComponent(searchTerm);
        const apiUrl = `/alert${endpoint}/${fieldKey}?pageNumber=${pageNumber}&pageSize=${pageSize}&searchTerm=${encodedSearchTerm}`;
        const request = createNewConfigurationRequest(apiUrl, csrfToken, newFieldModel);
        return request.then((response) => {
            setProgress(false);
            if (response.ok) {
                return response.json().then((data) => {
                    const { options, totalPages } = data;
                    setTotalPageCount(totalPages);
                    return options;
                });
            }
            return [];
        });
    };

    const selectOnClick = (event) => {
        event.preventDefault();
        event.stopPropagation();
        setShowTable(true);
    };

    const components = {
        // MultiValue: TypeLabel,
        // ValueContainer: Container,
        DropdownIndicator: null,
        MultiValueRemove: () => <div />
    };
    const { fieldKey, readOnly } = props;
    const selectFieldId = `${fieldKey}-selectField`;
    const selectButtonId = `${fieldKey}_select`;
    const clearButtonId = `${fieldKey}_clear`;
    const confirmModalId = `${fieldKey}-confirmModal`;

    const table = (
        <div className="col-sm-8 d-inline-flex p-2">
            <div className="d-block typeAheadField">
                <Select
                    id={selectFieldId}
                    className="typeAheadField"
                    onChange={null}
                    options={[]}
                    isMulti
                    components={components}
                    noOptionsMessage={null}
                    isDisabled
                    clearable={false}
                    value={displayedData}
                />
                <div className="d-inline-flex float-right">
                    <GeneralButton
                        id={selectButtonId}
                        className="selectButton"
                        onClick={selectOnClick}
                        disabled={showTable || readOnly}
                    >
                        Select
                    </GeneralButton>
                    {displayedData && displayedData.length > 0
                    && (
                        <GeneralButton
                            id={clearButtonId}
                            className="selectClearButton"
                            onClick={setShowClearConfirm(true)}
                        >
                            Clear
                        </GeneralButton>
                    )}
                </div>
                {/*<ConfirmModal*/}
                {/*    id={confirmModalId}*/}
                {/*    title="Clear Input"*/}
                {/*    message="Are you sure you want to clear all selected items?"*/}
                {/*    showModal={showClearConfirm}*/}
                {/*    affirmativeAction={handleClearClick}*/}
                {/*    negativeAction={setShowClearConfirm(false)}*/}
                {/*/>*/}
            </div>
        </div>
    );

    const saveTableData = (selectedRows) => {
        setDisplayedData(selectedRows);
        setShowTable(false);
    };

    const { useRowAsValue, columns } = props;

    return (
        <div>
            <div>
                <LabeledField field={table} labelClass="col-sm-3" {...props} />
            </div>
            {showTable && (
                <TableSelectInputTable
                    useRowAsValue={useRowAsValue}
                    totalPageCount={totalPageCount}
                    retrieveData={retrieveTableData}
                    propogateData={saveTableData}
                    columns={columns}
                    progress={progress}
                />
            )}
        </div>
    );
};

TableSelectInputField.propTypes = {
    fieldKey: PropTypes.string.isRequired,
    endpoint: PropTypes.string.isRequired,
    csrfToken: PropTypes.string.isRequired,
    currentConfig: PropTypes.object.isRequired,
    columns: PropTypes.array.isRequired,
    requiredRelatedFields: PropTypes.array,
    searchable: PropTypes.bool,
    paged: PropTypes.bool,
    useRowAsValue: PropTypes.bool,
    readOnly: PropTypes.bool
};

TableSelectInputField.defaultProps = {
    requiredRelatedFields: [],
    searchable: true,
    paged: false,
    useRowAsValue: false,
    readOnly: false
};

export default TableSelectInputField;
