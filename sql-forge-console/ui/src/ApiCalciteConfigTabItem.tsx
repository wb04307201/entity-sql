import {Button, Col, Flex, Input, Modal, Row} from "antd";
import {useEffect, useState} from "react";
import apiClient from "./apiClient.tsx";

function ApiCalciteConfigTabItem(props: {
    remove: () => void,
    reload: () => void
}) {
    const [config, setConfig] = useState("");

    useEffect(() => {
            apiClient.get(`/sql/forge/api/calciteConfig`)
                .then((data: unknown) => {
                    const config = data as {context: string};
                    setConfig(config.context)
                })
    }, []);

    const executeSave = () => {
        if (!config) {
            Modal.error({title: '错误', content: "请输入配置"});
            return;
        }
        try {
            JSON.parse(config);
        } catch (error) {
            const typedError = error as { message: string };
            Modal.error({title: '错误', content: typedError.message});
            return;
        }

        apiClient.post('/sql/forge/api/calciteConfig', {context: config})
            .then((_) => {
                props.reload()
                props.remove()
            })
    }


    return (
        <div style={{height: '100%'}}>
            <Row style={{height: 'calc(100% - 33px)'}}>
                <Col span={24}>
                    <Input.TextArea
                        wrap="soft"
                        value={config}
                        onChange={(e) => setConfig(e.target.value)}
                        autoSize={false}
                        styles={{textarea: {height: '100%'}}}
                        style={{resize: "none"}}
                        placeholder="请输入配置内容"
                    />
                </Col>
            </Row>
            <Row>
                <Col span={24}>
                    <Flex gap={"small"} style={{float: "right"}}>
                        <Button
                            onClick={() => {
                                setConfig(`{
  "version": "1.0",
  "defaultSchema": "MYSQL",
  "schemas": [
    {
      "factory": "org.apache.calcite.adapter.jdbc.JdbcSchema$Factory",
      "name": "MYSQL",
      "operand": {
        "jdbcDriver": "com.mysql.cj.jdbc.Driver",
        "jdbcUrl": "jdbc:mysql://localhost:3306/test",
        "jdbcUser": "root",
        "jdbcPassword": "123456"
      },
      "type": "custom"
    },
    {
      "factory": "org.apache.calcite.adapter.jdbc.JdbcSchema$Factory",
      "name": "POSTGRES",
      "operand": {
        "jdbcDriver": "org.postgresql.Driver",
        "jdbcUrl": "jdbc:postgresql://localhost:5432/test",
        "jdbcUser": "postgres",
        "jdbcPassword": "123456"
      },
      "type": "custom"
    }
  ]
}`)
                            }}
                        >示例</Button>
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

export default ApiCalciteConfigTabItem;