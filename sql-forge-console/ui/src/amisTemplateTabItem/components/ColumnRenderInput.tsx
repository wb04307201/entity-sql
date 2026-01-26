import React from 'react';
import {Input} from 'antd';

const ColumnRenderInput = (props: {
  value: any;
  index: number;
  dataIndex: string;
  data: any[];
  setData: (data: any[]) => void;
}) => {
  return (
    <Input
      value={props.value}
      onChange={e => {
        const newData = [...props.data];
        newData[props.index][props.dataIndex] = e.target.value;
        props.setData(newData);
      }}
    />
  );
};

export default ColumnRenderInput;