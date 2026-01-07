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
import {Col, Row, Select, Table, type TableProps} from "antd";

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
        const data = database?.schemaTableTypeTables
            .find((item: SchemaTableTypeTable) => item.schema.tableSchema === schema)?.tableTypeTables
            .find((item: TableTypeTable) => item.tableType === 'TABLE' || item.tableType === 'BASE TABLE' || item.tableType === 'table')?.tables
            .find((item: TableColumn) => item.table.tableName === value)?.columns
            .map((item: ColumnInfo) => {
                return {
                    columnName: item.columnName,
                    typeName: item.typeName,
                    columnSize: item.columnSize,
                    decimalDigits: item.decimalDigits,
                    remarks: item.remarks,
                    isPrimaryKey: false,
                    isSearchable: false,
                    isInsertable: false,
                    isUpdatable: false
                }
            }) || []
        setData(data)
    }

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
    ]

    return <>
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
        <Row>
            <Col span={24}>
                <Table columns={columns} dataSource={data}/>
            </Col>
        </Row>
    </>;
});

export default SingleTable;