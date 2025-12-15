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

    const executeSql = () => {
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
            <Row style={{height: 'calc(50% - 32px)'}}>
                <Col span={24}>
                    <Input.TextArea
                        value={json}
                        onChange={(e) => setJson(e.target.value)}
                        autoSize={false}
                        styles={{textarea: {height: '100%'}}}
                        style={{resize: "none"}}
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
                                    setTableName("orders o")
                                    setJson(`{
    "@column": [
        "o.id AS order_id",
        "u.username",
        "p.name AS product_name",
        "p.price",
        "o.quantity",
        "(p.price * o.quantity) AS total"
    ],
    "@where": [
        {
            "column": "u.username",
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
            "on": "users u ON o.user_id = u.id"
        },
        {
            "type": "INNER_JOIN",
            "on": "products p ON o.product_id = p.id"
        }
    ],
    "@order": [
        "o.order_date"
    ],
    "@group": null,
    "@distince": false
}`)
                                }
                            }}
                        >示例</Button>
                        <Button
                            type="primary"
                            onClick={executeSql}
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