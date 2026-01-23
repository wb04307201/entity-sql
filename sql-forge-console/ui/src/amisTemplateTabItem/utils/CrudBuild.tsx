import {DataType} from '../../type';
import {crudJson} from './json';

export const isNumberJavaSqlType = (javaSqlType: string): boolean => {
  return (
    javaSqlType === 'INTEGER' ||
    javaSqlType === 'BIGINT' ||
    javaSqlType === 'SMALLINT' ||
    javaSqlType === 'TINYINT' ||
    javaSqlType === 'NUMERIC' ||
    javaSqlType === 'DECIMAL' ||
    javaSqlType === 'FLOAT' ||
    javaSqlType === 'DOUBLE'
  );
};
const SYS_DICT_ITEM = 'sys_dict_item';
const ITEM_CODE = 'item_code';
const ITEM_NAME = 'item_name';
const DICT_CODE = 'dict_code';


export const buildSingleTable = (table: string, tableData: DataType[]) => {
  const selectData = {
    '@column': tableData
      .filter(item => item.isPrimaryKey || item.isTableable)
      .map(item => {
        if (item.isTableable && item.dict) {
          return `${item.dict}.${ITEM_NAME} as ${item.columnName}`;
        } else {
          return `${table}.${item.columnName}`;
        }
      }),
    '@where': tableData
      .filter(item => item.isTableable && item.isSearchable)
      .map(item => {
        if (item.dict) {
          return {
            column: item.columnName,
            condition: 'EQ',
            value: '${' + item.columnName + ' | default:undefined}'
          };
        } else {
          return {
            column: item.columnName,
            condition: 'LIKE',
            value: '${' + item.columnName + ' | default:undefined}'
          };
        }
      }),
    '@join': tableData
      .filter(item => item.isTableable && item.dict)
      .map(item => {
        return {
          type: 'LEFT_OUTER_JOIN',
          joinTable: `${SYS_DICT_ITEM} ${item.dict}`,
          on: `${table}.${item.columnName} = ${item.dict}.${ITEM_CODE} and ${item.dict}.${DICT_CODE} = '${item.dict}'`
        };
      })
  };

  let sourceDict: {[key: string]: any} = {};
  tableData
    .filter(item => item.isTableable && item.dict)
    .forEach(item => {
      sourceDict[item.dict] = {
        method: 'post',
        url: `/sql/forge/api/json/select/${SYS_DICT_ITEM}`,
        data: {
          '@column': [ITEM_CODE, ITEM_NAME],
          '@where': [
            {
              column: DICT_CODE,
              condition: 'EQ',
              value: item.dict
            }
          ]
        },
        adaptor: `return {\n  options: payload.map(item => ({\n    value: item.${ITEM_CODE.toLowerCase()} || item.${ITEM_CODE.toUpperCase()},\n    label: item.${ITEM_NAME.toLowerCase()} ||  item.${ITEM_NAME.toUpperCase()}\n  }))\n};`
      };
    });

  const insertForm = tableData
    .filter(
      item => item.isPrimaryKey || (item.isTableable && item.isInsertable)
    )
    .map(item => {
      if (item.isPrimaryKey) {
        return {
          type: 'uuid',
          name: `${item.columnName}`
        };
      } else if (isNumberJavaSqlType(item.javaSqlType)) {
        return {
          type: 'input-number',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          precision: item.decimalDigits
        };
      } else if (item.dict) {
        return {
          type: 'select',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          maxLength: item.columnSize,
          source: sourceDict[item.dict],
          clearable: true
        };
      } else {
        return {
          type: 'input-text',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          maxLength: item.columnSize
        };
      }
    });

  const updateForm = tableData
    .filter(
      item => (item.isTableable && item.isUpdatable)
    )
    .map(item => {
      if (isNumberJavaSqlType(item.javaSqlType)) {
        return {
          type: 'input-number',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          precision: item.decimalDigits
        };
      } else if (item.dict) {
        return {
          type: 'select',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          maxLength: item.columnSize,
          source: sourceDict[item.dict],
          clearable: true
        };
      } else {
        return {
          type: 'input-text',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          maxLength: item.columnSize
        };
      }
    });

  const showCheckColumns = tableData.filter(item => item.isTableable && item.isShowCheck) || [];
  const primaryField = tableData.find(item => item.isPrimaryKey)?.columnName;
  const labelTpl =
    showCheckColumns.length > 0
      ? `$\{${showCheckColumns.map(item => item.columnName).join(' - ')}\}`
      : `$\{${tableData.find(item => item.isPrimaryKey)?.columnName}\}`;

  const columns = tableData
    .filter(item => item.isPrimaryKey || item.isTableable)
    .map(item => {
      if (item.isPrimaryKey) {
        return {
          name: item.columnName,
          label: item.remarks ? item.remarks : item.columnName,
          hidden: true
        };
      } else if (isNumberJavaSqlType(item.javaSqlType)) {
        let col=  {
          name: item.columnName,
          label: item.remarks ? item.remarks : item.columnName,
          sortable: true,
          align: 'right',
          searchable: undefined
        }
        if (item.isSearchable){
          col.searchable = {
            type: 'input-number',
            name: item.columnName,
            label: item.remarks ? item.remarks : item.columnName,
            precision: item.decimalDigits,
            placeholder: `输入${
              item.remarks ? item.remarks : item.columnName
            }`
          };
        }
        return col
      }else {
        const col = {
          name: item.columnName,
          label: item.remarks ? item.remarks : item.columnName,
          sortable: true,
          searchable: undefined
        };
        if (item.isSearchable && item.dict){
          col.searchable = {
            type: 'select',
            name: item.columnName,
            label: item.remarks ? item.remarks : item.columnName,
            maxLength: item.columnSize,
            placeholder: `输入${
              item.remarks ? item.remarks : item.columnName
            }`,
            source: sourceDict[item.dict],
            clearable: true
          }
        }else {
          col.searchable = {
            type: 'input-text',
            name: item.columnName,
            label: item.remarks ? item.remarks : item.columnName,
            maxLength: item.columnSize,
            placeholder: `输入${
              item.remarks ? item.remarks : item.columnName
            }`
          }
        }
        return col
      }
    });

  return crudJson(table, selectData, insertForm, labelTpl, primaryField, columns,updateForm)
};

export const buildMainDetailTable = (
  table: string,
  column: string,
  primaryKey: DataType,
  tableColumns: DataType[],
  searchableColumns: DataType[],
  showCheckColumns: DataType[],
  insertableColumns: DataType[],
  updatableColumns: DataType[],
  detailTable: string,
  detailColumn: string,
  detailTablePrimaryKey: DataType,
  detailTableColumns: DataType[],
  detailShowCheckColumns: DataType[],
  detailInsertableColumns: DataType[],
  detailUpdatableColumns: DataType[]
) => {
  const where = searchableColumns.map(item => {
    return {
      column: item.columnName,
      condition: 'LIKE',
      value: '${' + item.columnName + ' | default:undefined}'
    };
  });

  const columns = [
    {
      name: primaryKey.columnName,
      label: primaryKey.columnName,
      hidden: true
    },
    ...tableColumns.map(item => {
      if (item.isSearchable) {
        if (isNumberJavaSqlType(item.javaSqlType)) {
          return {
            name: item.columnName,
            label: item.remarks ? item.remarks : item.columnName,
            searchable: {
              type: 'input-number',
              name: item.columnName,
              label: item.remarks ? item.remarks : item.columnName,
              precision: item.decimalDigits,
              placeholder: `输入${
                item.remarks ? item.remarks : item.columnName
              }`
            },
            sortable: true,
            align: 'right'
          };
        } else {
          return {
            name: item.columnName,
            label: item.remarks ? item.remarks : item.columnName,
            searchable: {
              type: 'input-text',
              name: item.columnName,
              label: item.remarks ? item.remarks : item.columnName,
              maxLength: item.columnSize,
              placeholder: `输入${
                item.remarks ? item.remarks : item.columnName
              }`
            },
            sortable: true
          };
        }
      } else {
        return {
          name: item.columnName,
          label: item.remarks ? item.remarks : item.columnName,
          sortable: true
        };
      }
    }),
    {
      type: 'operation',
      label: '操作',
      buttons: [
        {
          label: '修改',
          type: 'button',
          icon: 'fa fa-pen-to-square',
          actionType: 'drawer',
          drawer: {
            title: '新增表单',
            body: {
              type: 'form',
              initApi: {
                method: 'post',
                url: `/sql/forge/api/json/select/${table}`,
                data: {
                  '@where': [
                    {
                      column: primaryKey.columnName,
                      condition: 'EQ',
                      value: '${ID}'
                    }
                  ]
                },
                responseData: {
                  '&': '${items | first}'
                }
              },
              api: {
                method: 'post',
                url: `/sql/forge/api/json/update/${table}`,
                data: {
                  '@set': '$$',
                  '@where': [
                    {
                      column: primaryKey.columnName,
                      condition: 'EQ',
                      value: '${ID}'
                    }
                  ]
                }
              },
              onEvent: {
                submitSucc: {
                  actions: [
                    {
                      actionType: 'reload',
                      componentId: 'crud_table'
                    }
                  ]
                }
              },
              body: updatableColumns.map(item => {
                if (isNumberJavaSqlType(item.javaSqlType)) {
                  return {
                    type: 'input-number',
                    name: `${item.columnName}`,
                    label: `${item.remarks ? item.remarks : item.columnName}`,
                    precision: item.decimalDigits
                  };
                } else {
                  return {
                    type: 'input-text',
                    name: `${item.columnName}`,
                    label: `${item.remarks ? item.remarks : item.columnName}`,
                    maxLength: item.columnSize
                  };
                }
              })
            }
          }
        },
        {
          label: '删除',
          type: 'button',
          icon: 'fa fa-minus',
          actionType: 'ajax',
          level: 'danger',
          confirmText: '确认要删除？',
          api: {
            method: 'post',
            url: `/sql/forge/api/json/delete/${table}`,
            data: {
              '@where': [
                {
                  column: primaryKey.columnName,
                  condition: 'EQ',
                  value: '${ID}'
                }
              ]
            }
          }
        }
      ],
      fixed: 'right'
    }
  ];

  const detailColumns = [
    {
      name: detailTablePrimaryKey.columnName,
      label: detailTablePrimaryKey.columnName,
      hidden: true
    },
    ...detailTableColumns.map(item => {
      return {
        name: item.columnName,
        label: item.remarks ? item.remarks : item.columnName,
        hidden: item.columnName === detailColumn,
        sortable: true
      };
    }),
    {
      type: 'operation',
      label: '操作',
      buttons: [
        {
          label: '修改',
          type: 'button',
          icon: 'fa fa-pen-to-square',
          actionType: 'drawer',
          drawer: {
            title: '新增表单',
            body: {
              type: 'form',
              initApi: {
                method: 'post',
                url: `/sql/forge/api/json/select/${detailTable}`,
                data: {
                  '@where': [
                    {
                      column: detailTablePrimaryKey.columnName,
                      condition: 'EQ',
                      value: '${ID}'
                    }
                  ]
                },
                responseData: {
                  '&': '${items | first}'
                }
              },
              api: {
                method: 'post',
                url: `/sql/forge/api/json/update/${detailTable}`,
                data: {
                  '@set': '$$',
                  '@where': [
                    {
                      column: primaryKey.columnName,
                      condition: 'EQ',
                      value: '${ID}'
                    }
                  ]
                }
              },
              onEvent: {
                submitSucc: {
                  actions: [
                    {
                      actionType: 'reload',
                      componentId: 'detail_table'
                    }
                  ]
                }
              },
              body: detailUpdatableColumns.map(item => {
                if (isNumberJavaSqlType(item.javaSqlType)) {
                  return {
                    type: 'input-number',
                    name: `${item.columnName}`,
                    label: `${item.remarks ? item.remarks : item.columnName}`,
                    disabled: item.columnName == detailColumn,
                    precision: item.decimalDigits
                  };
                } else {
                  return {
                    type: 'input-text',
                    name: `${item.columnName}`,
                    label: `${item.remarks ? item.remarks : item.columnName}`,
                    disabled: item.columnName == detailColumn,
                    maxLength: item.columnSize
                  };
                }
              })
            }
          }
        },
        {
          label: '删除',
          type: 'button',
          icon: 'fa fa-minus',
          actionType: 'ajax',
          level: 'danger',
          confirmText: '确认要删除？',
          api: {
            method: 'post',
            url: `/sql/forge/api/json/delete/${detailTable}`,
            data: {
              '@where': [
                {
                  column: detailTablePrimaryKey.columnName,
                  condition: 'EQ',
                  value: '${ID}'
                }
              ]
            }
          }
        }
      ],
      fixed: 'right'
    }
  ];

  return {
    type: 'flex',
    style: {
      width: '100%',
      height: '100%'
    },
    items: [
      {
        style: {
          width: '50%',
          height: '100%'
        },
        type: 'wrapper',
        body: {
          type: 'crud',
          id: 'crud_table',
          api: {
            method: 'post',
            url: `/sql/forge/api/json/selectPage/${table}`,
            data: {
              '@where': where,
              '@order': [
                "${default(orderBy && orderDir ? (orderBy + ' ' + orderDir):'',undefined)}"
              ],
              '@page': {
                pageIndex: '${page - 1}',
                pageSize: '${perPage}'
              }
            }
          },
          headerToolbar: [
            {
              label: '新增',
              type: 'button',
              icon: 'fa fa-plus',
              level: 'primary',
              actionType: 'drawer',
              drawer: {
                title: '新增表单',
                body: {
                  type: 'form',
                  api: {
                    method: 'post',
                    url: `/sql/forge/api/json/insert/${table}`,
                    data: {
                      '@set': '$$'
                    }
                  },
                  onEvent: {
                    submitSucc: {
                      actions: [
                        {
                          actionType: 'reload',
                          componentId: 'crud_table'
                        }
                      ]
                    }
                  },
                  body: [
                    {
                      type: 'uuid',
                      name: `${primaryKey.columnName}`
                    },
                    ...insertableColumns.map(item => {
                      if (isNumberJavaSqlType(item.javaSqlType)) {
                        return {
                          type: 'input-number',
                          name: `${item.columnName}`,
                          label: `${
                            item.remarks ? item.remarks : item.columnName
                          }`,
                          precision: item.decimalDigits
                        };
                      } else {
                        return {
                          type: 'input-text',
                          name: `${item.columnName}`,
                          label: `${
                            item.remarks ? item.remarks : item.columnName
                          }`,
                          maxLength: item.columnSize
                        };
                      }
                    })
                  ]
                }
              }
            },
            'bulkActions',
            {
              type: 'columns-toggler',
              align: 'right'
            },
            {
              type: 'drag-toggler',
              align: 'right'
            },
            {
              type: 'export-excel',
              label: '导出',
              icon: 'fa fa-file-excel',
              api: {
                method: 'post',
                url: `/sql/forge/api/json/select/${table}`,
                data: {
                  '@where': where
                }
              },
              align: 'right'
            }
          ],
          footerToolbar: [
            'statistics',
            {
              type: 'pagination',
              layout: 'total,perPage,pager,go'
            }
          ],
          bulkActions: [
            {
              label: '批量删除',
              icon: 'fa fa-trash',
              actionType: 'ajax',
              api: {
                method: 'post',
                url: `/sql/forge/api/json/delete/${table}`,
                data: {
                  '@where': [
                    {
                      column: primaryKey.columnName,
                      condition: 'IN',
                      value: '${ids | split}'
                    }
                  ]
                }
              },
              confirmText: '确定要批量删除?'
            }
          ],
          keepItemSelectionOnPageChange: true,
          labelTpl:
            showCheckColumns != null && showCheckColumns.length > 0
              ? `$\{${showCheckColumns
                  .map(item => item.columnName)
                  .join(' - ')}\}`
              : `$\{${primaryKey.columnName}\}`,
          autoFillHeight: true,
          autoGenerateFilter: true,
          showIndex: true,
          primaryField: primaryKey.columnName,
          onEvent: {
            rowClick: {
              actions: [
                {
                  actionType: 'reload',
                  componentId: 'detail_table',
                  data: {
                    [`${detailColumn}`]: `$\{event.data.item.${column}\}`
                  }
                }
              ]
            }
          },
          columns: columns
        }
      },
      {
        style: {
          width: '50%',
          height: '100%'
        },
        type: 'wrapper',
        body: {
          type: 'crud',
          id: 'detail_table',
          api: {
            method: 'post',
            url: `/sql/forge/api/json/selectPage/${detailTable}`,
            data: {
              '@where': [
                {
                  column: detailColumn,
                  condition: 'EQ',
                  value: `$\{${detailColumn} | default:""\}`
                }
              ],
              '@order': [
                "${default(orderBy && orderDir ? (orderBy + ' ' + orderDir):'',undefined)}"
              ],
              '@page': {
                pageIndex: '${page - 1}',
                pageSize: '${perPage}'
              }
            }
          },
          headerToolbar: [
            {
              label: '新增',
              type: 'button',
              icon: 'fa fa-plus',
              level: 'primary',
              actionType: 'drawer',
              drawer: {
                title: '新增表单',
                body: {
                  type: 'form',
                  api: {
                    method: 'post',
                    url: `/sql/forge/api/json/insert/${detailTable}`,
                    data: {
                      '@set': '$$'
                    }
                  },
                  onEvent: {
                    submitSucc: {
                      actions: [
                        {
                          actionType: 'reload',
                          componentId: 'detail_table'
                        }
                      ]
                    }
                  },
                  body: [
                    {
                      type: 'uuid',
                      name: `${primaryKey.columnName}`
                    },
                    ...detailInsertableColumns.map(item => {
                      if (isNumberJavaSqlType(item.javaSqlType)) {
                        return {
                          type: 'input-number',
                          name: `${item.columnName}`,
                          label: `${
                            item.remarks ? item.remarks : item.columnName
                          }`,
                          disabled: item.columnName == detailColumn,
                          precision: item.decimalDigits
                        };
                      } else {
                        return {
                          type: 'input-text',
                          name: `${item.columnName}`,
                          label: `${
                            item.remarks ? item.remarks : item.columnName
                          }`,
                          disabled: item.columnName == detailColumn,
                          maxLength: item.columnSize
                        };
                      }
                    })
                  ]
                }
              }
            },
            'bulkActions',
            {
              type: 'columns-toggler',
              align: 'right'
            },
            {
              type: 'drag-toggler',
              align: 'right'
            },
            {
              type: 'export-excel',
              label: '导出',
              icon: 'fa fa-file-excel',
              api: {
                method: 'post',
                url: `/sql/forge/api/json/select/${detailTable}`,
                data: {
                  '@where': where
                }
              },
              align: 'right'
            }
          ],
          footerToolbar: [
            'statistics',
            {
              type: 'pagination',
              layout: 'total,perPage,pager,go'
            }
          ],
          bulkActions: [
            {
              label: '批量删除',
              icon: 'fa fa-trash',
              actionType: 'ajax',
              api: {
                method: 'post',
                url: `/sql/forge/api/json/delete/${detailTable}`,
                data: {
                  '@where': [
                    {
                      column: detailTablePrimaryKey.columnName,
                      condition: 'IN',
                      value: '${ids | split}'
                    }
                  ]
                }
              },
              confirmText: '确定要批量删除?'
            }
          ],
          keepItemSelectionOnPageChange: true,
          labelTpl:
            detailShowCheckColumns != null && detailShowCheckColumns.length > 0
              ? `$\{${detailShowCheckColumns
                  .map(item => item.columnName)
                  .join(' - ')}\}`
              : `$\{${detailTablePrimaryKey.columnName}\}`,
          autoFillHeight: true,
          autoGenerateFilter: true,
          showIndex: true,
          primaryField: detailTablePrimaryKey.columnName,
          columns: detailColumns
        }
      }
    ]
  };
};
