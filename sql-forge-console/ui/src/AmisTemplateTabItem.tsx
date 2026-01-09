import {Button, Col, Drawer, Flex, Input, Modal, Row} from 'antd';
import {useEffect, useState} from "react";
import apiClient from "./apiClient.tsx";
import Editor from '@monaco-editor/react';
import {Editor as AmisEitor} from 'amis-editor';

function AmisTemplateTabItem(props: {
    isCreate: boolean,
    apiTemplateId: string,
    reload: () => void,
    remove?: () => void
}) {

    const [isCreate] = useState(props.isCreate);
    const [apiTemplateId, setApiTemplateId] = useState(props.apiTemplateId);
    const [context, setContext] = useState<string | undefined>(undefined);
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
                      setContext(`{
\t"type": "crud",
\t"id": "crud_users",
\t"api": {
\t\t"method": "post",
\t\t"url": "/sql/forge/api/json/selectPage/users",
\t\t"data": {
\t\t\t"@where": [{
\t\t\t\t"column": "USERNAME",
\t\t\t\t"condition": "LIKE",
\t\t\t\t"value": "$\{USERNAME\}"
\t\t\t}, {
\t\t\t\t"column": "EMAIL",
\t\t\t\t"condition": "LIKE",
\t\t\t\t"value": "$\{EMAIL\}"
\t\t\t}],
\t\t\t"@order": ["$\{default(orderBy && orderDir ? (orderBy + ' ' + orderDir):'',undefined)\}"],
\t\t\t"@page": {
\t\t\t\t"pageIndex": "$\{page - 1\}",
\t\t\t\t"pageSize": "$\{perPage\}"
\t\t\t}
\t\t}
\t},
\t"headerToolbar": [{
\t\t\t"label": "新增",
\t\t\t"type": "button",
\t\t\t"icon": "fa fa-plus",
\t\t\t"level": "primary",
\t\t\t"actionType": "drawer",
\t\t\t"drawer": {
\t\t\t\t"title": "新增表单",
\t\t\t\t"body": {
\t\t\t\t\t"type": "form",
\t\t\t\t\t"api": {
\t\t\t\t\t\t"method": "post",
\t\t\t\t\t\t"url": "/sql/forge/api/json/insert/users",
\t\t\t\t\t\t"data": {
\t\t\t\t\t\t\t"@set": "$$"
\t\t\t\t\t\t}
\t\t\t\t\t},
\t\t\t\t\t"onEvent": {
\t\t\t\t\t\t"submitSucc": {
\t\t\t\t\t\t\t"actions": [{
\t\t\t\t\t\t\t\t"actionType": "reload",
\t\t\t\t\t\t\t\t"componentId": "crud_users"
\t\t\t\t\t\t\t}]
\t\t\t\t\t\t}
\t\t\t\t\t},
\t\t\t\t\t"body": [{
\t\t\t\t\t\t\t"type": "uuid",
\t\t\t\t\t\t\t"name": "ID"
\t\t\t\t\t\t},
\t\t\t\t\t\t{
\t\t\t\t\t\t\t"type": "input-text",
\t\t\t\t\t\t\t"name": "USERNAME",
\t\t\t\t\t\t\t"label": "用户名"
\t\t\t\t\t\t},
\t\t\t\t\t\t{
\t\t\t\t\t\t\t"type": "input-text",
\t\t\t\t\t\t\t"name": "EMAIL",
\t\t\t\t\t\t\t"label": "邮箱"
\t\t\t\t\t\t}
\t\t\t\t\t]
\t\t\t\t}
\t\t\t}
\t\t},
\t\t"bulkActions",
\t\t{
\t\t\t"type": "columns-toggler",
\t\t\t"align": "right"
\t\t},
\t\t{
\t\t\t"type": "drag-toggler",
\t\t\t"align": "right"
\t\t},
\t\t{
\t\t\t"type": "export-excel",
\t\t\t"label": "导出",
\t\t\t"icon": "fa fa-file-excel",
\t\t\t"api": {
\t\t\t\t"method": "post",
\t\t\t\t"url": "/sql/forge/api/json/select/users",
\t\t\t\t"data": {
\t\t\t\t\t"@where": [{
\t\t\t\t\t\t"column": "USERNAME",
\t\t\t\t\t\t"condition": "LIKE",
\t\t\t\t\t\t"value": "$\{USERNAME\}"
\t\t\t\t\t}, {
\t\t\t\t\t\t"column": "EMAIL",
\t\t\t\t\t\t"condition": "LIKE",
\t\t\t\t\t\t"value": "$\{EMAIL\}"
\t\t\t\t\t}]
\t\t\t\t}
\t\t\t},
\t\t\t"align": "right"
\t\t}
\t],
\t"footerToolbar": [
\t\t"statistics",
\t\t{
\t\t\t"type": "pagination",
\t\t\t"layout": "total,perPage,pager,go"
\t\t}
\t],
\t"bulkActions": [{
\t\t"label": "批量删除",
\t\t"icon": "fa fa-trash",
\t\t"actionType": "ajax",
\t\t"api": {
\t\t\t"method": "post",
\t\t\t"url": "/sql/forge/api/json/delete/users",
\t\t\t"data": {
\t\t\t\t"@where": [{
\t\t\t\t\t"column": "ID",
\t\t\t\t\t"condition": "IN",
\t\t\t\t\t"value": "$\{ids | split\}"
\t\t\t\t}]
\t\t\t}
\t\t},
\t\t"confirmText": "确定要批量删除?"
\t}],
\t"keepItemSelectionOnPageChange": true,
\t"labelTpl": "$\{USERNAME\}",
\t"autoFillHeight": true,
\t"autoGenerateFilter": true,
\t"showIndex": true,
\t"primaryField": "ID",
\t"columns": [{
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
\t\t\t},
\t\t\t"sortable": true
\t\t}, {
\t\t\t"name": "EMAIL",
\t\t\t"label": "邮箱",
\t\t\t"searchable": {
\t\t\t\t"type": "input-text",
\t\t\t\t"name": "EMAIL",
\t\t\t\t"label": "邮箱",
\t\t\t\t"placeholder": "输入邮箱"
\t\t\t},
\t\t\t"sortable": true
\t\t},
\t\t{
\t\t\t"type": "operation",
\t\t\t"label": "操作",
\t\t\t"buttons": [{
\t\t\t\t\t"label": "修改",
\t\t\t\t\t"type": "button",
\t\t\t\t\t"icon": "fa fa-pen-to-square",
\t\t\t\t\t"actionType": "drawer",
\t\t\t\t\t"drawer": {
\t\t\t\t\t\t"title": "新增表单",
\t\t\t\t\t\t"body": {
\t\t\t\t\t\t\t"type": "form",
\t\t\t\t\t\t\t"initApi": {
\t\t\t\t\t\t\t\t"method": "post",
\t\t\t\t\t\t\t\t"url": "/sql/forge/api/json/select/users",
\t\t\t\t\t\t\t\t"data": {
\t\t\t\t\t\t\t\t\t"@where": [{
\t\t\t\t\t\t\t\t\t\t"column": "ID",
\t\t\t\t\t\t\t\t\t\t"condition": "EQ",
\t\t\t\t\t\t\t\t\t\t"value": "$\{ID\}"
\t\t\t\t\t\t\t\t\t}]
\t\t\t\t\t\t\t\t},
\t\t\t\t\t\t\t\t"responseData": {
\t\t\t\t\t\t\t\t\t"&": "$\{items | first\}"
\t\t\t\t\t\t\t\t}
\t\t\t\t\t\t\t},
\t\t\t\t\t\t\t"api": {
\t\t\t\t\t\t\t\t"method": "post",
\t\t\t\t\t\t\t\t"url": "/sql/forge/api/json/update/users",
\t\t\t\t\t\t\t\t"data": {
\t\t\t\t\t\t\t\t\t"@set": "$$",
\t\t\t\t\t\t\t\t\t"@where": [{
\t\t\t\t\t\t\t\t\t\t"column": "ID",
\t\t\t\t\t\t\t\t\t\t"condition": "EQ",
\t\t\t\t\t\t\t\t\t\t"value": "$\{ID\}"
\t\t\t\t\t\t\t\t\t}]
\t\t\t\t\t\t\t\t}
\t\t\t\t\t\t\t},
\t\t\t\t\t\t\t"onEvent": {
\t\t\t\t\t\t\t\t"submitSucc": {
\t\t\t\t\t\t\t\t\t"actions": [{
\t\t\t\t\t\t\t\t\t\t"actionType": "reload",
\t\t\t\t\t\t\t\t\t\t"componentId": "crud_users"
\t\t\t\t\t\t\t\t\t}]
\t\t\t\t\t\t\t\t}
\t\t\t\t\t\t\t},
\t\t\t\t\t\t\t"body": [{
\t\t\t\t\t\t\t\t\t"type": "input-text",
\t\t\t\t\t\t\t\t\t"name": "USERNAME",
\t\t\t\t\t\t\t\t\t"label": "用户名"
\t\t\t\t\t\t\t\t},
\t\t\t\t\t\t\t\t{
\t\t\t\t\t\t\t\t\t"type": "input-text",
\t\t\t\t\t\t\t\t\t"name": "EMAIL",
\t\t\t\t\t\t\t\t\t"label": "邮箱"
\t\t\t\t\t\t\t\t}
\t\t\t\t\t\t\t]
\t\t\t\t\t\t}
\t\t\t\t\t}
\t\t\t\t},
\t\t\t\t{
\t\t\t\t\t"label": "删除",
\t\t\t\t\t"type": "button",
\t\t\t\t\t"icon": "fa fa-minus",
\t\t\t\t\t"actionType": "ajax",
\t\t\t\t\t"level": "danger",
\t\t\t\t\t"confirmText": "确认要删除？",
\t\t\t\t\t"api": {
\t\t\t\t\t\t"method": "post",
\t\t\t\t\t\t"url": "/sql/forge/api/json/delete/users",
\t\t\t\t\t\t"data": {
\t\t\t\t\t\t\t"@where": [{
\t\t\t\t\t\t\t\t"column": "ID",
\t\t\t\t\t\t\t\t"condition": "EQ",
\t\t\t\t\t\t\t\t"value": "$\{ID\}"
\t\t\t\t\t\t\t}]
\t\t\t\t\t\t}
\t\t\t\t\t}
\t\t\t\t}
\t\t\t],
\t\t\t"fixed": "right"
\t\t}
\t]
}`);
                    }}
                  >
                    CRUD示例
                  </Button>
                  <Button
                    onClick={() => {
                      setApiTemplateId('AmisTemplate-chart-test');
                      setContext(`{
\t"type": "chart",
\t"api": {
\t\t"method": "post",
\t\t"url": "/sql/forge/api/calcite/execute/ApiCalciteTemplate-test",
\t\t"data": {
\t\t\t"ids": [
\t\t\t\t1,
\t\t\t\t2
\t\t\t]
\t\t}
\t},
\t"height": "100vh",
\t"config": {
\t\t"xAxis": {
\t\t\t"type": "category",
\t\t\t"data": "$\{items | pick:name\}"
\t\t},
\t\t"yAxis": {
\t\t\t"type": "value"
\t\t},
\t\t"series": [{
\t\t\t"data": "$\{items | pick:grade\}",
\t\t\t"type": "bar"
\t\t}]
\t}
}`);
                    }}
                  >
                    图表示例
                  </Button>
                </>
              )}
              <Button type="primary" onClick={() => setShowAmisEditor(true)}>
                可视化编辑
              </Button>
              <Button type="primary" onClick={executeSave}>
                保存
              </Button>
            </Flex>
          </Col>
        </Row>
        <Drawer
          title="Amis可视化编辑"
          closable={{'aria-label': 'Close Button'}}
          onClose={() => setShowAmisEditor(false)}
          open={showAmisEditor}
          width={'100%'}
        >
          <AmisEitor
            value={context ? JSON.parse(context) : {type:'page'}}
            onChange={(value) => setContext(JSON.stringify(value))}
          />
        </Drawer>
      </>
    );
}

export default AmisTemplateTabItem;