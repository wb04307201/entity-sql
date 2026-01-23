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
} from '../../type.tsx';
import apiClient from '../../apiClient.tsx';
import {Col, Modal, Row, Select, Table, type TableProps} from 'antd';
import {
  buildSingleTable,
  getIndex,
  getPrimaryKey,
  isNumberJavaSqlType
} from '../utils/CrudBuild';
import ColumnRenderSelect from '../components/ColumnRenderSelect';
import ColumnRenderMutilCheckBox from '../components/ColumnRenderMutilCheckBox';

const SingleTable = forwardRef<AmisTemplateCrudMethods, AmisTemplateCrudProps>(
  (props, ref) => {
    const [database, setDatabase] = useState<DatabaseInfo>();
    const [schema, setSchema] = useState<string>();
    const [schemaOptions, setSchemaOptions] = useState<OptionType[]>([]);
    const [table, setTable] = useState<string>();
    const [tableOptions, setTableOptions] = useState<OptionType[]>([]);
    const [data, setData] = useState<DataType[]>([]);
    const columns: TableProps<DataType>['columns'] = [
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
        title: '主 表 查 选 新 改',
        dataIndex: 'isPrimaryKey',
        render: (_, row: DataType, index: number) => {
          return (
            <ColumnRenderMutilCheckBox
              row={row}
              index={index}
              data={data}
              setData={setData}
            />
          );
        }
      },
      {
        title: '字典',
        dataIndex: 'dict',
        render: (value: boolean, _, index: number) => {
          return (
            <ColumnRenderSelect
              value={value}
              index={index}
              dataIndex={'dict'}
              data={data}
              setData={setData}
              options={dictOptions}
            />
          );
        }
      }
    ];
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

      const result = await apiClient.post(
        'sql/forge/api/json/select/SYS_DICT',
        {
          '@column': ['DICT_CODE', 'DICT_NAME']
        }
      );

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
          .find(
            (item: SchemaTableTypeTable) => item.schema.tableSchema === value
          )
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
      setTable(value);
      const tableColumn: TableColumn | undefined =
        database?.schemaTableTypeTables
          .find(
            (item: SchemaTableTypeTable) => item.schema.tableSchema === schema
          )
          ?.tableTypeTables.find(
            (item: TableTypeTable) =>
              item.tableType === 'TABLE' ||
              item.tableType === 'BASE TABLE' ||
              item.tableType === 'table'
          )
          ?.tables.find((item: TableColumn) => item.table.tableName === value);

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
        setData(data);
        props.setapiTemplateId(`SingleTable-${value}`);
      } else {
        setData([]);
      }
    };

    const getContext = () => {
      if (!table) {
        Modal.error({title: '错误', content: '请先选择表'});
        return;
      }

      if (!data.find(item => item.isPrimaryKey)) {
        Modal.error({title: '错误', content: '需要一个主键'});
        return;
      }

      const context = {
        type: 'page',
        body: buildSingleTable(table, data)
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
              placeholder="请选择table"
              value={table}
              onChange={onTableChange}
              options={tableOptions}
            />
          </Col>
        </Row>
        <Row style={{height: 'calc(100% - 33px)'}}>
          <Col span={24}>
            <Table
              columns={columns}
              dataSource={data}
              pagination={false}
              scroll={{y: 'calc(100vh - 220px)'}}
            />
          </Col>
        </Row>
      </div>
    );
  }
);

export default SingleTable;
