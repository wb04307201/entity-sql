import {Button, Col, Input, Modal, Row, Table} from "antd";
import {useState} from "react";

interface ColumnType {
    title: string;
    dataIndex: string;
    key: string;
}

interface DataType {
    [key: string]: any;
}

function TabItemContent() {

    const [sql, setSql] = useState('');
    const [dataSource, setDataSource] = useState<DataType[]>([]);
    const [columns, setColumns] = useState<ColumnType[]>([]);

    const executeSql = () => {
        if (!sql) {
            return;
        }
        fetch('/sql/forge/execute',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    sql: sql
                })
            })
            .then(async response => {
                // 检查响应状态
                if (!response.ok) {
                    const errorData = await response.json(); // 先解析响应体
                    throw new Error(errorData?.error);
                }
                return response.json();
            })
            .then(data => {
                if (Array.isArray(data) && data.length > 0) {
                    const row =data[0];
                    const columns=[]
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
            .catch(e => {
                Modal.error({ title: '错误', content: e.message || '未知错误' });
            });
    }

    return (
        <div style={{height: '100%'}}>
            <Row style={{height: 'calc(50% - 32px)'}}>
                <Col span={24}>
                    <Input.TextArea
                        value={sql}
                        onChange={(e) => setSql(e.target.value)}
                        autoSize={false}
                        styles={{textarea: {height: '100%'}}}
                        style={{resize: "none"}}
                    />
                </Col>
            </Row>
            <Row>
                <Col span={24}>
                    <Button
                        type="primary"
                        style={{float: "right"}}
                        onClick={executeSql}
                    >执行</Button>
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

export default TabItemContent;