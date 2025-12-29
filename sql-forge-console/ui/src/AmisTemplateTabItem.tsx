import {Button, Col, Flex, Input, Modal, Row} from "antd";
import {useEffect, useState} from "react";
import apiClient from "./apiClient.tsx";
function AmisTemplateTabItem(props: {
    isCreate: boolean,
    apiTemplateId: string,
    reload: () => void,
    remove?: () => void
}) {

    const [isCreate] = useState(props.isCreate);
    const [apiTemplateId, setApiTemplateId] = useState(props.apiTemplateId);
    const [context, setContext] = useState("");

    useEffect(() => {
        if (!isCreate && apiTemplateId) {
            apiClient.get(`/sql/forge/amis/template/${apiTemplateId}`)
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

        try {
            JSON.parse(context);
        } catch (error) {
            const typedError = error as { message: string };
            Modal.error({title: '错误', content: typedError.message});
            return;
        }

        apiClient.post('/sql/forge/amis/template', {id: apiTemplateId, context: context})
            .then((_) => {
                props.reload && props.reload()
                props.remove && props.remove()
            })
    }

    const executeView = () => {
        console.log("executeView")
    }

    return (
        <div style={{height: '100%'}}>
            <Row style={{height: 'calc(100% - 33px)'}} gutter={8}>
                <Col span={24}>
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
                                        setContext(`{
  "type": "page",
  "body": {
    "type": "form",
    "body": [{
        "type": "input-text",
        "name": "name",
        "label": "姓名"
      },
      {
        "name": "email",
        "type": "input-email",
        "label": "邮箱"
      },
      {
        "name": "color",
        "type": "input-color",
        "label": "color"
      },
      {
        "type": "editor",
        "name": "editor",
        "label": "编辑器"
      }
    ]
  }
}`)
                                    }}
                                >示例</Button>
                            )
                        }
                        {
                            !isCreate && (
                                <Button
                                    onClick={executeView}
                                >查看</Button>
                            )
                        }
                        <Button
                            type="primary"
                            onClick={executeSave}
                        >保存</Button>
                    </Flex>
                </Col>
            </Row>
        </div>
    )
}

export default AmisTemplateTabItem;