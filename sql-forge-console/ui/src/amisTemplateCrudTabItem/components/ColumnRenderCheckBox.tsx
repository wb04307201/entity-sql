import React from 'react';
import {Checkbox} from 'antd';


const ColumnRenderCheckBox = (props: {
  value:any;
  index: number;
  dataIndex:string;
  data: any[];
  setData: (data: any[]) => void;
}) => {

  return (
    <Checkbox
      checked={props.value}
      onChange={e => {
        const newData = [...props.data];
        newData[props.index][props.dataIndex] = e.target.checked;
        props.setData(newData);
      }}
    />
  );
};

export default ColumnRenderCheckBox;