import {forwardRef, useEffect, useImperativeHandle, useState} from 'react';
import type {
  AmisTemplateCrudMethods,
  AmisTemplateCrudProps,
  ColumnInfo,
  DatabaseInfo,
  DataType,
  OptionType,
  SchemaTableTypeTable,
  TableColumn,
  TableTypeTable
} from '../type.tsx';
import apiClient from '../apiClient.tsx';
import {Checkbox, Col, Modal, Row, Select, Table, type TableProps} from 'antd';
import {buildMainDetailTable, buildSingleTable, isNumberJavaSqlType} from './utils/build';
import ColumnRenderCheckBox from './components/ColumnRenderCheckBox';

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
      dataIndex: 'typeName'
    },
    {
      title: '长度',
      dataIndex: 'columnSize'
    },
    {
      title: '精度',
      dataIndex: 'decimalDigits'
    },
    {
      title: '备注',
      dataIndex: 'remarks'
    },
    {
      title: '主键',
      dataIndex: 'isPrimaryKey',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isPrimaryKey'}
            data={mainData}
            setData={setMainData}
          />
        );
      }
    },
    {
      title: '表格',
      dataIndex: 'isTableable',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isTableable'}
            data={mainData}
            setData={setMainData}
          />
        );
      }
    },
    {
      title: '查询',
      dataIndex: 'isSearchable',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isSearchable'}
            data={mainData}
            setData={setMainData}
          />
        );
      }
    },
    {
      title: '选择',
      dataIndex: 'isShowCheck',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isShowCheck'}
            data={mainData}
            setData={setMainData}
          />
        );
      }
    },
    {
      title: '新建',
      dataIndex: 'isInsertable',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isInsertable'}
            data={mainData}
            setData={setMainData}
          />
        );
      }
    },
    {
      title: '编辑',
      dataIndex: 'isUpdatable',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isUpdatable'}
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
      dataIndex: 'typeName'
    },
    {
      title: '长度',
      dataIndex: 'columnSize'
    },
    {
      title: '精度',
      dataIndex: 'decimalDigits'
    },
    {
      title: '备注',
      dataIndex: 'remarks'
    },
    {
      title: '主键',
      dataIndex: 'isPrimaryKey',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isPrimaryKey'}
            data={detailData}
            setData={setDetailData}
          />
        );
      }
    },
    {
      title: '表格',
      dataIndex: 'isTableable',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isTableable'}
            data={detailData}
            setData={setDetailData}
          />
        );
      }
    },
    {
      title: '选择',
      dataIndex: 'isShowCheck',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isShowCheck'}
            data={detailData}
            setData={setDetailData}
          />
        );
      }
    },
    {
      title: '新建',
      dataIndex: 'isInsertable',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isInsertable'}
            data={detailData}
            setData={setDetailData}
          />
        );
      }
    },
    {
      title: '编辑',
      dataIndex: 'isUpdatable',
      render: (value: boolean, _, index: number) => {
        return (
          <ColumnRenderCheckBox
            value={value}
            index={index}
            dataIndex={'isUpdatable'}
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
  const [detailColumnOptions, setDetailColumnOptions] = useState<OptionType[]>([]);

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
      const columns: ColumnInfo[] = tableColumn.columns;
      const primaryKeys: {columnName: string}[] = tableColumn.primaryKeys;
      const data: DataType[] = columns.map(column => {
        const isPrimaryKey = primaryKeys.some(
          primaryKey => primaryKey.columnName === column.columnName
        );
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
          isShowCheck: isPrimaryKey,
          isInsertable: !isPrimaryKey,
          isUpdatable: !isPrimaryKey
        };
      });
      setMainData(data);
      setMainColumnOptions(tableColumn.columns.map((item) => {
        return {
          value: item.columnName,
          label: item.columnName
        };
      }));
      props.setapiTemplateId(`MainDetailTable-${value}-${detailTable}`);
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
      const columns: ColumnInfo[] = tableColumn.columns;
      const primaryKeys: {columnName: string}[] = tableColumn.primaryKeys;
      const data: DataType[] = columns.map(column => {
        const isPrimaryKey = primaryKeys.some(
          primaryKey => primaryKey.columnName === column.columnName
        );
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
          isShowCheck: isPrimaryKey,
          isInsertable: !isPrimaryKey,
          isUpdatable: !isPrimaryKey
        };
      });
      setDetailData(data);
      setDetailColumnOptions(tableColumn.columns.map((item) => {
        return {
          value: item.columnName,
          label: item.columnName
        };
      }));
      props.setapiTemplateId(`MainDetailTable-${mainTable}-${value}`);
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
    const tableColumns: DataType[] =
      mainData.filter(item => item.isTableable) || [];
    const searchableColumns: DataType[] = tableColumns.filter(
      item => item.isSearchable
    );
    const showCheckColumns: DataType[] = mainData.filter(
      item => item.isShowCheck
    );
    const insertableColumns: DataType[] =
      mainData.filter(item => item.isInsertable) || [];
    const updatableColumns: DataType[] =
      mainData.filter(item => item.isUpdatable) || [];

    const detailTablePrimaryKey: DataType | undefined = detailData.find(
      item => item.isPrimaryKey
    );
    if (!detailTablePrimaryKey){
      Modal.error({title: '错误', content: '子表需要一个主键'});
      return;
    }
    const detailTableColumns: DataType[] =
      detailData.filter(item => item.isTableable) || [];
    const detailShowCheckColumns: DataType[] = detailData.filter(
      item => item.isShowCheck
    );
    const detailInsertableColumns: DataType[] = detailData.filter(
      item => item.isInsertable
    );
    const detailUpdatableColumns: DataType[] = detailData.filter(
      item => item.isUpdatable
    );

    const context = {
      type: 'page',
      style: {
        width: '100vw',
        height: '100vh'
      },
      body: buildMainDetailTable(
        mainTable,
        mainColumn,
        primaryKey,
        tableColumns,
        searchableColumns,
        showCheckColumns,
        insertableColumns,
        updatableColumns,
        detailTable,
        detailColumn,
        detailTablePrimaryKey,
        detailTableColumns,
        detailShowCheckColumns,
        detailInsertableColumns,
        detailUpdatableColumns
      )
    };

    return JSON.stringify(context, null, 2);
  };

  useImperativeHandle(ref, () => ({
    getContext
  }));

  return (
    <div style={{height: '100%'}}>
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
            onChange={value => setMainColumn(value)}
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
            onChange={value => setDetailColumn(value)}
            options={detailColumnOptions}
          />
        </Col>
      </Row>
      <Row style={{height: 'calc(50% - 17px)'}}>
        <Col span={24}>
          <Table
            columns={mainColumns}
            dataSource={mainData}
            pagination={false}
            scroll={{y: 'calc(50vh - 135px)'}}
          />
        </Col>
      </Row>
      <Row style={{height: 'calc(50% - 17px)'}}>
        <Col span={24}>
          <Table
            columns={detailColumns}
            dataSource={detailData}
            pagination={false}
            scroll={{y: 'calc(50vh - 135px)'}}
          />
        </Col>
      </Row>
    </div>
  );
});

export default MasterDetailTable;
