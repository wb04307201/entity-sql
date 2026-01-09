import {Button, Col, Flex, Modal, Row, Table} from "antd";
import {useState} from "react";
import apiClient from "./apiClient.tsx";
import Editor from "@monaco-editor/react";

interface ColumnType {
    title: string;
    dataIndex: string;
    key: string;
}

type DataType = Record<string, unknown>;

function DatabaseTabItem() {

    const [sql, setSql] = useState<string | undefined>(undefined);
    const [dataSource, setDataSource] = useState<DataType[]>([]);
    const [columns, setColumns] = useState<ColumnType[]>([]);

    const executeSql = () => {
        if (!sql) {
            Modal.error({title: '错误', content: '请输入sql'});
            return;
        }

        apiClient.post('/sql/forge/api/calcite/sql/execute', {sql: sql})
            .then(data => {
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
            });
    }

    return (
      <>
        <Row style={{height: 'calc(50% - 33px)'}}>
          <Col span={24}>
            <Editor
              language="sql"
              value={sql}
              onChange={(value: string | undefined) => setSql(value)}
            />
          </Col>
        </Row>
        <Row style={{height: '33px'}}>
          <Col span={24}>
            <Flex gap={'small'} style={{float: 'right'}}>
              <Button
                onClick={() => {
                  setSql(`select student.name, sum(score.grade) as grade
from MYSQL.student as student
    join POSTGRES.score as score
        on student.id=score.student_id group by student.name`);
                }}
              >
                示例
              </Button>
              <Button type="primary" onClick={executeSql}>
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
            />
          </Col>
        </Row>
      </>
    );
}

export default DatabaseTabItem;