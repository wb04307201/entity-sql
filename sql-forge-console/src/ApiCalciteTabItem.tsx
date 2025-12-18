import {Button, Col, Flex, Input, Modal, Row, Table} from "antd";
import {useEffect, useState} from "react";
import apiClient from "./apiClient.tsx";

interface ColumnType {
    title: string;
    dataIndex: string;
    key: string;
}

type DataType = Record<string, unknown>;

function ApiCalciteTabItem(props: {
    isCreate: boolean,
    apiTemplateId: string,
    reload: () => void,
    remove?: () => void
}) {

    const [isCreate] = useState(props.isCreate);
    const [apiTemplateId, setApiTemplateId] = useState(props.apiTemplateId);
    const [context, setContext] = useState("");
    const [json, setJson] = useState("");
    const [dataSource, setDataSource] = useState<DataType[]>([]);
    const [columns, setColumns] = useState<ColumnType[]>([]);

    useEffect(() => {
        if (!isCreate && apiTemplateId) {
            apiClient.get(`/sql/forge/api/calcite/${apiTemplateId}`)
                .json()
                .then((data: unknown) => {
                    const apiTemplate = data as { context: string }
                    setContext(apiTemplate.context)
                })
        }
    }, []);

    const executeSave = () => {
        if (!apiTemplateId) {
            Modal.error({title: '错误', content: "请输入模板标识"});
        }
        if (!context) {
            Modal.error({title: '错误', content: "请输入模板内容"});
        }

        apiClient.post('/sql/forge/api/calcite', {json: {id: apiTemplateId, context: context}})
            .json()
            .then((_data) => {
                props.reload && props.reload()
                props.remove && props.remove()
            })
    }

    const executeTest = () => {
        if (!json) {
            Modal.error({title: '错误', content: "请输入json"});
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

        apiClient.post(`/sql/forge/api/calcite/execute/${apiTemplateId}`, {json: params})
            .json()
            .then((data) => {
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
            <Row style={{height: 'calc(50% - 32px)'}} gutter={8}>
                <Col span={isCreate ? 24 : 16}>
                    <Input.TextArea
                        wrap="soft"
                        value={context}
                        onChange={(e) => setContext(e.target.value)}
                        autoSize={false}
                        styles={{textarea: {height: '100%'}}}
                        style={{resize: "none"}}
                        placeholder="请输入模板内容"
                    />
                </Col>
                {
                    !isCreate && (
                        <Col span={8}>
                            <Input.TextArea
                                value={json}
                                onChange={(e) => setJson(e.target.value)}
                                autoSize={false}
                                styles={{textarea: {height: '100%'}}}
                                style={{resize: "none"}}
                                placeholder="请输入json"
                            />
                        </Col>
                    )
                }
            </Row>
            <Row>
                <Col span={24}>
                    <Flex gap={"small"} style={{float: "right"}}>
                        <Input placeholder="模板标识" value={apiTemplateId}
                               onChange={(e) => setApiTemplateId(e.target.value)}
                               disabled={!isCreate}
                        />
                        {
                            isCreate && (
                                <Button
                                    onClick={() => {
                                        setContext(`SELECT * FROM users WHERE 1=1<if test="name != null && name != ''"> AND username = #{name}</if><if test="ids != null && !ids.isEmpty()"><foreach collection="ids" item="id" open=" AND id IN (" separator="," close=")">#{id}</foreach></if><if test="(name == null || name == '') && (ids == null || ids.isEmpty()) "> AND 0=1</if> ORDER BY username DESC`)
                                    }}
                                >示例</Button>
                            )
                        }
                        {
                            !isCreate && (
                                <Button
                                    onClick={executeTest}
                                >测试</Button>
                            )
                        }
                        <Button
                            type="primary"
                            onClick={executeSave}
                        >保存</Button>
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

export default ApiCalciteTabItem;