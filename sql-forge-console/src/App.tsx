import {Layout, Tabs, Tree} from 'antd';
import {useEffect, useRef, useState} from "react";
import TabItemContent from "./TabItemContent.tsx";

const {Content, Sider} = Layout;

type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

interface DataNode {
    title: string;
    key: string;
    isLeaf?: boolean;
    children?: DataNode[];
}

const updateTreeData = (list: DataNode[], key: React.Key, children: DataNode[]): DataNode[] =>
    list.map((node) => {
        if (node.key === key) {
            return {
                ...node,
                children,
            };
        }
        if (node.children) {
            return {
                ...node,
                children: updateTreeData(node.children, key, children),
            };
        }
        return node;
    });

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
    const [treeData, setTreeData] = useState<DataNode[]>([
        {title: 'Expand to load', key: '0'},
        {title: 'Expand to load', key: '1'},
        {title: 'Tree Node', key: '2', isLeaf: true},
    ]);
    const [tableTypes, setTableTypes] = useState<{ description: string; type: string; }>()

    useEffect(() => {
        fetch('/sql/forge/database')
            .then(response => response.json())
            .then(data => {
                console.log(data);
                const tempTableTypes = data.tableTypes.map((item: {
                    description: string;
                    type: string;
                }) => ({title: item.description, key: item.type}))
                setTableTypes(tempTableTypes)

                const children = []

                children.push({title: '表', key: 'tables', children: [...tempTableTypes]})
                children.push({title: '目录', key: 'catalogs'})
                children.push({title: '模式', key: 'schemas'})

                setTreeData([{title: data.productName, key: 'datasource', children: children}])
            })
    },[])


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

    const onLoadData = ({ key, children }: { key: string; children?: DataNode[] }) =>
        new Promise<void>((resolve) => {
            if (children) {
                resolve();
                return;
            }
            setTimeout(() => {
                setTreeData((origin) =>
                    updateTreeData(origin, key, [
                        {title: 'Child Node', key: `${key}-0`},
                        {title: 'Child Node', key: `${key}-1`},
                    ]),
                );

                console.log(tableTypes)

                resolve();
            }, 1000);
        });

    return (
        <Layout style={{height: '100%'}}>
            <Sider theme={'light'}>
                <Tree loadData={onLoadData} treeData={treeData}/>
            </Sider>
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
