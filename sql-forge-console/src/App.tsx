import {Button, Layout, Spin, Tabs, Tree} from 'antd';
import {useEffect, useRef, useState} from "react"
import DatabaseTabItem from "./DatabaseTabItem.tsx";
import {DeleteOutlined, EditOutlined, PlusOutlined, ReloadOutlined, SettingOutlined} from '@ant-design/icons';
import ApiJsonTabItem from "./ApiJsonTabItem.tsx";
import apiClient from "./apiClient.tsx";
import ApiTemplateTabItem from "./ApiTemplateTabItem.tsx";
import ApiCalciteTabItem from "./ApiCalciteTabItem.tsx";
import ApiCalciteConfigTabItem from "./ApiCalciteConfigTabItem.tsx";

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

interface DatabaseInfo {
    databaseInfo: unknown,
    schemaTableTypeTables: {
        schema: { tableSchema: string },
        tableTypeTables: {
            tableType: string,
            tables: {
                table: { tableName: string },
                columns: { columnName: string }[]
            }[]
        } []
    }[]
}

function App() {

    const [items, setItems] = useState<TabItem[]>([]);
    const [activeKey, setActiveKey] = useState<string>();
    const newTabIndex = useRef(1);
    const [treeData, setTreeData] = useState<DataNode[]>([]);
    const [treeSpinning, setTreeSpinning] = useState<boolean>(false);

    const loadData = async () => {
        setTreeSpinning(true)
        const functionalState: {
            apiDatabase: boolean,
            apiJson: boolean,
            apiTemplate: boolean,
            apiCalcite: boolean
        } = await apiClient.get('/sql/forge/console/functionalState').json()

        let TreeData: DataNode[] = [...treeData];

        if (functionalState.apiDatabase) {
            TreeData = await loadApiDatabase(TreeData)
        }
        if (functionalState.apiJson) {
            TreeData.push({title: 'ApiJson', key: 'ApiJson', isLeaf: true})
        }
        if (functionalState.apiTemplate) {
            TreeData = await loadApiTemplate(TreeData);
        }
        if (functionalState.apiCalcite) {
            TreeData = await loadApiCalcite(TreeData)
        }

        setTreeData(TreeData);
        setTreeSpinning(false)
    };

    const loadApiDatabase = async (TreeData: DataNode[]) => {
        const databasNode: DataNode = {title: 'Database', key: 'Database', children: []}
        const database: DatabaseInfo = await apiClient.get('/sql/forge/api/databaseMetaData').json()
        const schemaTableTypeTables = database.schemaTableTypeTables
        if (schemaTableTypeTables) {
            schemaTableTypeTables.forEach(schemaTableTypeTable => {
                const schemaNode: DataNode = {
                    title: schemaTableTypeTable.schema.tableSchema,
                    key: `${databasNode.key}-${schemaTableTypeTable.schema.tableSchema}`,
                    children: []
                }
                const tableTypeTables = schemaTableTypeTable.tableTypeTables
                if (tableTypeTables) {
                    tableTypeTables.forEach(tableType => {
                        const tableTypeNode: DataNode = {
                            title: tableType.tableType,
                            key: `${schemaNode.key}-${tableType.tableType}`,
                            children: []
                        }
                        const tables = tableType.tables;
                        if (tables) {
                            tables.forEach(table => {
                                const tableNode: DataNode = {
                                    title: table.table.tableName,
                                    key: `${tableTypeNode.key}-${table.table.tableName}`,
                                    children: []
                                }
                                const columns = table.columns;
                                if (columns) {
                                    tableNode.children = columns.map((column) => ({
                                        title: column.columnName,
                                        key: `${tableNode.key}-${column.columnName}`,
                                        isLeaf: true
                                    }))
                                }
                                tableTypeNode.children?.push(tableNode);
                            })
                        }
                        schemaNode.children?.push(tableTypeNode)
                    })
                }
                databasNode.children?.push(schemaNode)
            })
        }
        const orgTreeNode = TreeData.find(item => item.title === 'Database')
        if (orgTreeNode) {
            TreeData.splice(TreeData.indexOf(orgTreeNode), 1, databasNode)
        } else {
            TreeData.push(databasNode)
        }

        return TreeData
    }

    const loadApiTemplate = async (TreeData: DataNode[]) => {
        const templates: { id: string }[] = await apiClient.get('/sql/forge/api/template').json()
        const apiTemplateNode: DataNode = {
            title: 'ApiTemplate', key: 'ApiTemplate', children: templates.map(item => ({
                title: item.id,
                key: 'ApiTemplate-' + item.id
            }))
        }
        const orgTreeNode = TreeData.find(item => item.title === 'ApiTemplate')
        if (orgTreeNode) {
            TreeData.splice(TreeData.indexOf(orgTreeNode), 1, apiTemplateNode)
        } else {
            TreeData.push(apiTemplateNode)
        }
        return TreeData
    }

    const loadApiCalcite = async (TreeData: DataNode[]) => {
        const apiCalciteNode: DataNode = {title: 'ApiCalcite', key: 'ApiCalcite', children: []}
        const database: DatabaseInfo = await apiClient.get('/sql/forge/api/calciteMetaData').json()
        const schemaTableTypeTables = database.schemaTableTypeTables
        if (schemaTableTypeTables) {
            schemaTableTypeTables.forEach(schemaTableTypeTable => {
                const schemaNode: DataNode = {
                    title: schemaTableTypeTable.schema.tableSchema,
                    key: `${apiCalciteNode.key}-${schemaTableTypeTable.schema.tableSchema}`,
                    children: []
                }
                const tableTypeTables = schemaTableTypeTable.tableTypeTables
                if (tableTypeTables) {
                    tableTypeTables.forEach(tableType => {
                        const tableTypeNode: DataNode = {
                            title: tableType.tableType,
                            key: `${schemaNode.key}-${tableType.tableType}`,
                            children: []
                        }
                        const tables = tableType.tables;
                        if (tables) {
                            tables.forEach(table => {
                                const tableNode: DataNode = {
                                    title: table.table.tableName,
                                    key: `${tableTypeNode.key}-${table.table.tableName}`,
                                    children: []
                                }
                                const columns = table.columns;
                                if (columns) {
                                    tableNode.children = columns.map((column) => ({
                                        title: column.columnName,
                                        key: `${tableNode.key}-${column.columnName}`,
                                        isLeaf: true
                                    }))
                                }
                                tableTypeNode.children?.push(tableNode);
                            })
                        }
                        schemaNode.children?.push(tableTypeNode)
                    })
                }
                apiCalciteNode.children?.push(schemaNode)
            })
        }
        const templates: { id: string }[] = await apiClient.get('/sql/forge/api/calcite').json()
        templates.forEach(item => {
            apiCalciteNode.children?.push({
                title: item.id,
                key: 'ApiCalcite-' + item.id
            })
        })
        const orgTreeNode = TreeData.find(item => item.title === 'ApiCalcite')
        if (orgTreeNode) {
            TreeData.splice(TreeData.indexOf(orgTreeNode), 1, apiCalciteNode)
        } else {
            TreeData.push(apiCalciteNode)
        }
        return TreeData
    }

    useEffect(() => {
        loadData()
    }, [])

    const reloadApiTemplate = async () => {
        setTreeSpinning(true)
        let TreeData: DataNode[] = [...treeData];
        TreeData = await loadApiTemplate(TreeData)
        setTreeData(TreeData)
        setTreeSpinning(false)
    }

    const reloadApiCalcite = async () => {
        setTreeSpinning(true)
        let TreeData: DataNode[] = [...treeData];
        TreeData = await loadApiCalcite(TreeData)
        setTreeData(TreeData)
        setTreeSpinning(false)
    }

    const onChange = (newActiveKey: string) => {
        setActiveKey(newActiveKey);
    };

    const add = (type: string) => {
        const index = `${newTabIndex.current++}`;
        const newActiveKey = `Tab-${type}-${index}`;
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
        } else if (type === 'ApiTemplate') {
            newPanes.push({
                label: newLabel,
                children: <ApiTemplateTabItem isCreate={true} apiTemplateId={""} reload={reloadApiTemplate}
                                              remove={() => remove(newActiveKey)}/>,
                key: newActiveKey,
            })
        } else if (type.startsWith('ApiTemplate-')) {
            newPanes.push({
                label: newLabel,
                children: <ApiTemplateTabItem isCreate={false} apiTemplateId={type.substring(12)}
                                              reload={reloadApiTemplate}/>,
                key: newActiveKey,
            })
        } else if (type === 'ApiCalcite-config') {
            newPanes.push({
                label: newLabel,
                children: <ApiCalciteConfigTabItem remove={() => remove(newActiveKey)} reload={reloadApiCalcite}/>,
                key: newActiveKey,
            })

        } else if (type === 'ApiCalcite') {
            newPanes.push({
                label: newLabel,
                children: <ApiCalciteTabItem isCreate={true} apiTemplateId={""} reload={reloadApiCalcite}
                                             remove={() => remove(newActiveKey)}/>,
                key: newActiveKey,
            })
        } else if (type.startsWith('ApiCalcite-')) {
            newPanes.push({
                label: newLabel,
                children: <ApiCalciteTabItem isCreate={false} apiTemplateId={type.substring(11)}
                                             reload={reloadApiTemplate}/>,
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

    const removes = (targetKey: string) => {
        const newPanes = items?.filter((item) => !item.key.startsWith(`Tab-${targetKey}-`)) || [];
        setItems(newPanes);
        setActiveKey(undefined);
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
                <Spin spinning={treeSpinning}>
                    <Tree
                        treeData={treeData}
                        defaultExpandAll={true}
                        titleRender={(nodeData: DataNode) => {
                            if (nodeData.key === 'Database') {
                                return (<div>
                                    <span style={{fontWeight: 'bold'}}>{nodeData.title}</span>
                                    <Button shape="circle" icon={<ReloadOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => reloadApiTemplate()}
                                    />
                                    <Button shape="circle" icon={<PlusOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                add(nodeData.key);
                                            }}
                                    />
                                </div>)
                            } else if (nodeData.key === 'ApiJson') {
                                return (<div>
                                    <span style={{fontWeight: 'bold'}}>{nodeData.title}</span>
                                    <Button shape="circle" icon={<PlusOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                add(nodeData.key);
                                            }}
                                    />
                                </div>)
                            } else if (nodeData.key === 'ApiTemplate') {
                                return (<div>
                                    <span style={{fontWeight: 'bold'}}>{nodeData.title}</span>
                                    <Button shape="circle" icon={<ReloadOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={async () => {
                                                setTreeSpinning(true)
                                                let TreeData: DataNode[] = [...treeData];
                                                TreeData = await loadApiTemplate(TreeData)
                                                setTreeData(TreeData)
                                                setTreeSpinning(false)
                                            }}
                                    />
                                    <Button shape="circle" icon={<PlusOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                add(nodeData.key);
                                            }}
                                    />
                                </div>)
                            } else if (nodeData.key.startsWith('ApiTemplate-')) {
                                return (<div>
                                    <span>{nodeData.title}</span>
                                    <Button shape="circle" icon={<DeleteOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                setTreeSpinning(true)
                                                apiClient.delete(`/sql/forge/api/template/${nodeData.key.substring(12)}`)
                                                    .json()
                                                    .then((_) => {
                                                        removes(nodeData.key);
                                                        reloadApiTemplate();
                                                    }).catch((_) => {
                                                    setTreeSpinning(false)
                                                })
                                            }}
                                    />
                                    <Button shape="circle" icon={<EditOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                add(nodeData.key);
                                            }}
                                    />
                                </div>)
                            } else if (nodeData.key === 'ApiCalcite') {
                                return (<div>
                                    <span style={{fontWeight: 'bold'}}>{nodeData.title}</span>
                                    <Button shape="circle" icon={<ReloadOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={async () => {
                                                setTreeSpinning(true)
                                                let TreeData: DataNode[] = [...treeData];
                                                TreeData = await loadApiTemplate(TreeData)
                                                setTreeData(TreeData)
                                                setTreeSpinning(false)
                                            }}
                                    />
                                    <Button shape="circle" icon={<SettingOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                add(`${nodeData.key}-config`)
                                            }}
                                    />
                                    <Button shape="circle" icon={<PlusOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                add(nodeData.key);
                                            }}
                                    />
                                </div>)
                            } else if (nodeData.key.startsWith('ApiCalcite-')) {
                                return (<div>
                                    <span>{nodeData.title}</span>
                                    <Button shape="circle" icon={<DeleteOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                setTreeSpinning(true)
                                                apiClient.delete(`/sql/forge/api/calcite/${nodeData.key.substring(11)}`)
                                                    .json()
                                                    .then(_data => {
                                                        removes(nodeData.key);
                                                        reloadApiCalcite();
                                                    }).catch(_error => {
                                                    setTreeSpinning(false)
                                                })
                                            }}
                                    />
                                    <Button shape="circle" icon={<EditOutlined/>} size="small"
                                            style={{marginLeft: '8px', border: 'none'}}
                                            onClick={() => {
                                                add(nodeData.key);
                                            }}
                                    />
                                </div>)
                            } else {
                                return nodeData.title
                            }
                        }}
                        blockNode={true}
                    />
                </Spin>
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
