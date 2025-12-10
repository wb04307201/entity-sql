import {Col, Layout, Row, Tabs, Input, Table} from 'antd';
import {useRef, useState} from "react";
import type {TableProps} from 'antd';
import TabItemContent from "./TabItemContent.tsx";

const {Content, Sider} = Layout;

type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

function App() {

    const [items, setItems] = useState([
        {
            label: '标签页1',
            children: <TabItemContent textAreaId="Tab1TextArea" dataSource={[]} columns={[]}/>,
            key: 'Tab1'
        }
    ]);
    const [activeKey, setActiveKey] = useState(items[0].key);
    const newTabIndex = useRef(2);

    const onChange = (newActiveKey: string) => {
        setActiveKey(newActiveKey);
    };

    const add = () => {
        const index = `${newTabIndex.current++}`;
        const newActiveKey = `Tab${index}`;
        const newLabel = `标签页${index}`;
        const newTextAreaId = `Tab${index}TextArea`;
        const newPanes = [...items];
        newPanes.push({
            label: newLabel,
            children: <TabItemContent textAreaId={newTextAreaId} dataSource={[]} columns={[]}/>,
            key: newActiveKey,
        });
        setItems(newPanes);
        setActiveKey(newActiveKey);
    };

    const remove = (targetKey: TargetKey) => {
        let newActiveKey = activeKey;
        let lastIndex = -1;
        items.forEach((item, i) => {
            if (item.key === targetKey) {
                lastIndex = i - 1;
            }
        });
        const newPanes = items.filter((item) => item.key !== targetKey);
        if (newPanes.length && newActiveKey === targetKey) {
            if (lastIndex >= 0) {
                newActiveKey = newPanes[lastIndex].key;
            } else {
                newActiveKey = newPanes[0].key;
            }
        }
        setItems(newPanes);
        setActiveKey(newActiveKey);
    };

    const onEdit = (
        targetKey: React.MouseEvent | React.KeyboardEvent | string,
        action: 'add' | 'remove',
    ) => {
        if (action === 'add') {
            add();
        } else {
            remove(targetKey);
        }
    };

    return (
        <Layout style={{height: '100%'}}>
            <Sider theme={'light'}>left sidebar</Sider>
            <Content>
                <Tabs
                    type="editable-card"
                    onChange={onChange}
                    activeKey={activeKey}
                    onEdit={onEdit}
                    items={items}
                    style={{height: '100vh'}}
                    styles={{
                        content: {height: 'calc(100vh - 56px)', padding: '0 16px'}
                    }}
                />
            </Content>
        </Layout>
    )
}

export default App
