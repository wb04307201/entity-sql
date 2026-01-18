import {DataType} from '../../type';

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

export const buildSingleTable = (
  table: string,
  primaryKey: DataType,
  tableColumns: DataType[],
  searchableColumns: DataType[],
  showCheckColumns: DataType[],
  insertableColumns: DataType[],
  updatableColumns: DataType[]
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

  return {
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
    labelTpl: showCheckColumns != null && showCheckColumns.length > 0 ? `$\{${showCheckColumns.map(item => item.columnName).join(' - ')}\}` : `$\{${primaryKey.columnName}\}`,
    autoFillHeight: true,
    autoGenerateFilter: true,
    showIndex: true,
    primaryField: primaryKey.columnName,
    columns: columns
  };
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
            detailShowCheckColumns != null &&
            detailShowCheckColumns.length > 0
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
