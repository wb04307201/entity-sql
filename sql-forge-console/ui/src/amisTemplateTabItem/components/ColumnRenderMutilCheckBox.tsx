import React from 'react';
import {Checkbox} from 'antd';
import {DataType} from '../../type';

const ColumnRenderMutilCheckBox = (props: {
  row: DataType;
  index: number;
  data: DataType[];
  setData: (data: DataType[]) => void;
}) => {
  return (
    <div style={{display: 'flex', alignItems: 'center', gap: '2px'}}>
      <Checkbox
        checked={props.row.isPrimaryKey}
        onChange={e => {
          const newData = [...props.data];
          newData[props.index].isPrimaryKey = e.target.checked;
          props.setData(newData);
        }}
      />
      <Checkbox
        checked={props.row.isTableable}
        onChange={e => {
          const newData = [...props.data];
          newData[props.index].isTableable = e.target.checked;
          props.setData(newData);
        }}
      />
      <Checkbox
        checked={props.row.isSearchable}
        onChange={e => {
          const newData = [...props.data];
          newData[props.index].isSearchable = e.target.checked;
          props.setData(newData);
        }}
      />
      <Checkbox
        checked={props.row.isShowCheck}
        onChange={e => {
          const newData = [...props.data];
          newData[props.index].isShowCheck = e.target.checked;
          props.setData(newData);
        }}
      />
      <Checkbox
        checked={props.row.isInsertable}
        onChange={e => {
          const newData = [...props.data];
          newData[props.index].isInsertable = e.target.checked;
          props.setData(newData);
        }}
      />
      <Checkbox
        checked={props.row.isUpdatable}
        onChange={e => {
          const newData = [...props.data];
          newData[props.index].isUpdatable = e.target.checked;
          props.setData(newData);
        }}
      />
    </div>
  );
};

export default ColumnRenderMutilCheckBox;
