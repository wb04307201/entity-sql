import {Button, Col, Flex, Input, Modal, Row, Table, Radio} from "antd";
import {useState} from "react";
import apiClient from "./apiClient.tsx";
import Editor from "@monaco-editor/react";

interface ColumnType {
    title: string;
    dataIndex: string;
    key: string;
}

type DataType = Record<string, unknown>;

function ApiJsonTabItem() {

    const [tableName, setTableName] = useState("");
    const [type, setType] = useState("select");
    const [json, setJson] = useState<string | undefined>(undefined);
    const [dataSource, setDataSource] = useState<DataType[]>([]);
    const [columns, setColumns] = useState<ColumnType[]>([]);

    const executeJson = () => {
        if (!json) {
            Modal.error({title: '错误', content: "请输入json"});
            return;
        }
        if (!tableName) {
            Modal.error({title: '错误', content: "请输入表名"});
            return;
        }
        let params;
        try {
            params = JSON.parse(json);
        } catch (error) {
            const typedError = error as { message: string };
            Modal.error({title: '错误', content: typedError.message});
            return;
        }

        apiClient.post(`/sql/forge/api/json/${type}/${tableName}`, params)
            .then((data: unknown) => {
                if(type == 'select') {
                    const res = data as DataType[] ;
                    const row = res[0];
                    const columns = []
                    for (const key in row) {
                        columns.push({
                            title: key,
                            dataIndex: key,
                            key: key,
                        });
                    }
                    setColumns(columns)
                    setDataSource(res)
                }else if(type == 'selectPage'){
                    const res = data as { rows: DataType[], total: number };
                    const row = res.rows[0];
                    const columns = []
                    for (const key in row) {
                        columns.push({
                            title: key,
                            dataIndex: key,
                            key: key,
                        });
                    }
                    setColumns(columns)
                    setDataSource(res.rows)
                } else {
                    setColumns([{
                        title: '',
                        dataIndex: 'key',
                        key: 'key',
                    }])
                    setDataSource([{key: data}])
                }
            })
    }

    return (
        <>
            <Row style={{height: 'calc(50% - 66px)'}}>
                <Col span={24}>
                    <Editor language="json" value={json}
                            onChange={(value: string | undefined) => setJson(value)}/>
                </Col>
            </Row>
            <Row style={{height: '66px'}}>
                <Col span={24}>
                    <Flex gap={"small"} style={{float: "right"}}>
                        <Input placeholder="表名" value={tableName} onChange={(e) => setTableName(e.target.value)}/>
                        <Radio.Group
                            value={type}
                            options={[
                                {value: "select", label: '查询'},
                                {value: "selectPage", label: '分页查询'},
                                {value: "insert", label: '插入'},
                                {value: "update", label: '更新'},
                                {value: "delete", label: '删除'},
                            ]}
                            onChange={(e) => setType(e.target.value)}
                        />
                        <Button
                            onClick={() => {
                                if (type === "select") {
                                    setTableName("orders")
                                    setJson(`{
    "@column": [
        "orders.id AS order_id",
        "users.username",
        "products.name AS product_name",
        "products.price",
        "orders.quantity",
        "(products.price * orders.quantity) AS total"
    ],
    "@where": [
        {
            "column": "users.username",
            "condition": "EQ",
            "value": "alice"
        }
    ],
    "@join": [
        {
            "type": "INNER_JOIN",
            "joinTable":"users",
            "on": "orders.user_id = users.id"
        },
        {
            "type": "INNER_JOIN",
            "joinTable":"products",
            "on": "orders.product_id = products.id"
        }
    ],
    "@order": [
        "orders.order_date"
    ],
    "@group": null,
    "@distince": false
}`)
                                } else if (type === "selectPage") {
                                    setTableName("orders")
                                    setJson(`{
    "@column": [
        "orders.id AS order_id",
        "users.username",
        "products.name AS product_name",
        "products.price",
        "orders.quantity",
        "(products.price * orders.quantity) AS total"
    ],
    "@where": [
        {
            "column": "users.username",
            "condition": "EQ",
            "value": "alice"
        }
    ],
    "@page": {
        "pageIndex": 0,
        "pageSize": 10
    },
    "@join": [
        {
            "type": "INNER_JOIN",
            "joinTable":"users",
            "on": "orders.user_id = users.id"
        },
        {
            "type": "INNER_JOIN",
            "joinTable":"products",
            "on": "orders.product_id = products.id"
        }
    ],
    "@order": [
        "orders.order_date"
    ],
    "@distince": false
}`)
                                }else if (type === "insert") {
                                    setTableName("users")
                                    setJson(`{
    "@set": [
        {
            "column": "id",
            "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
        },
        {
            "column": "username",
            "value": "wb04307201"
        },
        {
            "column": "email",
            "value": "wb04307201@gitee.com"
        }
    ],
    "@with_select": {
        "@column": null,
        "@where": [
            {
                "column": "id",
                "condition": "EQ",
                "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
            }
        ],
        "@page": null,
        "@join": null,
        "@order": null,
        "@group": null,
        "@distince": false
    }
}`)
                                } else if (type === "update") {
                                    setTableName("users")
                                    setJson(`{
    "@set": [
        {
            "column": "email",
            "value": "wb04307201@github.com"
        }
    ],
    "@where": [
        {
            "column": "id",
            "condition": "EQ",
            "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
        }
    ],
    "@with_select": {
        "@column": null,
        "@where": [
            {
                "column": "id",
                "condition": "EQ",
                "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
            }
        ],
        "@page": null,
        "@join": null,
        "@order": null,
        "@group": null,
        "@distince": false
    }
}`)
                                } else if (type === "delete") {
                                    setTableName("users")
                                    setJson(`{
    "@where": [
        {
            "column": "id",
            "condition": "EQ",
            "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
        }
    ],
    "@with_select": {
        "@column": null,
        "@where": [
            {
                "column": "id",
                "condition": "EQ",
                "value": "26a05ba3-913d-4085-a505-36d40021c8d1"
            }
        ],
        "@page": null,
        "@join": null,
        "@order": null,
        "@group": null,
        "@distince": false
    }
}`)
                                }
                            }}
                        >示例</Button>
                        <Button
                            type="primary"
                            onClick={executeJson}
                        >执行</Button>
                    </Flex>
                </Col>
            </Row>
            <Row style={{height: '50%'}}>
                <Col span={24}>
                    <Table
                        dataSource={dataSource}
                        columns={columns}
                        pagination={false}
                        scroll={{y: 'calc(50vh - 86px)'}}
                    />
                </Col>
            </Row>
        </>
    )
}

export default ApiJsonTabItem;