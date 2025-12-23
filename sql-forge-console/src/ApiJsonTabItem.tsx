import {Button, Col, Flex, Input, Modal, Row, Table, Radio} from "antd";
import {useState} from "react";
import apiClient from "./apiClient.tsx";

interface ColumnType {
    title: string;
    dataIndex: string;
    key: string;
}

type DataType = Record<string, unknown>;

function ApiJsonTabItem() {

    const [tableName, setTableName] = useState("");
    const [type, setType] = useState("select");
    const [json, setJson] = useState("");
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

        apiClient.post(`/sql/forge/api/json/${type}/${tableName}`, {json: params})
            .json()
            .then(data => {
                if (Array.isArray(data) && data.length > 0) {
                    const row = data[0];
                    const columns = []
                    for (const key in row) {
                        columns.push({
                            title: key,
                            dataIndex: key,
                            key: key,
                        });
                    }
                    setColumns(columns)
                    setDataSource(data)
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
        <div style={{height: '100%'}}>
            <Row style={{height: 'calc(50% - 33px)'}}>
                <Col span={24}>
                    <Input.TextArea
                        value={json}
                        onChange={(e) => setJson(e.target.value)}
                        autoSize={false}
                        styles={{textarea: {height: '100%'}}}
                        style={{resize: "none"}}
                        placeholder="请输入json"
                    />
                </Col>
            </Row>
            <Row>
                <Col span={24}>
                    <Flex gap={"small"} style={{float: "right"}}>
                        <Input placeholder="表名" value={tableName} onChange={(e) => setTableName(e.target.value)}/>
                        <Radio.Group
                            value={type}
                            options={[
                                {value: "select", label: '查询'},
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
    "@group": null,
    "@distince": false
}`)
                                } else if (type === "insert") {
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
                    />
                </Col>
            </Row>
        </div>
    )
}

export default ApiJsonTabItem;