import {Button, Layout, Tabs, Tree} from 'antd';
import {useEffect, useRef, useState} from "react"
import DatabaseTabItem from "./DatabaseTabItem.tsx";
import {PlusOutlined} from '@ant-design/icons';
import ApiJsonTabItem from "./ApiJsonTabItem..tsx";
import apiClient from "./apiClient.tsx";

const {Content, Sider} = Layout;

interface DataNode {
    title: string;
    key: string;
    isLeaf?: boolean;
    children?: DataNode[];
}

interface TabItem {
    label: string;
    children: React.ReactNode;
    key: string;
}

function App() {

    const [items, setItems] = useState<TabItem[]>([]);
    const [activeKey, setActiveKey] = useState<string>();
    const newTabIndex = useRef(1);
    const [treeData, setTreeData] = useState<DataNode[]>([]);

    const loadData = async () => {
            const functionalState: {
                apiDatabase: boolean,
                apiJson: boolean,
                apiTemplate: boolean
            } = await apiClient.get('/sql/forge/console/functionalState').json()

            const TreeData: DataNode[] = [];

            if (functionalState.apiDatabase) {
                const database: {
                    databaseInfo: unknown,
                    tableTypes: {
                        tableType: string,
                        tables: { table: { tableName: string }, columns: { columnName: string }[] }[]
                    } []
                } = await apiClient.get('/sql/forge/api/database/current').json()

                const databasNode: DataNode = {title: 'Database', key: 'Database', children: []}

                if (database.tableTypes) {
                    database.tableTypes.forEach((tableType: {
                        tableType: string,
                        tables: { table: { tableName: string }, columns: { columnName: string }[] }[]
                    }) => {
                        const tableTypeNode: DataNode = {
                            title: tableType.tableType,
                            key: tableType.tableType,
                            children: []
                        }
                        const tables = tableType.tables;
                        if (tables) {
                            tables.forEach((table) => {
                                const tableNode: DataNode = {
                                    title: table.table.tableName,
                                    key: table.table.tableName,
                                    children: []
                                }
                                const columns = table.columns;
                                if (columns) {
                                    tableNode.children = columns.map((column) => ({
                                        title: column.columnName,
                                        key: column.columnName,
                                        isLeaf: true
                                    }))
                                }
                                tableTypeNode.children?.push(tableNode);
                            })
                        }
                        databasNode.children?.push(tableTypeNode)
                    })
                }
                TreeData.push(databasNode)
            }
            if (functionalState.apiJson) {
                TreeData.push({title: 'ApiJson', key: 'ApiJson', isLeaf: true})
            }
            if (functionalState.apiTemplate) {
                const templates: { name: string }[] = await apiClient.get('/sql/forge/api/template/list').json()
                TreeData.push({
                    title: 'ApiTemplate',
                    key: 'ApiTemplate',
                    children: templates.map((item: { name: string }) => ({
                        title: item.name,
                        key: item.name
                    }))
                })
            }

            setTreeData(TreeData);
    };

    useEffect(() => {
        loadData()
    }, [])

    const onChange = (newActiveKey: string) => {
        setActiveKey(newActiveKey);
    };

    const add = (type: string) => {
        const index = `${newTabIndex.current++}`;
        const newActiveKey = `Tab${index}`;
        const newLabel = `标签页${index}`;
        const newPanes = [...items];

        if (type === 'Database') {
            newPanes.push({
                label: newLabel,
                children: <DatabaseTabItem/>,
                key: newActiveKey,
            });
        } else if (type === 'ApiJson') {
            newPanes.push({
                label: newLabel,
                children: <ApiJsonTabItem/>,
                key: newActiveKey,
            })
        }

        setItems(newPanes);
        setActiveKey(newActiveKey);
    };

    const remove = (targetKey: string) => {
        let newActiveKey = activeKey;
        let lastIndex = 0;
        items.forEach((pane, i) => {
            if (pane.key === targetKey) {
                lastIndex = i - 1;
            }
        });

        const newPanes = items?.filter((item) => item.key !== targetKey) || [];

        if (newPanes.length && newActiveKey === targetKey) {
            const {key} = newPanes[lastIndex] || newPanes[newPanes.length - 1];
            newActiveKey = key;
        } else {
            newActiveKey = undefined
        }

        setItems(newPanes);
        setActiveKey(newActiveKey);
    };


    const onEdit = (
        targetKey: React.MouseEvent | React.KeyboardEvent | string,
        action: 'add' | 'remove',
    ) => {
        if (typeof targetKey === 'string' && action === 'add') {
            add("");
        } else if (typeof targetKey === 'string' && action === 'remove') {
            remove(targetKey);
        }
    };

    return (
        <Layout style={{height: '100%'}}>
            <Sider theme={'light'} width={'300px'}>
                <Tree
                    treeData={treeData}
                    defaultExpandAll={true}
                    titleRender={(nodeData: DataNode) => {
                        if (nodeData.key === 'Database') {
                            return (<div>
                                <span style={{fontWeight: 'bold'}}>{nodeData.title}</span>
                                <Button shape="circle" icon={<PlusOutlined/>} size="small"
                                        style={{marginLeft: '8px', border: 'none'}}
                                        onClick={() => {
                                            add(nodeData.key);
                                        }}
                                />
                            </div>)
                        } else if (nodeData.key === 'ApiJson') {
                            return <div>
                                <span style={{fontWeight: 'bold'}}>{nodeData.title}</span>
                                <Button shape="circle" icon={<PlusOutlined/>} size="small"
                                        style={{marginLeft: '8px', border: 'none'}}
                                        onClick={() => {
                                            add(nodeData.key);
                                        }}
                                />

                            </div>
                        } else {
                            return nodeData.title
                        }
                    }}
                    blockNode={true}
                />
            </Sider>
            <Content>
                <Tabs
                    type="editable-card"
                    hideAdd={true}
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
