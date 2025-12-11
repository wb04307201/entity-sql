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

function App() {

    const [items, setItems] = useState([
        {
            label: '标签页1',
            children: <TabItemContent />,
            key: 'Tab1'
        }
    ]);
    const [activeKey, setActiveKey] = useState(items[0].key);
    const newTabIndex = useRef(2);
    const [treeData, setTreeData] = useState<DataNode[]>(
        [
            {
                title: '数据库', key: 'datasource', children:
                    [
                        {
                            title: '表', key: 'datasource-tables', children:
                                [
                                    {title: '表', key: 'datasource-tables-TABLE'},
                                    {title: '视图', key: 'datasource-tables-VIEW'},
                                ]
                        },
                    ]
            }
        ]);

    useEffect(() =>{
        loadDataSourceTables()
    },[])

    const loadDataSourceTables = () => {
        Promise.all([
            fetch('/sql/forge/tables?types=TABLE').then(response => response.json()),
            fetch('/sql/forge/tables?types=VIEW').then(response => response.json())
        ]).then(([data1, data2]) => {
            const tables: DataNode[] = data1.map((item:{tableName: string, remarks: string}) => ({
                title: item.remarks ? item.remarks : item.tableName,
                key: `datasource-tables-TABLE-${item.tableName}`
            }));

            const views: DataNode[] = data2.map((item:{tableName: string, remarks: string}) => ({
                title: item.remarks ? item.remarks : item.tableName,
                key: `datasource-tables-VIEW-${item.tableName}`
            }));

            setTreeData([
                {
                    title: '数据库',
                    key: 'datasource',
                    children: [
                        {
                            title: '表',
                            key: 'datasource-tables',
                            children: [
                                {title: '表', key: 'datasource-tables-TABLE', children: tables},
                                {title: '视图', key: 'datasource-tables-VIEW', children: views},
                            ]
                        },
                    ]
                }
            ]);
        });
    };



    const onChange = (newActiveKey: string) => {
        setActiveKey(newActiveKey);
    };

    const add = () => {
        const index = `${newTabIndex.current++}`;
        const newActiveKey = `Tab${index}`;
        const newLabel = `标签页${index}`;
        const newPanes = [...items];
        newPanes.push({
            label: newLabel,
            children: <TabItemContent />,
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

    const onLoadData = ({key, children}: { key: string; children?: DataNode[] }) =>
        new Promise<void>((resolve) => {
            console.log('onLoadData', key, children);
            if (children) {
                resolve();
                return;
            }

            const keys = key.split('-');

            fetch(`/sql/forge/columns?tableNamePattern=${keys[keys.length - 1]}`)
                .then(response => response.json())
                .then(data => {
                    const columns: DataNode[] = data.map((item:{columnName: string, remarks: string}) => ({
                        title: item.remarks ? item.remarks : item.columnName,
                        key: `${key}-${item.columnName}`,
                        isLeaf: true,
                    }));

                    setTreeData((origin) =>
                        updateTreeData(origin, key, columns),
                    );

                    resolve();
                });
        });

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

    return (
        <Layout style={{height: '100%'}}>
            <Sider theme={'light'} width={'300px'}>
                <Tree
                    loadData={onLoadData}
                    treeData={treeData}
                    defaultExpandAll={true}
                />
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

export default App;
