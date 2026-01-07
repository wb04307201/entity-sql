import {forwardRef, useEffect, useImperativeHandle, useState} from 'react';
import type {
    AmisTemplateCrudMethods, ColumnInfo,
    DatabaseInfo,
    OptionType,
    SchemaTableTypeTable,
    TableColumn,
    TableTypeTable
} from "../type.tsx";
import apiClient from "../apiClient.tsx";
import {Checkbox, Col, Row, Select, Table, type TableProps} from "antd";

interface DataType extends ColumnInfo {
    isPrimaryKey: boolean;
    isSearchable: boolean;
    isInsertable: boolean;
    isUpdatable: boolean;
}

const SingleTable = forwardRef<AmisTemplateCrudMethods>((_, ref) => {
    const getContext = () => {
        return "SingleTable"
    };

    useImperativeHandle(ref, () => ({
        getContext
    }));

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
            title: '主键',
            dataIndex: 'isPrimaryKey',
            render: (value: boolean, _, index: number) => {
                return <Checkbox checked={value} onChange={(e) => {
                    const newData = [...data];
                    newData[index].isPrimaryKey = e.target.checked;
                    setData(newData);
                }}/>
            }
        },
        {
            title: '查询',
            dataIndex: 'isSearchable',
            render: (value: boolean, _, index: number) => {
                return <Checkbox checked={value} onChange={(e) => {
                    const newData = [...data];
                    newData[index].isSearchable = e.target.checked;
                    setData(newData);
                }}/>
            }
        },
        {
            title: '新建',
            dataIndex: 'isInsertable',
            render: (value: boolean, _, index: number) => {
                return <Checkbox checked={value} onChange={(e) => {
                    const newData = [...data];
                    newData[index].isInsertable = e.target.checked;
                    setData(newData);
                }}/>
            }
        },
        {
            title: '编辑',
            dataIndex: 'isUpdatable',
            render: (value: boolean, _, index: number) => {
                return <Checkbox checked={value} onChange={(e) => {
                    const newData = [...data];
                    newData[index].isUpdatable = e.target.checked;
                    setData(newData);
                }}/>
            }
        }
    ]

    const load = async () => {
        const database: DatabaseInfo = await apiClient.get('/sql/forge/api/databaseMetaData')
        setDatabase(database)
        setSchemaOptions(database?.schemaTableTypeTables.map((item) => ({
            value: item.schema.tableSchema,
            label: item.schema.tableSchema
        })))
        setTableOptions([])
    }

    useEffect(() => {
        load()
    }, []);

    const onSchemaChange = (value: string) => {
        setSchema(value)
        setTableOptions(database?.schemaTableTypeTables
            .find((item: SchemaTableTypeTable) => item.schema.tableSchema === value)?.tableTypeTables
            .find((item: TableTypeTable) => item.tableType === 'TABLE' || item.tableType === 'BASE TABLE' || item.tableType === 'table')?.tables
            .map((item: TableColumn) => ({
                value: item.table.tableName,
                label: item.table.tableName
            })) || [])
    }

    const onTableChange = (value: string) => {
        setTable(value)
        const tableColumn: TableColumn | undefined = database?.schemaTableTypeTables
            .find((item: SchemaTableTypeTable) => item.schema.tableSchema === schema)?.tableTypeTables
            .find((item: TableTypeTable) => item.tableType === 'TABLE' || item.tableType === 'BASE TABLE' || item.tableType === 'table')?.tables
            .find((item: TableColumn) => item.table.tableName === value)

        if (tableColumn) {
            const columns: ColumnInfo[] = tableColumn.columns
            const primaryKeys: { columnName: string }[] = tableColumn.primaryKeys
            const data: DataType[] = columns.map((column) => {
                const isPrimaryKey = primaryKeys.some((primaryKey) => primaryKey.columnName === column.columnName);
                return {
                    columnName: column.columnName,
                    typeName: column.typeName,
                    columnSize: column.columnSize,
                    decimalDigits: column.decimalDigits,
                    remarks: column.remarks,
                    isPrimaryKey: isPrimaryKey,
                    isSearchable: !isPrimaryKey,
                    isInsertable: !isPrimaryKey,
                    isUpdatable: !isPrimaryKey
                }
            })
            setData(data)
        } else {
            setData([])
        }
    }


    return <div style={{height: '100%'}}>
        <Row>
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
                <Table columns={columns} dataSource={data} pagination={false} scroll={{y: 'calc(100vh - 220px)'}}/>
            </Col>
        </Row>
    </div>;
});

export default SingleTable;