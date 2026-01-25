import {Button, Col, Drawer, Flex, Input, Modal, Row} from 'antd';
import React, {useEffect, useState} from "react";
import apiClient from "../apiClient.tsx";
import Editor from '@monaco-editor/react';
import {Editor as AmisEitor} from 'amis-editor';
import {chartJson, crudJson} from './json';
import AmisTemplateCrud from './AmisTemplateCrud';

function AmisTemplateTabItem(props: {
    isCreate: boolean,
    apiTemplateId: string,
    reload: () => void,
    remove?: () => void
}) {

    const [isCreate] = useState(props.isCreate);
    const [apiTemplateId, setApiTemplateId] = useState(props.apiTemplateId);
    const [context, setContext] = useState<string | undefined>(undefined);
    const [showCrudEditor, setShowCrudEditor] = useState(false);
    const [showAmisEditor, setShowAmisEditor] = useState(false);

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

    return (
      <>
        <Row style={{height: 'calc(100% - 33px)'}}>
          <Col span={24}>
            <Editor
              language="json"
              value={context}
              onChange={(value: string | undefined) => setContext(value)}
            />
          </Col>
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
                <>
                  <Button
                    onClick={() => {
                      setApiTemplateId('AmisTemplate-crud-test');
                      setContext(JSON.stringify(crudJson, null, 2));
                    }}
                  >
                    CRUD示例
                  </Button>
                  <Button
                    onClick={() => {
                      setApiTemplateId('AmisTemplate-chart-test');
                      setContext(JSON.stringify(chartJson, null, 2));
                    }}
                  >
                    图表示例
                  </Button>
                </>
              )}
              <Button onClick={() => setShowCrudEditor(true)}>
                模板化编辑
              </Button>
              <Button onClick={() => setShowAmisEditor(true)}>
                可视化编辑
              </Button>
              <Button type="primary" onClick={executeSave}>
                保存
              </Button>
            </Flex>
          </Col>
        </Row>
        <Drawer
          title="模板可视化编辑"
          onClose={() => setShowCrudEditor(false)}
          open={showCrudEditor}
          width={'100%'}
        >
          {showCrudEditor && (
            <AmisTemplateCrud setApiTemplateId={setApiTemplateId} setContext={setContext} close={() => setShowCrudEditor(false)}/>
          )}
        </Drawer>
        <Drawer
          title="Amis可视化编辑"
          onClose={() => setShowAmisEditor(false)}
          open={showAmisEditor}
          width={'100%'}
        >
          {showAmisEditor && (
            <AmisEitor
              value={context ? JSON.parse(context) : {type: 'page'}}
              onChange={value => setContext(JSON.stringify(value, null, 2))}
            />
          )}
        </Drawer>
      </>
    );
}

export default AmisTemplateTabItem;