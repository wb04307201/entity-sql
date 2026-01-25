import {Button, Col, Flex, Input, Modal, Row, Table, Radio} from 'antd';
import {useState} from 'react';
import apiClient from '../apiClient.tsx';
import Editor from '@monaco-editor/react';
import {
  deleteJson,
  insertJson,
  selectJson,
  selectPageJson,
  updateJson
} from './json';
import {buildTableData} from '../CommonUtils';

interface ColumnType {
  title: string;
  dataIndex: string;
  key: string;
}

type DataType = Record<string, unknown>;

function ApiJsonTabItem() {
  const [tableName, setTableName] = useState('');
  const [type, setType] = useState('select');
  const [json, setJson] = useState<string | undefined>(undefined);
  const [dataSource, setDataSource] = useState<DataType[]>([]);
  const [columns, setColumns] = useState<ColumnType[]>([]);

  const executeJson = () => {
    if (!json) {
      Modal.error({title: '错误', content: '请输入json'});
      return;
    }
    if (!tableName) {
      Modal.error({title: '错误', content: '请输入表名'});
      return;
    }
    let params;
    try {
      params = JSON.parse(json);
    } catch (error) {
      const typedError = error as {message: string};
      Modal.error({title: '错误', content: typedError.message});
      return;
    }

    apiClient
      .post(`/sql/forge/api/json/${type}/${tableName}`, params)
      .then((data: unknown) => {
        const tableData = buildTableData(data);
        setColumns(tableData.columns);
        setDataSource(tableData.rows);
      });
  };

  return (
    <>
      <Row style={{height: 'calc(50% - 66px)'}}>
        <Col span={24}>
          <Editor
            language="json"
            value={json}
            onChange={(value: string | undefined) => setJson(value)}
          />
        </Col>
      </Row>
      <Row style={{height: '66px'}}>
        <Col span={24}>
          <Flex gap={'small'} style={{float: 'right'}}>
            <Input
              placeholder="表名"
              value={tableName}
              onChange={e => setTableName(e.target.value)}
            />
            <Radio.Group
              value={type}
              options={[
                {value: 'select', label: '查询'},
                {value: 'selectPage', label: '分页查询'},
                {value: 'insert', label: '插入'},
                {value: 'update', label: '更新'},
                {value: 'delete', label: '删除'}
              ]}
              onChange={e => setType(e.target.value)}
            />
            <Button
              onClick={() => {
                if (type === 'select') {
                  setTableName('orders');
                  setJson(JSON.stringify(selectJson, null, 2));
                } else if (type === 'selectPage') {
                  setTableName('orders');
                  setJson(JSON.stringify(selectPageJson, null, 2));
                } else if (type === 'insert') {
                  setTableName('users');
                  setJson(JSON.stringify(insertJson, null, 2));
                } else if (type === 'update') {
                  setTableName('users');
                  setJson(JSON.stringify(updateJson, null, 2));
                } else if (type === 'delete') {
                  setTableName('users');
                  setJson(JSON.stringify(deleteJson, null, 2));
                }
              }}
            >
              示例
            </Button>
            <Button type="primary" onClick={executeJson}>
              执行
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

export default ApiJsonTabItem;
