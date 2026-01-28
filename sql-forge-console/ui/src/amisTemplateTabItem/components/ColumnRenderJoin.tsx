import React, {useState} from 'react';
import {Button, Modal, Select} from 'antd';
import {
  ColumnInfo,
  type DatabaseInfo,
  DictJoinInfo,
  JoinInfo,
  type OptionType,
  type SchemaTableTypeTable,
  type TableColumn,
  TableJoinInfo,
  type TableTypeTable
} from '../../type';
import {SettingOutlined} from '@ant-design/icons';
import apiClient from '../../apiClient';
import {SYS_DICT, DICT_CODE, DICT_NAME} from '../utils/CrudBuild';

const ColumnRenderJoin = (props: {
  value: JoinInfo;
  index: number;
  data: any[];
  setData: (data: any[]) => void;
}) => {
  const [show, setShow] = useState(false);
  const [joinInfo, setJoinInfo] = useState<JoinInfo>(props.value);
  const [dictOptions, setDictOptions] = useState();
  const [database, setDatabase] = useState<DatabaseInfo>();
  const [schemaOptions, setSchemaOptions] = useState<OptionType[]>([]);
  const [tableOptions, setTableOptions] = useState<OptionType[]>([]);
  const [columnOptions, setColumnOptions] = useState<OptionType[]>([]);

  const renderTitle = () => {
    return (
      <>
        {props.value?.joinType === 'dict' && (
          <div>字典</div>
        )}
        {props.value?.joinType === 'table' && (
            <div>表</div>
        )}
      </>
    );
  };

  const loadDictOptions = async () => {
    const data = await apiClient.post(`sql/forge/api/json/select/${SYS_DICT}`, {
      '@column': [`${DICT_CODE}`, `${DICT_NAME}`]
    });

    setDictOptions(
      data.map(item => ({
        value: item[DICT_CODE.toLowerCase()] || item[DICT_CODE.toUpperCase()],
        label: item[DICT_NAME.toLowerCase()] || item[DICT_NAME.toUpperCase()]
      }))
    );
  };

  const loadDatasource = async () => {
    const database: DatabaseInfo = await apiClient.get(
      '/sql/forge/api/databaseMetaData'
    );
    setDatabase(database);
    setSchemaOptions(
      database?.schemaTableTypeTables.map(item => ({
        value: item.schema.tableSchema,
        label: item.schema.tableSchema
      }))
    );
    setTableOptions([]);
  }

  const onSchemaChange = (value: string) => {
    setJoinInfo({
      ...joinInfo,
      schema: value,
      table: undefined,
      onColumn: undefined,
      selectColumn: undefined
    } as TableJoinInfo);
    setTableOptions(
      database?.schemaTableTypeTables
        .find((item: SchemaTableTypeTable) => item.schema.tableSchema === value)
        ?.tableTypeTables.find(
          (item: TableTypeTable) =>
            item.tableType === 'TABLE' ||
            item.tableType === 'BASE TABLE' ||
            item.tableType === 'table'
        )
        ?.tables.map((item: TableColumn) => ({
          value: item.table.tableName,
          label: item.table.tableName
        })) || []
    );
  };

  const onTableChange = (value: string) => {
    setJoinInfo({
      ...joinInfo,
      table: value,
      onColumn: undefined,
      selectColumn: undefined
    } as TableJoinInfo);
    const tableColumn: TableColumn | undefined = database?.schemaTableTypeTables
      .find((item: SchemaTableTypeTable) => item.schema.tableSchema === joinInfo?.schema)
      ?.tableTypeTables.find(
        (item: TableTypeTable) =>
          item.tableType === 'TABLE' ||
          item.tableType === 'BASE TABLE' ||
          item.tableType === 'table'
      )
      ?.tables.find((item: TableColumn) => item.table.tableName === value);
    if (tableColumn) {
      setColumnOptions(
        tableColumn.columns.map((item: ColumnInfo) => {
          return {
            value: item.columnName,
            label: item.columnName
          };
        })
      );
    } else {
      setColumnOptions([]);
    }
  };

  return (
    <div style={{display: 'flex'}}>
      <Button
        shape="circle"
        icon={<SettingOutlined />}
        size="small"
        onClick={() => {
          setShow(true);
        }}
      />
      {renderTitle()}
      <Modal
        title="关联信息"
        open={show}
        onCancel={() => setShow(false)}
        onOk={() => {
          const newData = [...props.data];
          newData[props.index].join = {...joinInfo};
          props.setData(newData);
          setShow(false);
        }}
      >
        <Select
          value={joinInfo?.joinType}
          options={[
            {value: 'dict', label: '字典'},
            {value: 'table', label: '表'}
          ]}
          onChange={async value => {
            if (value === 'dict') {
              setJoinInfo({joinType: 'dict'} as DictJoinInfo);
              await loadDictOptions();
            }
            if (value === 'table') {
              setJoinInfo({joinType: 'table'} as TableJoinInfo);
              await loadDatasource();
            }
          }}
        />
        {joinInfo?.joinType === 'dict' && (
          <Select
            placeholder="请选择字典"
            value={joinInfo?.dict}
            onChange={value => {
              setJoinInfo({...joinInfo, dict: value});
            }}
            options={dictOptions}
          />
        )}
        {joinInfo?.joinType === 'table' && (
          <>
            <Select
              placeholder="请选择schema"
              value={joinInfo?.schema}
              onChange={onSchemaChange}
              options={schemaOptions}
            />
            <Select
              placeholder="请选择表"
              value={joinInfo?.table}
              onChange={onTableChange}
              options={tableOptions}
            />
            <Select
              placeholder="请选择关联列"
              value={joinInfo?.onColumn}
              onChange={value => setJoinInfo({...joinInfo, onColumn: value})}
              options={columnOptions}
            />
            <Select
              placeholder="请选择显示列"
              value={joinInfo?.selectColumn}
              onChange={value =>
                setJoinInfo({...joinInfo, selectColumn: value})
              }
              options={columnOptions}
            />
          </>
        )}
      </Modal>
    </div>
  );
};

export default ColumnRenderJoin;
