export const crudJson = (
  table,
  selectData,
  insertForm,
  labelTpl,
  primaryField,
  columns,
  updateForm
) => {
  return {
    type: 'crud',
    id: 'crud_table',
    api: {
      method: 'post',
      url: `/sql/forge/api/json/selectPage/${table}`,
      data: {
        ...selectData,
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
            body: insertForm
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
          url: `/sql/forge/api/json/insert/${table}`,
          data: selectData
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
                column: primaryField,
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
    labelTpl: labelTpl,
    autoFillHeight: true,
    autoGenerateFilter: true,
    showIndex: true,
    primaryField: primaryField,
    columns: [
      ...columns,
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
                        column: primaryField,
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
                        column: primaryField,
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
                body: updateForm
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
                    column: primaryField,
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
    ]
  };
};
