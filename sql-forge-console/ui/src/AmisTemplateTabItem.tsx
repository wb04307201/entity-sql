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

    const amisEditor = () => {
        console.log("amisEditor")
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
\t"title": "users增删改查",
\t"body": {
\t\t"type": "crud",
\t\t"api": {
\t\t\t"method": "post",
\t\t\t"url": "/sql/forge/api/json/select/users",
\t\t\t"data": {
\t\t\t\t"@where": [{
\t\t\t\t\t"column": "USERNAME",
\t\t\t\t\t"condition": "LIKE",
\t\t\t\t\t"value": "\\$\\{USERNAME\\}"
\t\t\t\t}, {
\t\t\t\t\t"column": "EMAIL",
\t\t\t\t\t"condition": "LIKE",
\t\t\t\t\t"value": "\\$\\{EMAIL\\}"
\t\t\t\t}]
\t\t\t}
\t\t},
\t\t"autoGenerateFilter": true,
\t\t"showIndex": true,
\t\t"columns": [{
\t\t\t"name": "ID",
\t\t\t"label": "ID",
\t\t\t"hidden": true
\t\t}, {
\t\t\t"name": "USERNAME",
\t\t\t"label": "用户名",
\t\t\t"searchable": {
\t\t\t\t"type": "input-text",
\t\t\t\t"name": "USERNAME",
\t\t\t\t"label": "用户名",
\t\t\t\t"placeholder": "输入用户名"
\t\t\t}
\t\t}, {
\t\t\t"name": "EMAIL",
\t\t\t"label": "邮箱",
\t\t\t"searchable": {
\t\t\t\t"type": "input-text",
\t\t\t\t"name": "EMAIL",
\t\t\t\t"label": "邮箱",
\t\t\t\t"placeholder": "输入邮箱"
\t\t\t}
\t\t}]
\t}
}`)
                                    }}
                                >示例</Button>
                            )
                        }
                        {
                            !isCreate && (
                                <Button
                                    onClick={amisEditor}
                                >可视化编辑</Button>
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