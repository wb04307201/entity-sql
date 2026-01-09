import {Button, Col, Flex, Input, Modal, Row, Table} from "antd";
import {useEffect, useState} from "react";
import apiClient from "./apiClient.tsx";
import Editor from "@monaco-editor/react";

interface ColumnType {
    title: string;
    dataIndex: string;
    key: string;
}

type DataType = Record<string, unknown>;

function ApiTemplateTabItem(props: {
    isCreate: boolean,
    apiTemplateId: string,
    reload: () => void,
    remove?: () => void
}) {

    const [isCreate] = useState(props.isCreate);
    const [apiTemplateId, setApiTemplateId] = useState(props.apiTemplateId);
    const [context, setContext] = useState<string | undefined>(undefined);
    const [json, setJson] = useState<string | undefined>(undefined);
    const [dataSource, setDataSource] = useState<DataType[]>([]);
    const [columns, setColumns] = useState<ColumnType[]>([]);

    useEffect(() => {
        if (!isCreate && apiTemplateId) {
            apiClient.get(`/sql/forge/api/template/${apiTemplateId}`)
                .then((data: unknown) => {
                    const apiTemplate = data as { context: string }
                    setContext(apiTemplate.context)
                })
        }
    }, []);

    const executeSave = () => {
        if (!apiTemplateId) {
            Modal.error({title: '错误', content: "请输入模板标识"});
            return;
        }
        if (!context) {
            Modal.error({title: '错误', content: "请输入模板内容"});
            return;
        }

        apiClient.post('/sql/forge/api/template', {id: apiTemplateId, context: context})
            .then((_) => {
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

        apiClient.post(`/sql/forge/api/template/execute/${apiTemplateId}`, params)
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
      <>
        <Row style={{height: 'calc(50% - 33px)'}} gutter={8}>
          <Col span={isCreate ? 24 : 16}>
            <Editor
              language="xml"
              value={context}
              onChange={(value: string | undefined) => setContext(value)}
            />
          </Col>
          {!isCreate && (
            <Col span={8}>
              <Editor
                language="json"
                value={json}
                onChange={(value: string | undefined) => setJson(value)}
              />
            </Col>
          )}
        </Row>
        <Row style={{height: '33px'}}>
          <Col span={24}>
            <Flex gap={'small'} style={{float: 'right'}}>
              <Input
                placeholder="模板标识"
                value={apiTemplateId}
                onChange={e => setApiTemplateId(e.target.value)}
                disabled={!isCreate}
              />
              {isCreate && (
                <Button
                  onClick={() => {
                    setApiTemplateId('ApiTemplate-test');
                    setContext(`SELECT * FROM users WHERE 1=1
<if test="name != null && name != ''">AND username = #{name}</if>
<if test="ids != null && !ids.isEmpty()"><foreach collection="ids" item="id" open="AND id IN (" separator="," close=")">#{id}</foreach></if>
<if test="(name == null || name == '') && (ids == null || ids.isEmpty()) ">AND 0=1</if>
ORDER BY username DESC`);
                  }}
                >
                  示例
                </Button>
              )}
              {!isCreate && <Button onClick={executeTest}>测试</Button>}
              <Button type="primary" onClick={executeSave}>
                保存
              </Button>
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
    );
}

export default ApiTemplateTabItem;