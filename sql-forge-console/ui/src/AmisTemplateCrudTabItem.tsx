import {Button, Col, Flex, Input, Modal, Row, Select} from "antd";
import {useRef, useState} from "react";
import apiClient from "./apiClient.tsx";
import SingleTable from "./amisTemplateCrud/SingleTable.tsx";
import None from "./amisTemplateCrud/None.tsx";
import type {AmisTemplateCrudMethods} from "./type.tsx";

function AmisTemplateCrudTabItem(props: {
    reload: () => void,
    remove?: () => void
}) {

    const [templateType, setTemplateType] = useState<string |undefined>(undefined);
    const childRef = useRef<AmisTemplateCrudMethods>(null);
    const [apiTemplateId, setApiTemplateId] = useState<string |undefined>(undefined);


    const executeSave = () => {
        if (!childRef.current){
            Modal.error({title: '错误', content: "未知异常"});
            return;
        }

        const context = childRef.current.getContext()

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

    const renderTemplateContent = () => {
      switch (templateType) {
        case 'single-table':
          return (
            <SingleTable ref={childRef} setapiTemplateId={setApiTemplateId} />
          );
        default:
          return <None ref={childRef} />;
      }
    };

    return (
      <>
        <Row style={{height: '33px'}}>
          <Col span={24}>
            <Select
              placeholder="请选择模板类型"
              value={templateType}
              onChange={value => setTemplateType(value)}
              allowClear={true}
              options={[{value: 'single-table', label: '单表'}]}
            />
          </Col>
        </Row>
        <Row style={{height: 'calc(100% - 66px)'}}>
          <Col span={24}>{renderTemplateContent()}</Col>
        </Row>
        <Row style={{height: '33px'}}>
          <Col span={24}>
            <Flex gap={'small'} style={{float: 'right'}}>
              <Input
                placeholder="模板标识"
                value={apiTemplateId}
                onChange={e => setApiTemplateId(e.target.value)}
              />
              <Button type="primary" onClick={executeSave}>
                保存
              </Button>
            </Flex>
          </Col>
        </Row>
      </>
    );
}

export default AmisTemplateCrudTabItem;