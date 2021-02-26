import React from "react";
import { ValueContainer } from "react-select/src/components/containers";
import CellMissingDataFormat from "./CellMissingDataFormat";

const Container = (props) => {
    const {getValue, children} = props;
    const { length } = getValue();
    const error = <CellMissingDataFormat />;
    const hasError = getValue().find((value) => value.missing);
    if (length <= 5) {
        return (
            <ValueContainer {...props}>
                {children}
                {hasError && error}
            </ValueContainer>
        );
    }

    return (
        <ValueContainer {...props}>
            {!props.selectProps.menuIsOpen
            && `${length} Items selected`}
            {React.cloneElement(children[1])}
            {hasError && error}
        </ValueContainer>
    );
};

export default Container;
