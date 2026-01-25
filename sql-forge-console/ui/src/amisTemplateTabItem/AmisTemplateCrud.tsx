import {Button, Col, Flex, Input, Modal, Row, Select} from 'antd';
import {useRef, useState} from 'react';
import apiClient from '../apiClient.tsx';
import SingleTable from './template/SingleTable.tsx';
import None from './template/None.tsx';
import type {AmisTemplateCrudMethods} from '../type.tsx';
import MasterDetailTable from './template/MasterDetailTable';

function AmisTemplateCrud(props: {
  setApiTemplateId: (value: string | undefined) => void;
  setContext: (value: string | undefined) => void;
  close: () => void;
}) {
  const [templateType, setTemplateType] = useState<string | undefined>(
    undefined
  );
  const childRef = useRef<AmisTemplateCrudMethods>(null);

  const executeSave = () => {
    if (!childRef.current) {
      Modal.error({title: '错误', content: '未知异常'});
      return;
    }

    props.setApiTemplateId(childRef.current.getApiTemplateId());
    props.setContext(childRef.current.getContext());
    props.close();
  };

  const renderTemplateContent = () => {
    switch (templateType) {
      case 'single-table':
        return <SingleTable ref={childRef} />;
      case 'master-detail':
        return <MasterDetailTable ref={childRef} />;
      default:
        return <None ref={childRef} />;
    }
  };

  return (
    <div style={{height: '100%'}}>
      <Row style={{height: '33px'}}>
        <Col span={24}>
          <Select
            placeholder="请选择模板类型"
            value={templateType}
            onChange={value => setTemplateType(value)}
            allowClear={true}
            options={[
              {value: 'single-table', label: '单表'},
              {value: 'master-detail', label: '主从表'}
            ]}
          />
        </Col>
      </Row>
      <Row style={{height: 'calc(100% - 66px)'}}>
        <Col span={24}>{renderTemplateContent()}</Col>
      </Row>
      <Row style={{height: '33px'}}>
        <Col span={24}>
          <Flex gap={'small'} style={{float: 'right'}}>
            <Button type="primary" onClick={executeSave}>
              确定
            </Button>
          </Flex>
        </Col>
      </Row>
    </div>
  );
}

export default AmisTemplateCrud;
