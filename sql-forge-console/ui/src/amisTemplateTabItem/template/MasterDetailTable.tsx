import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useState
} from 'react';
import type {
  AmisTemplateCrudMethods,
  AmisTemplateCrudProps,
  ColumnInfo,
  DatabaseInfo,
  DataType,
  JoinInfo,
  OptionType,
  SchemaTableTypeTable,
  TableColumn,
  TableTypeTable
} from '../../type.tsx';
import apiClient from '../../apiClient.tsx';
import {Col, Modal, Row, Select, Table, type TableProps} from 'antd';
import {
  buildMainDetailTable,
  getIndex,
  getPrimaryKey,
  isNumberJavaSqlType
} from '../utils/CrudBuild';
import ColumnRenderMutilCheckBox from '../components/ColumnRenderMutilCheckBox';
import ColumnRenderJoin from '../components/ColumnRenderJoin';
import ColumnRenderInput from '../components/ColumnRenderInput';

const MasterDetailTable = forwardRef<
  AmisTemplateCrudMethods,
  AmisTemplateCrudProps
>((props, ref) => {
  const [database, setDatabase] = useState<DatabaseInfo>();
  const [schema, setSchema] = useState<string>();
  const [schemaOptions, setSchemaOptions] = useState<OptionType[]>([]);
  const [mainTable, setMainTable] = useState<string>();
  const [detailTable, setDetailTable] = useState<string>();
  const [tableOptions, setTableOptions] = useState<OptionType[]>([]);
  const [mainData, setMainData] = useState<DataType[]>([]);
  const [detailData, setDetailData] = useState<DataType[]>([]);
  const mainColumns: TableProps<DataType>['columns'] = [
    {
      title: '列名',
      dataIndex: 'columnName'
    },
    {
      title: '类型',
      dataIndex: 'columnType',
      render: (_, row) => {
        return `${row.javaSqlType}(${row.columnSize}${
          row.decimalDigits ? ',' + row.decimalDigits : ''
        })`;
      }
    },
    {
      title: '备注',
      dataIndex: 'remarks',
      render: (value, _, index: number) => {
        return (
          <ColumnRenderInput
            value={value}
            index={index}
            dataIndex={'remarks'}
            data={mainData}
            setData={setMainData}
          />
        );
      }
    },
    {
      title: '主 表 查 选 新 改',
      dataIndex: 'isPrimaryKey',
      render: (_, row: DataType, index: number) => {
        return (
          <ColumnRenderMutilCheckBox
            row={row}
            index={index}
            data={mainData}
            setData={setMainData}
          />
        );
      }
    },
    {
      title: '关联',
      dataIndex: 'join',
      render: (value: JoinInfo, _, index: number) => {
        return (
          <ColumnRenderJoin
            value={value}
            index={index}
            data={mainData}
            setData={setMainData}
          />
        );
      }
    }
  ];
  const detailColumns: TableProps<DataType>['columns'] = [
    {
      title: '列名',
      dataIndex: 'columnName'
    },
    {
      title: '类型',
      dataIndex: 'columnType',
      render: (_, row) => {
        return `${row.javaSqlType}(${row.columnSize}${
          row.decimalDigits ? ',' + row.decimalDigits : ''
        })`;
      }
    },
    {
      title: '备注',
      dataIndex: 'remarks',
      render: (value, _, index: number) => {
        return (
          <ColumnRenderInput
            value={value}
            index={index}
            dataIndex={'remarks'}
            data={detailData}
            setData={setDetailData}
          />
        );
      }
    },
    {
      title: '主 表 选 新 改',
      dataIndex: 'isPrimaryKey',
      render: (_, row: DataType, index: number) => {
        return (
          <ColumnRenderMutilCheckBox
            row={row}
            index={index}
            data={detailData}
            setData={setDetailData}
            show={[
              'isPrimaryKey',
              'isTableable',
              'isShowCheck',
              'isInsertable',
              'isUpdatable'
            ]}
          />
        );
      }
    },
    {
      title: '关联',
      dataIndex: 'join',
      render: (value: JoinInfo, _, index: number) => {
        return (
          <ColumnRenderJoin
            value={value}
            index={index}
            data={detailData}
            setData={setDetailData}
          />
        );
      }
    }
  ];
  const [mainColumn, setMainColumn] = useState<string>();
  const [detailColumn, setDetailColumn] = useState<string>();
  const [mainColumnOptions, setMainColumnOptions] = useState<OptionType[]>([]);
  const [detailColumnOptions, setDetailColumnOptions] = useState<OptionType[]>(
    []
  );
  const [dictOptions, setDictOptions] = useState();

  const load = async () => {
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

    const result = await apiClient.post('sql/forge/api/json/select/SYS_DICT', {
      '@column': ['DICT_CODE', 'DICT_NAME']
    });

    setDictOptions(
      result.map(item => ({
        value: item.DICT_CODE,
        label: item.DICT_NAME
      }))
    );
  };

  useEffect(() => {
    load();
  }, []);

  const onSchemaChange = (value: string) => {
    setSchema(value);
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

  const onMainTableChange = (value: string) => {
    setMainTable(value);
    const tableColumn: TableColumn | undefined = database?.schemaTableTypeTables
      .find((item: SchemaTableTypeTable) => item.schema.tableSchema === schema)
      ?.tableTypeTables.find(
        (item: TableTypeTable) =>
          item.tableType === 'TABLE' ||
          item.tableType === 'BASE TABLE' ||
          item.tableType === 'table'
      )
      ?.tables.find((item: TableColumn) => item.table.tableName === value);
    if (tableColumn) {
      setMainColumnOptions(
        tableColumn.columns.map((item: ColumnInfo) => {
          return {
            value: item.columnName,
            label: item.columnName
          };
        })
      );
    } else {
      setMainColumnOptions([]);
    }
    setMainColumn(undefined);
  };

  const onMainTableColumnChange = (value: string) => {
    setMainColumn(value);
    const tableColumn: TableColumn | undefined = database?.schemaTableTypeTables
      .find((item: SchemaTableTypeTable) => item.schema.tableSchema === schema)
      ?.tableTypeTables.find(
        (item: TableTypeTable) =>
          item.tableType === 'TABLE' ||
          item.tableType === 'BASE TABLE' ||
          item.tableType === 'table'
      )
      ?.tables.find((item: TableColumn) => item.table.tableName === mainTable);

    if (tableColumn) {
      const columns: ColumnInfo[] = tableColumn.columns;
      const primaryKey = getPrimaryKey(tableColumn.primaryKeys);
      const uniqueIndex = getIndex(primaryKey, tableColumn.indexes);
      const data: DataType[] = columns.map(column => {
        const isPrimaryKey = primaryKey === column.columnName;
        return {
          columnName: column.columnName,
          dataType: column.dataType,
          javaSqlType: column.javaSqlType,
          typeName: column.typeName,
          columnSize: column.columnSize,
          decimalDigits: column.decimalDigits,
          remarks: column.remarks,
          isPrimaryKey: isPrimaryKey,
          isTableable: !isPrimaryKey,
          isSearchable:
            !isPrimaryKey && !isNumberJavaSqlType(column.javaSqlType),
          isShowCheck: uniqueIndex === column.columnName,
          isInsertable: !isPrimaryKey,
          isUpdatable: !isPrimaryKey
        };
      });
      setMainData(data);
    } else {
      setMainData([]);
    }
  };

  const onDetailTableChange = (value: string) => {
    setDetailTable(value);
    const tableColumn: TableColumn | undefined = database?.schemaTableTypeTables
      .find((item: SchemaTableTypeTable) => item.schema.tableSchema === schema)
      ?.tableTypeTables.find(
        (item: TableTypeTable) =>
          item.tableType === 'TABLE' ||
          item.tableType === 'BASE TABLE' ||
          item.tableType === 'table'
      )
      ?.tables.find((item: TableColumn) => item.table.tableName === value);

    if (tableColumn) {
      setDetailColumnOptions(
        tableColumn.columns.map((item: ColumnInfo) => {
          return {
            value: item.columnName,
            label: item.columnName
          };
        })
      );
    } else {
      setDetailColumnOptions([]);
    }
    setDetailColumn(undefined);
  };

  const onDetailTableColumnChange = (value: string) => {
    setDetailColumn(value);
    const tableColumn: TableColumn | undefined = database?.schemaTableTypeTables
      .find((item: SchemaTableTypeTable) => item.schema.tableSchema === schema)
      ?.tableTypeTables.find(
        (item: TableTypeTable) =>
          item.tableType === 'TABLE' ||
          item.tableType === 'BASE TABLE' ||
          item.tableType === 'table'
      )
      ?.tables.find(
        (item: TableColumn) => item.table.tableName === detailTable
      );

    if (tableColumn) {
      const columns: ColumnInfo[] = tableColumn.columns;
      const primaryKey = getPrimaryKey(tableColumn.primaryKeys);
      const uniqueIndex = getIndex(primaryKey, tableColumn.indexes, [value]);
      const data: DataType[] = columns.map(column => {
        const isPrimaryKey = primaryKey === column.columnName;
        return {
          columnName: column.columnName,
          dataType: column.dataType,
          javaSqlType: column.javaSqlType,
          typeName: column.typeName,
          columnSize: column.columnSize,
          decimalDigits: column.decimalDigits,
          remarks: column.remarks,
          isPrimaryKey: isPrimaryKey,
          isTableable: !isPrimaryKey,
          isSearchable: false,
          isShowCheck: uniqueIndex === column.columnName,
          isInsertable: !isPrimaryKey,
          isUpdatable: !isPrimaryKey
        };
      });
      setDetailData(data);
    } else {
      setDetailData([]);
    }
  };

  const getContext = () => {
    if (!mainTable || !detailTable || !mainColumn || !detailColumn) {
      Modal.error({title: '错误', content: '请先选择主子表和列'});
      return;
    }

    const primaryKey: DataType | undefined = mainData.find(
      item => item.isPrimaryKey
    );
    if (!primaryKey) {
      Modal.error({title: '错误', content: '主表需要一个主键'});
      return;
    }
    const detailTablePrimaryKey: DataType | undefined = detailData.find(
      item => item.isPrimaryKey
    );
    if (!detailTablePrimaryKey) {
      Modal.error({title: '错误', content: '子表需要一个主键'});
      return;
    }

    const context = {
      type: 'page',
      style: {
        width: '100vw',
        height: '100vh'
      },
      body: buildMainDetailTable(
        'crud_table',
        mainTable,
        mainColumn,
        mainData,
        'detail_table',
        detailTable,
        detailColumn,
        detailData
      )
    };

    return JSON.stringify(context, null, 2);
  };

  useImperativeHandle(ref, () => ({
    getContext,
    getApiTemplateId: () => `MainDetailTable-${mainTable}-${detailTable}`
  }));

  return (
    <>
      <Row style={{height: '33px'}}>
        <Col span={24}>
          <Select
            placeholder="请选择schema"
            value={schema}
            onChange={onSchemaChange}
            options={schemaOptions}
          />
          <Select
            placeholder="请选择主表"
            value={mainTable}
            onChange={onMainTableChange}
            options={tableOptions}
          />
          <Select
            placeholder="请选择关联主列"
            value={mainColumn}
            onChange={onMainTableColumnChange}
            options={mainColumnOptions}
          />
          <Select
            placeholder="请选择子表"
            value={detailTable}
            onChange={onDetailTableChange}
            options={tableOptions}
          />
          <Select
            placeholder="请选择关联子列"
            value={detailColumn}
            onChange={onDetailTableColumnChange}
            options={detailColumnOptions}
          />
        </Col>
      </Row>
      <Row style={{height: 'calc(50% - 50px)'}}>
        <Col span={24}>
          <Table
            columns={mainColumns}
            dataSource={mainData}
            pagination={false}
            scroll={{y: 'calc(50vh - 165px)'}}
          />
        </Col>
      </Row>
      <Row style={{height: 'calc(50% - 50px)'}}>
        <Col span={24}>
          <Table
            columns={detailColumns}
            dataSource={detailData}
            pagination={false}
            scroll={{y: 'calc(50vh - 165px)'}}
          />
        </Col>
      </Row>
    </>
  );
});

export default MasterDetailTable;
