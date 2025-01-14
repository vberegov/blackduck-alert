import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import {
    BootstrapTable,
    DeleteButton,
    InsertButton,
    TableHeaderColumn
} from 'react-bootstrap-table';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import AutoRefresh from 'component/common/AutoRefresh';
import DescriptorLabel from 'component/common/DescriptorLabel';
import IconTableCellFormatter from 'component/common/IconTableCellFormatter';
import {
    fetchDistributionJobs,
    openJobDeleteModal,
    validateCurrentJobs
} from 'store/actions/distributions';
import * as DescriptorUtilities from 'util/descriptorUtilities';
import JobDeleteModal from 'distribution/JobDeleteModal';
import * as FieldModelUtilities from 'util/fieldModelUtilities';
import ConfigurationLabel from 'component/common/ConfigurationLabel';
import DistributionConfiguration, {
    KEY_CHANNEL_NAME,
    KEY_ENABLED,
    KEY_FREQUENCY,
    KEY_NAME,
    KEY_PROVIDER_NAME
} from 'dynamic/DistributionConfiguration';
import StatusMessage from 'field/StatusMessage';

/**
 * Selects className based on field value
 * @param fieldValue
 * @returns {string}
 */
function statusColumnClassNameFormat(fieldValue) {
    let className = '';
    if (fieldValue === 'Pending') {
        className = 'statusPending';
    } else if (fieldValue === 'Success') {
        className = 'statusSuccess';
    } else if (fieldValue === 'Failure') {
        className = 'statusFailure';
    }
    return `${className} tableCell`;
}

/**
 * Return type column data
 * @param cell
 * @returns {*}
 */
function frequencyColumnDataFormat(cell) {
    let cellText = '';
    if (cell === 'REAL_TIME') {
        cellText = 'Real Time';
    } else if (cell === 'DAILY') {
        cellText = 'Daily';
    }

    return (
        <div title={cellText}>
            {cellText}
        </div>
    );
}

function defaultColumnDataFormat(cell) {
    return (
        <div title={cell}>
            {cell}
        </div>
    );
}

const jobModificationState = {
    NEW: 'New',
    EDIT: 'Edit',
    COPY: 'Copy'
};

class Index extends Component {
    constructor(props) {
        super(props);
        this.createCustomModal = this.createCustomModal.bind(this);
        this.createCustomButtonGroup = this.createCustomButtonGroup.bind(this);
        this.cancelRowSelect = this.cancelRowSelect.bind(this);
        this.editButtonClicked = this.editButtonClicked.bind(this);
        this.editButtonClick = this.editButtonClick.bind(this);
        this.copyButtonClicked = this.copyButtonClicked.bind(this);
        this.copyButtonClick = this.copyButtonClick.bind(this);
        this.customJobConfigDeletionConfirm = this.customJobConfigDeletionConfirm.bind(this);
        this.reloadJobs = this.reloadJobs.bind(this);
        this.saveBtn = this.saveBtn.bind(this);
        this.onJobDeleteClose = this.onJobDeleteClose.bind(this);
        this.onJobDeleteSubmit = this.onJobDeleteSubmit.bind(this);
        this.descriptorDataFormat = this.descriptorDataFormat.bind(this);
        this.nameDataFormat = this.nameDataFormat.bind(this);
        this.getCurrentJobConfig = this.getCurrentJobConfig.bind(this);
        // Paging
        this.onPageChange = this.onPageChange.bind(this);
        this.onSizePerPageListChange = this.onSizePerPageListChange.bind(this);
        this.onSearchChange = this.onSearchChange.bind(this);
        this.onSortChange = this.onSortChange.bind(this);
        this.onRowClick = this.onRowClick.bind(this);

        this.state = {
            currentPage: 1,
            currentPageSize: 10,
            currentSearchTerm: '',
            currentRowSelected: null,
            jobsToDelete: [],
            showDeleteModal: false,
            nextDelete: null,
            modificationState: jobModificationState.NEW
        };
    }

    componentDidMount() {
        this.reloadJobs();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        const { currentPage, currentPageSize, currentSearchTerm } = this.state;
        if (prevState.currentPage !== currentPage || prevState.currentPageSize !== currentPageSize || prevState.currentSearchTerm !== currentSearchTerm) {
            this.reloadJobs();
        }
    }

    onJobDeleteSubmit() {
        this.state.nextDelete();
    }

    onJobDeleteClose() {
        this.setState({
            showDeleteModal: false,
            nextDelete: null,
            jobsToDelete: [],
            modificationState: jobModificationState.NEW
        });
    }

    getCurrentJobConfig() {
        const {
            currentPage, currentPageSize, currentSearchTerm, currentRowSelected, modificationState
        } = this.state;
        if (currentRowSelected != null) {
            const { id } = currentRowSelected;
            return (
                <DistributionConfiguration
                    handleCancel={this.cancelRowSelect}
                    onSave={this.saveBtn}
                    jobId={id}
                    onModalClose={() => {
                        this.props.fetchDistributionJobs(currentPage, currentPageSize, currentSearchTerm);
                        this.cancelRowSelect();
                    }}
                    jobModificationState={modificationState}
                />
            );
        }
        return null;
    }

    createCustomModal(onModalClose) {
        const { currentPage, currentPageSize, currentSearchTerm } = this.state;
        return (
            <DistributionConfiguration
                projects={this.state.projects}
                handleCancel={this.cancelRowSelect}
                onModalClose={() => {
                    this.props.fetchDistributionJobs(currentPage, currentPageSize, currentSearchTerm);
                    this.cancelRowSelect();
                    onModalClose();
                }}
                onSave={this.saveBtn}
            />
        );
    }

    saveBtn() {
        this.cancelRowSelect();
        this.reloadJobs();
    }

    reloadJobs() {
        const { currentPage, currentPageSize, currentSearchTerm } = this.state;
        this.props.fetchDistributionJobs(currentPage, currentPageSize, currentSearchTerm);
        this.props.validateCurrentJobsAction();
    }

    cancelRowSelect() {
        this.refs.table.cleanSelected();
        this.setState({
            currentRowSelected: null,
            modificationState: jobModificationState.NEW
        });
    }

    customJobConfigDeletionConfirm(next, dropRowKeys) {
        const { jobs } = this.props;
        const matchingJobs = jobs.filter((job) => dropRowKeys.includes(job.jobId));
        this.props.openJobDeleteModal();
        this.setState({
            showDeleteModal: true,
            nextDelete: next,
            jobsToDelete: matchingJobs,
            modificationState: jobModificationState.NEW
        });
    }

    editButtonClicked(currentRowSelected) {
        this.setState({
            currentRowSelected,
            modificationState: jobModificationState.EDIT
        });
    }

    editButtonClick(cell, row) {
        return (
            <IconTableCellFormatter
                id="distribution-edit-cell"
                handleButtonClicked={this.editButtonClicked}
                currentRowSelected={row}
                buttonIconName="pencil-alt"
                buttonText="Edit"
            />
        );
    }

    copyButtonClicked(currentRowSelected) {
        this.setState({
            currentRowSelected,
            modificationState: jobModificationState.COPY
        });
    }

    copyButtonClick(cell, row) {
        return (
            <IconTableCellFormatter
                id="distribution-copy-cell"
                handleButtonClicked={this.copyButtonClicked}
                currentRowSelected={row}
                buttonIconName="copy"
                buttonText="Copy"
            />
        );
    }

    enabledState(cell) {
        const icon = (cell == 'true') ? 'check' : 'times';
        const color = (cell == 'true') ? 'synopsysGreen' : 'synopsysRed';
        const className = `alert-icon ${color}`;

        return (
            <div className="btn btn-link jobIconButton" title={cell}>
                <FontAwesomeIcon icon={icon} className={className} size="lg" title={cell} />
            </div>
        );
    }

    createCustomButtonGroup(buttons) {
        const classes = 'btn btn-md btn-info react-bs-table-add-btn tableButton';
        const insertOnClick = buttons.insertBtn ? buttons.insertBtn.props.onClick : null;
        const deleteOnClick = buttons.deleteBtn ? buttons.deleteBtn.props.onClick : null;
        const refreshButton = !this.props.autoRefresh && (
            <button id="distribution-refresh-button" type="button" className={classes} onClick={this.reloadJobs}>
                <FontAwesomeIcon icon="sync" className="alert-icon" size="lg" />
                Refresh
            </button>
        );
        return (
            <div>
                {buttons.insertBtn
                && (
                    <InsertButton id="distribution-insert-button" className="addJobButton btn-md" onClick={insertOnClick}>
                        <FontAwesomeIcon icon="plus" className="alert-icon" size="lg" />
                        New
                    </InsertButton>
                )}
                {buttons.deleteBtn
                && (
                    <DeleteButton
                        id="distribution-delete-button"
                        className="deleteJobButton btn-md"
                        onClick={deleteOnClick}
                    >
                        <FontAwesomeIcon icon="trash" className="alert-icon" size="lg" />
                        Delete
                    </DeleteButton>
                )}
                {refreshButton}
            </div>
        );
    }

    nameDataFormat(cell, row) {
        const defaultValue = <div className="inline" title={cell}>{cell}</div>;
        const { jobsValidationResults } = this.props;
        if (jobsValidationResults && jobsValidationResults.length > 0) {
            const jobErrors = jobsValidationResults.filter((item) => item.id === row.id);
            if (jobErrors && jobErrors.length > 0) {
                return (
                    <span className="missingData" title={cell}>
                        <FontAwesomeIcon icon="exclamation-triangle" className="alert-icon" size="lg" title={cell} />
                        {defaultValue}
                    </span>
                );
            }
            return defaultValue;
        }
        return defaultValue;
    }

    descriptorDataFormat(cell) {
        const defaultValue = <div className="inline" title={cell}>{cell}</div>;
        const { descriptors } = this.props;
        if (descriptors) {
            const descriptor = DescriptorUtilities.findFirstDescriptorByNameAndContext(descriptors, cell, DescriptorUtilities.CONTEXT_TYPE.DISTRIBUTION);
            if (descriptor) {
                return (<DescriptorLabel keyPrefix="distribution-channel-icon" descriptor={descriptor} />);
            }
            return defaultValue;
        }
        return defaultValue;
    }

    createTableData(jobs) {
        const tableData = [];
        if (jobs) {
            jobs.forEach((job) => {
                if (job && job.fieldModels) {
                    const channelModel = job.fieldModels.find((model) => FieldModelUtilities.hasKey(model, KEY_CHANNEL_NAME));
                    const providerName = FieldModelUtilities.getFieldModelSingleValue(channelModel, KEY_PROVIDER_NAME);
                    const id = job.jobId;
                    const name = FieldModelUtilities.getFieldModelSingleValue(channelModel, KEY_NAME);
                    const distributionType = channelModel.descriptorName;
                    const frequency = FieldModelUtilities.getFieldModelSingleValue(channelModel, KEY_FREQUENCY);
                    const enabled = FieldModelUtilities.getFieldModelSingleValue(channelModel, KEY_ENABLED);
                    const lastRan = FieldModelUtilities.getFieldModelSingleValue(job, 'lastRan');
                    const status = FieldModelUtilities.getFieldModelSingleValue(job, 'status');
                    const entry = {
                        id,
                        name,
                        distributionType,
                        providerName,
                        frequency,
                        lastRan,
                        status,
                        enabled
                    };
                    tableData.push(entry);
                }
            });
        }
        return tableData;
    }

    checkJobPermissions(operation) {
        const { descriptors } = this.props;
        if (descriptors) {
            const descriptorList = DescriptorUtilities.findDescriptorByTypeAndContext(descriptors, DescriptorUtilities.DESCRIPTOR_TYPE.CHANNEL, DescriptorUtilities.CONTEXT_TYPE.DISTRIBUTION);
            if (descriptorList) {
                return descriptorList.some((descriptor) => DescriptorUtilities.isOperationAssigned(descriptor, operation));
            }
        }
        return false;
    }

    onPageChange(page, sizePerPage) {
        this.setState({
            currentPage: page,
            currentPageSize: sizePerPage
        });
    }

    onSizePerPageListChange(sizePerPage) {
        this.setState({
            currentPageSize: sizePerPage
        });
    }

    onSearchChange(searchTerm, colInfos, multiColumnSearch) {
        this.setState({
            currentSearchTerm: searchTerm,
            currentPage: 1
        });
    }

    onSortChange(sortName, sortOrder) {
        // Not currently supported
    }

    onRowClick(row) {
        // Do nothing for single-click (this will allow rows to be "checked" on single-click for bulk operations)
    }

    render() {
        const { currentPage, currentPageSize, currentRowSelected, showDeleteModal } = this.state;
        const { totalPageCount } = this.props;

        const tableData = this.createTableData(this.props.jobs);

        const jobTableOptions = {
            btnGroup: this.createCustomButtonGroup,
            noDataText: 'No jobs configured',
            clearSearch: true,
            searchDelayTime: 750,
            insertModal: this.createCustomModal,
            handleConfirmDeleteRow: this.customJobConfigDeletionConfirm,
            // defaultSortName: 'name',
            // defaultSortOrder: 'asc',
            onRowDoubleClick: this.editButtonClicked,

            // Page Variables
            sizePerPage: currentPageSize,
            page: currentPage,

            // Page Functions
            onPageChange: this.onPageChange,
            onSizePerPageList: this.onSizePerPageListChange,
            onSearchChange: this.onSearchChange,
            onSortChange: this.onSortChange,
            onRowClick: this.onRowClick
        };

        // Displays the # of pages for the table
        const tableFetchInfo = {
            dataTotalSize: totalPageCount * currentPageSize
        };

        const jobsSelectRowProp = {
            mode: 'checkbox',
            clickToSelect: true,
            bgColor(row, isSelect) {
                if (isSelect) {
                    return '#e8e8e8';
                }
                return null;
            }
        };

        const canCreate = this.checkJobPermissions(DescriptorUtilities.OPERATIONS.CREATE);
        const canDelete = this.checkJobPermissions(DescriptorUtilities.OPERATIONS.DELETE);

        // TODO: Refresh is enabled on insert, but disabled on edit and delete modals
        // TODO: Convert to TableDisplay to resolve autoRefresh issues
        const shouldRefresh = !currentRowSelected && !showDeleteModal;

        const content = (
            <div>
                {this.getCurrentJobConfig()}
                <StatusMessage
                    id="distribution-status-message"
                    actionMessage={null}
                    errorMessage={this.props.errorMessage}
                />
                <BootstrapTable
                    version="4"
                    hover
                    condensed
                    data={tableData}
                    containerClass="table"
                    insertRow={canCreate}
                    deleteRow={canDelete}
                    selectRow={jobsSelectRowProp}
                    options={jobTableOptions}
                    trClassName="tableRow"
                    headerContainerClass="scrollable"
                    bodyContainerClass="tableScrollableBody"
                    ref="table"

                    search
                    pagination
                    remote
                    fetchInfo={tableFetchInfo}
                >
                    <TableHeaderColumn dataField="id" isKey hidden>Job Id</TableHeaderColumn>
                    <TableHeaderColumn
                        dataField="name"
                        dataSort
                        columnClassName="tableCell"
                        dataFormat={this.nameDataFormat}
                    >
                        Distribution Job
                    </TableHeaderColumn>
                    <TableHeaderColumn
                        dataField="distributionType"
                        dataSort
                        columnClassName="tableCell"
                        dataFormat={this.descriptorDataFormat}
                    >
                        Type
                    </TableHeaderColumn>
                    <TableHeaderColumn
                        dataField="providerName"
                        dataSort
                        columnClassName="tableCell"
                        dataFormat={this.descriptorDataFormat}
                    >
                        Provider
                    </TableHeaderColumn>
                    <TableHeaderColumn
                        dataField="frequency"
                        dataSort
                        columnClassName="tableCell"
                        dataFormat={frequencyColumnDataFormat}
                    >
                        Frequency Type
                    </TableHeaderColumn>
                    <TableHeaderColumn
                        dataField="lastRan"
                        dataSort
                        columnClassName="tableCell"
                        dataFormat={defaultColumnDataFormat}
                    >
                        Last Run
                    </TableHeaderColumn>
                    <TableHeaderColumn
                        dataField="status"
                        dataSort
                        columnClassName={statusColumnClassNameFormat}
                        dataFormat={defaultColumnDataFormat}
                    >
                        Status
                    </TableHeaderColumn>
                    <TableHeaderColumn
                        dataField="enabled"
                        width="96"
                        dataSort
                        columnClassName="tableCell"
                        dataFormat={this.enabledState}
                    >
                        Enabled
                    </TableHeaderColumn>
                    <TableHeaderColumn
                        dataField=""
                        width="48"
                        columnClassName="tableCell"
                        dataFormat={this.editButtonClick}
                        thStyle={{ textAlign: 'center' }}
                    >
                        Edit
                    </TableHeaderColumn>
                    <TableHeaderColumn
                        dataField=""
                        width="48"
                        columnClassName="tableCell"
                        dataFormat={this.copyButtonClick}
                        thStyle={{ textAlign: 'center' }}
                    >
                        Copy
                    </TableHeaderColumn>
                </BootstrapTable>

                {this.props.inProgress
                && (
                    <div className="progressIcon">
                        <FontAwesomeIcon icon="spinner" className="alert-icon" size="lg" spin />
                    </div>
                )}
                <p name="jobConfigTableMessage">{this.props.jobConfigTableMessage}</p>
            </div>
        );
        return (
            <div>
                {canDelete && (
                    <JobDeleteModal
                        createTableData={this.createTableData}
                        onModalSubmit={this.onJobDeleteSubmit}
                        onModalClose={this.onJobDeleteClose}
                        typeColumnDataFormat={this.descriptorDataFormat}
                        providerColumnDataFormat={this.descriptorDataFormat}
                        frequencyColumnDataFormat={frequencyColumnDataFormat}
                        statusColumnClassNameFormat={statusColumnClassNameFormat}
                        jobs={this.state.jobsToDelete}
                        show={this.state.showDeleteModal}
                    />
                )}

                <ConfigurationLabel configurationName="Distribution" description="Create jobs from the channels Alert provides. Double click the row to edit that job." />
                <div className="pull-right">
                    <AutoRefresh startAutoReload={this.reloadJobs} autoRefresh={this.props.autoRefresh} isEnabled={shouldRefresh} />
                </div>
                {content}
            </div>
        );
    }
}

Index.propTypes = {
    openJobDeleteModal: PropTypes.func.isRequired,
    fetchDistributionJobs: PropTypes.func.isRequired,
    autoRefresh: PropTypes.bool,
    descriptors: PropTypes.arrayOf(PropTypes.object),
    inProgress: PropTypes.bool.isRequired,
    jobs: PropTypes.arrayOf(PropTypes.object).isRequired,
    totalPageCount: PropTypes.number,
    jobConfigTableMessage: PropTypes.string,
    jobsValidationResults: PropTypes.arrayOf(PropTypes.object),
    errorMessage: PropTypes.string
};

Index.defaultProps = {
    autoRefresh: true,
    descriptors: [],
    jobConfigTableMessage: '',
    jobsValidationResults: [],
    errorMessage: null
};

const mapStateToProps = (state) => ({
    autoRefresh: state.refresh.autoRefresh,
    descriptors: state.descriptors.items,
    inProgress: state.distributions.inProgress,
    jobs: state.distributions.jobs,
    totalPageCount: state.distributions.totalPages,
    jobConfigTableMessage: state.distributions.jobConfigTableMessage,
    jobsValidationResults: state.distributions.jobsValidationResult,
    errorMessage: state.distributions.error.message
});

const mapDispatchToProps = (dispatch) => ({
    openJobDeleteModal: () => dispatch(openJobDeleteModal()),
    fetchDistributionJobs: (pageOffset, pageLimit, searchTerm) => dispatch(fetchDistributionJobs(pageOffset, pageLimit, searchTerm)),
    validateCurrentJobsAction: () => dispatch(validateCurrentJobs())
});

export default connect(mapStateToProps, mapDispatchToProps)(Index);
