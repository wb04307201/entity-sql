import {forwardRef, useEffect, useImperativeHandle, useState} from 'react';
import type {
    AmisTemplateCrudMethods, AmisTemplateCrudProps, ColumnInfo,
    DatabaseInfo,
    OptionType,
    SchemaTableTypeTable,
    TableColumn,
    TableTypeTable
} from "../type.tsx";
import apiClient from "../apiClient.tsx";
import {Checkbox, Col, Modal, Row, Select, Table, type TableProps} from "antd";

interface DataType extends ColumnInfo {
    isPrimaryKey: boolean;
    isSearchable: boolean;
    isTableable: boolean;
    isInsertable: boolean;
    isUpdatable: boolean;
}

const SingleTable = forwardRef<AmisTemplateCrudMethods, AmisTemplateCrudProps>((props, ref) => {

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
            title: '表格',
            dataIndex: 'isTableable',
            render: (value: boolean, _, index: number) => {
                return <Checkbox checked={value} onChange={(e) => {
                    const newData = [...data];
                    newData[index].isTableable = e.target.checked;
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
                    dataType: column.dataType,
                    javaSqlType: column.javaSqlType,
                    typeName: column.typeName,
                    columnSize: column.columnSize,
                    decimalDigits: column.decimalDigits,
                    remarks: column.remarks,
                    isPrimaryKey: isPrimaryKey,
                    isSearchable: !isPrimaryKey && !isNumberJavaSqlType(column.javaSqlType),
                    isTableable: !isPrimaryKey,
                    isInsertable: !isPrimaryKey,
                    isUpdatable: !isPrimaryKey
                }
            })
            setData(data)
            props.setapiTemplateId(`SingleTable-${value}`)
        } else {
            setData([])
        }
    }

    const isNumberJavaSqlType = (javaSqlType: string):boolean => {
        return javaSqlType === 'INTEGER' ||
            javaSqlType === 'BIGINT' ||
            javaSqlType === 'SMALLINT' ||
            javaSqlType === 'TINYINT' ||
            javaSqlType === 'NUMERIC' ||
            javaSqlType === 'DECIMAL' ||
            javaSqlType === 'FLOAT' ||
            javaSqlType === 'DOUBLE'
    }

    const getContext = () => {
        const primaryKey: DataType | undefined = data.find(item => item.isPrimaryKey)
        if (!primaryKey) {
            Modal.error({title: '错误', content: "需要一个主键"});
            return;
        }
        const tableableColumns: DataType[] = data.filter(item => item.isTableable) || []
        const searchableColumns: DataType[] = tableableColumns.filter(item => item.isSearchable)
        const insertableColumns: DataType[] = data.filter(item => item.isInsertable) || []
        const updatableColumns: DataType[] = data.filter(item => item.isUpdatable) || []

        const where = searchableColumns.map(item => {
            return {
                "column": item.columnName,
                "condition": "LIKE",
                "value": "${" + item.columnName + "}"
            }
        })

        const columns = [
                {
                    "name": primaryKey.columnName,
                    "label": primaryKey.columnName,
                    "hidden": true
                },
                ...tableableColumns.map( item => {
                    if (item.isSearchable){
                        if (isNumberJavaSqlType(item.javaSqlType)){
                            return {
                                "name": item.columnName,
                                "label": item.remarks ? item.remarks : item.columnName,
                                "searchable": {
                                    "type": "input-number",
                                    "name": item.columnName,
                                    "label": item.remarks ? item.remarks : item.columnName,
                                    "precision": item.decimalDigits,
                                    "placeholder": `输入${item.remarks ? item.remarks : item.columnName}`
                                },
                                "sortable": true,
                                "align": "right"
                            }
                        }else {
                            return {
                                "name": item.columnName,
                                "label": item.remarks ? item.remarks : item.columnName,
                                "searchable": {
                                    "type": "input-text",
                                    "name": item.columnName,
                                    "label": item.remarks ? item.remarks : item.columnName,
                                    "maxLength": item.columnSize,
                                    "placeholder": `输入${item.remarks ? item.remarks : item.columnName}`
                                },
                                "sortable": true
                            }
                        }
                    }else{
                        return {
                            "name": item.columnName,
                            "label": item.remarks ? item.remarks : item.columnName,
                            "sortable": true
                        }
                    }
                }),
            {
                "type": "operation",
                "label": "操作",
                "buttons": [{
                    "label": "修改",
                    "type": "button",
                    "icon": "fa fa-pen-to-square",
                    "actionType": "drawer",
                    "drawer": {
                        "title": "新增表单",
                        "body": {
                            "type": "form",
                            "initApi": {
                                "method": "post",
                                "url": `/sql/forge/api/json/select/${table}`,
                                "data": {
                                    "@where": [{
                                        "column": primaryKey.columnName,
                                        "condition": "EQ",
                                        "value": "${ID}"
                                    }]
                                },
                                "responseData": {
                                    "&": "${items | first}"
                                }
                            },
                            "api": {
                                "method": "post",
                                "url": `/sql/forge/api/json/update/${table}`,
                                "data": {
                                    "@set": "$$",
                                    "@where": [{
                                        "column": primaryKey.columnName,
                                        "condition": "EQ",
                                        "value": "${ID}"
                                    }]
                                }
                            },
                            "onEvent": {
                                "submitSucc": {
                                    "actions": [{
                                        "actionType": "reload",
                                        "componentId": "crud_table"
                                    }]
                                }
                            },
                            "body": updatableColumns.map(item => {
                                if (isNumberJavaSqlType(item.javaSqlType)){
                                    return {
                                        "type": "input-number",
                                        "name": `${item.columnName}`,
                                        "label": `${item.remarks ? item.remarks : item.columnName}`,
                                        "precision": item.decimalDigits,
                                    }
                                }else {
                                    return {
                                        "type": "input-text",
                                        "name": `${item.columnName}`,
                                        "label": `${item.remarks ? item.remarks : item.columnName}`,
                                        "maxLength": item.columnSize
                                    }
                                }
                            })
                        }
                    }
                },
                    {
                        "label": "删除",
                        "type": "button",
                        "icon": "fa fa-minus",
                        "actionType": "ajax",
                        "level": "danger",
                        "confirmText": "确认要删除？",
                        "api": {
                            "method": "post",
                            "url": `/sql/forge/api/json/delete/${table}`,
                            "data": {
                                "@where": [{
                                    "column": primaryKey.columnName,
                                    "condition": "EQ",
                                    "value": "${ID}"
                                }]
                            }
                        }
                    }
                ],
                "fixed": "right"
            }
        ]

        const context = {
            "type": "crud",
            "id":"crud_table",
            "api": {
                "method": "post",
                "url": `/sql/forge/api/json/selectPage/${table}`,
                "data": {
                    "@where": where,
                    "@order": ["${default(orderBy && orderDir ? (orderBy + ' ' + orderDir):'',undefined)}"],
                    "@page": {
                        "pageIndex": "${page - 1}",
                        "pageSize": "${perPage}"
                    }
                }
            },
            "headerToolbar": [
                {
                    "label": "新增",
                    "type": "button",
                    "icon": "fa fa-plus",
                    "level": "primary",
                    "actionType": "drawer",
                    "drawer": {
                        "title": "新增表单",
                        "body": {
                            "type": "form",
                            "api": {
                                "method": "post",
                                "url": `/sql/forge/api/json/insert/${table}`,
                                "data": {
                                    "@set": "$$"
                                }
                            },
                            "onEvent":{
                                "submitSucc": {
                                    "actions": [{
                                        "actionType": "reload",
                                        "componentId": "crud_table"
                                    }]
                                }
                            },
                            "body": [{
                                "type": "uuid",
                                "name": `${primaryKey.columnName}`
                            },
                                ...insertableColumns.map(item => {
                                    if (isNumberJavaSqlType(item.javaSqlType)){
                                        return {
                                            "type": "input-number",
                                            "name": `${item.columnName}`,
                                            "label": `${item.remarks ? item.remarks : item.columnName}`,
                                            "precision": item.decimalDigits,
                                        }
                                    }else {
                                        return {
                                            "type": "input-text",
                                            "name": `${item.columnName}`,
                                            "label": `${item.remarks ? item.remarks : item.columnName}`,
                                            "maxLength": item.columnSize
                                        }
                                    }
                                })
                            ]
                        }
                    }
                },
                "bulkActions",
                {
                    "type": "columns-toggler",
                    "align": "right"
                },
                {
                    "type": "drag-toggler",
                    "align": "right"
                },
                {
                    "type": "export-excel",
                    "label": "导出",
                    "icon": "fa fa-file-excel",
                    "api": {
                        "method": "post",
                        "url": `/sql/forge/api/json/select/${table}`,
                        "data": {
                            "@where": where
                        }
                    },
                    "align": "right"
                }
            ],
            "footerToolbar": [
                "statistics",
                {
                    "type": "pagination",
                    "layout": "total,perPage,pager,go"
                }
            ],
            "bulkActions": [{
                "label": "批量删除",
                "icon": "fa fa-trash",
                "actionType": "ajax",
                "api": {
                    "method": "post",
                    "url": `/sql/forge/api/json/delete/${table}`,
                    "data": {
                        "@where": [{
                            "column": primaryKey.columnName,
                            "condition": "IN",
                            "value": "${ids | split}"
                        }]
                    }
                },
                "confirmText": "确定要批量删除?"
            }],
            "keepItemSelectionOnPageChange": true,
            "labelTpl": "${USERNAME}",
            "autoFillHeight": true,
            "autoGenerateFilter": true,
            "showIndex": true,
            "primaryField": primaryKey.columnName,
            "columns": columns
        }

        return JSON.stringify(context, null, 2)
    };

    useImperativeHandle(ref, () => ({
        getContext
    }));

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
                <Table
                    columns={columns}
                    dataSource={data}
                    pagination={false}
                    scroll={{y: 'calc(100vh - 220px)'}}
                />
            </Col>
        </Row>
    </div>;
});

export default SingleTable;