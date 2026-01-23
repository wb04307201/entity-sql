import React from 'react';
import {Select} from 'antd';

const ColumnRenderSelect = (props: {
  value: any;
  index: number;
  dataIndex: string;
  data: any[];
  setData: (data: any[]) => void;
  options: {label: string; value: string}[];
}) => {
  return (
    <Select
      value={props.value}
      onChange={value => {
        const newData = [...props.data];
        newData[props.index][props.dataIndex] = value;
        props.setData(newData);
      }}
      options={props.options}
      allowClear={true}
    />
  );
};

export default ColumnRenderSelect;