import {Layout, Tabs, Tree} from 'antd';
import {useEffect, useRef, useState} from "react";
import {DeleteOutlined, EditOutlined, FileAddOutlined, SearchOutlined} from '@ant-design/icons';
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
            children: <TabItemContent/>,
            key: 'Tab1'
        }
    ]);
    const [activeKey, setActiveKey] = useState(items[0].key);
    const newTabIndex = useRef(2);
    const [treeData, setTreeData] = useState<DataNode[]>([]);

    useEffect(() => {
        loadDataBase()
    }, [])

    const loadDataBase = () => {
        fetch('/sql/forge/database')
            .then(response => response.json())
            .then(data => {
                setTreeData(
                    [
                        {
                            title: '数据库',
                            key: JSON.stringify({catalog: data.catalog, schema: data.schema}),
                            children:
                                [
                                    {
                                        title: '表',
                                        key: JSON.stringify({
                                            catalog: data.catalog,
                                            schema: data.schema,
                                            tableType: 'TABLE'
                                        })
                                    },
                                    {
                                        title: '视图',
                                        key: JSON.stringify({
                                            catalog: data.catalog,
                                            schema: data.schema,
                                            tableType: 'VIEW'
                                        })
                                    },
                                ]
                        }
                    ]
                )
            })
    };

    const loadTables = async (catalog: string, schemaPattern: string, tableType: string) => {
        const data = await fetch(`/sql/forge/tables?catalog=${catalog}&schemaPattern=${schemaPattern}&types=${tableType}`).then(response => response.json());

        const tables: DataNode[] = data.map((item: { tableName: string }) => ({
            title: item.tableName,
            key: JSON.stringify({catalog: catalog, schema: schemaPattern, tableTypes: tableType, table: item.tableName})
        }));

        return tables;
    };

    const loadColumns = async (catalog: string, schemaPattern: string, tableType: string,tableNamePattern: string) => {
        const data = await fetch(`/sql/forge/columns?catalog=${catalog}&schemaPattern=${schemaPattern}&tableNamePattern=${tableNamePattern}`).then(response => response.json());

        const columns: DataNode[] = data.map((item: { columnName: string }) => ({
            title: <div>item.columnName<SearchOutlined /><FileAddOutlined /><EditOutlined /><DeleteOutlined /></div>,
            key: JSON.stringify({catalog: catalog, schema: schemaPattern, tableTypes: tableType, table: tableNamePattern, column: item.columnName}),
            isLeaf: true
        }));

        return columns;
    }


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
            children: <TabItemContent/>,
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
        new Promise<void>(async (resolve) => {
            if (children) {
                resolve();
                return;
            }

            const json = JSON.parse(key);

            if (json.table){
                const columns: DataNode[] = await loadColumns(json.catalog, json.schema, json.tableType,json.table)
                setTreeData((origin) =>
                    updateTreeData(origin, key, columns),
                );
                resolve();
                return;
            }

            if (json.tableType) {
                const tables: DataNode[] = await loadTables(json.catalog, json.schema, json.tableType)
                setTreeData((origin) =>
                    updateTreeData(origin, key, tables),
                );
                resolve();
                return;
            }
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
