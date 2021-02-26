import React from "react";
import MultiValue from "react-select/src/components/MultiValue";
import DescriptorOption from "../../../component/common/DescriptorOption";

const TypeLabel = (props) => {
    const { data } = props;
    const missingItem = (data.missing) ? { textDecoration: 'line-through' } : {};

    return (
        <MultiValue {...props}>
            <DescriptorOption style={missingItem} label={data.label} value={data.value} />
        </MultiValue>
    );
};

export default TypeLabel;
