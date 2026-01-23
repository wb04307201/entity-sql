import React from 'react';
import {Checkbox} from 'antd';
import {DataType} from '../../type';

const ColumnRenderMutilCheckBox = ({
  row,
  index,
  show = [
    'isPrimaryKey',
    'isTableable',
    'isSearchable',
    'isShowCheck',
    'isInsertable',
    'isUpdatable'
  ],
  data,
  setData
}: {
  row: DataType;
  index: number;
  show: string[];
  data: DataType[];
  setData: (data: DataType[]) => void;
}) => {
  return (
    <div style={{display: 'flex', alignItems: 'center', gap: '2px'}}>
      {show.some(item => item === 'isPrimaryKey') && (
        <Checkbox
          checked={row.isPrimaryKey}
          onChange={e => {
            const newData = [...data];
            newData[index].isPrimaryKey = e.target.checked;
            setData(newData);
          }}
        />
      )}
      {show.some(item => item === 'isTableable') && (
        <Checkbox
          checked={row.isTableable}
          onChange={e => {
            const newData = [...data];
            newData[index].isTableable = e.target.checked;
            setData(newData);
          }}
        />
      )}
      {show.some(item => item === 'isSearchable') && (
        <Checkbox
          checked={row.isSearchable}
          onChange={e => {
            const newData = [...data];
            newData[index].isSearchable = e.target.checked;
            setData(newData);
          }}
        />
      )}
      {show.some(item => item === 'isShowCheck') && (
        <Checkbox
          checked={row.isShowCheck}
          onChange={e => {
            const newData = [...data];
            newData[index].isShowCheck = e.target.checked;
            setData(newData);
          }}
        />
      )}
      {show.some(item => item === 'isInsertable') && (
        <Checkbox
          checked={row.isInsertable}
          onChange={e => {
            const newData = [...data];
            newData[index].isInsertable = e.target.checked;
            setData(newData);
          }}
        />
      )}
      {show.some(item => item === 'isUpdatable') && (
        <Checkbox
          checked={row.isUpdatable}
          onChange={e => {
            const newData = [...data];
            newData[index].isUpdatable = e.target.checked;
            setData(newData);
          }}
        />
      )}
    </div>
  );
};

export default ColumnRenderMutilCheckBox;
