import {Col, Input, Row, Table} from "antd";

function TabItemContent(props: {
                            textAreaId: string,
                            dataSource: any[],
                            columns: any[]
                        }
) {

    return (
        <div style={{height: '100%'}}>
            <Row style={{height: '50%'}}>
                <Col span={24}>
                    <Input.TextArea id={props.textAreaId} autoSize={false} styles={{textarea: {height: '100%'}}}
                                    style={{resize: "none"}}/>
                </Col>
            </Row>
            <Row style={{height: '50%'}}>
                <Col span={24}>
                    <Table dataSource={props.dataSource} columns={props.columns}/>
                </Col>
            </Row>
        </div>
    )
}

export default TabItemContent;