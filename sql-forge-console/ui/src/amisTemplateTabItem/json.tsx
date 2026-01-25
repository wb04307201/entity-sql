export const crudJson = {
  type: 'page',
  body: {
    type: 'crud',
    id: 'crud_users',
    api: {
      method: 'post',
      url: '/sql/forge/api/json/selectPage/users',
      data: {
        '@where': [
          {
            column: 'USERNAME',
            condition: 'LIKE',
            value: '${USERNAME}'
          },
          {
            column: 'EMAIL',
            condition: 'LIKE',
            value: '${EMAIL}'
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
              url: '/sql/forge/api/json/insert/users',
              data: {
                '@set': '$$'
              }
            },
            onEvent: {
              submitSucc: {
                actions: [
                  {
                    actionType: 'reload',
                    componentId: 'crud_users'
                  }
                ]
              }
            },
            body: [
              {
                type: 'uuid',
                name: 'ID'
              },
              {
                type: 'input-text',
                name: 'USERNAME',
                label: '用户名'
              },
              {
                type: 'input-text',
                name: 'EMAIL',
                label: '邮箱'
              }
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
          url: '/sql/forge/api/json/select/users',
          data: {
            '@where': [
              {
                column: 'USERNAME',
                condition: 'LIKE',
                value: '${USERNAME}'
              },
              {
                column: 'EMAIL',
                condition: 'LIKE',
                value: '${EMAIL}'
              }
            ]
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
          url: '/sql/forge/api/json/delete/users',
          data: {
            '@where': [
              {
                column: 'ID',
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
    labelTpl: '${USERNAME}',
    autoFillHeight: true,
    autoGenerateFilter: true,
    showIndex: true,
    primaryField: 'ID',
    columns: [
      {
        name: 'ID',
        label: 'ID',
        hidden: true
      },
      {
        name: 'USERNAME',
        label: '用户名',
        searchable: {
          type: 'input-text',
          name: 'USERNAME',
          label: '用户名',
          placeholder: '输入用户名'
        },
        sortable: true
      },
      {
        name: 'EMAIL',
        label: '邮箱',
        searchable: {
          type: 'input-text',
          name: 'EMAIL',
          label: '邮箱',
          placeholder: '输入邮箱'
        },
        sortable: true
      },
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
                  url: '/sql/forge/api/json/select/users',
                  data: {
                    '@where': [
                      {
                        column: 'ID',
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
                  url: '/sql/forge/api/json/update/users',
                  data: {
                    '@set': '$$',
                    '@where': [
                      {
                        column: 'ID',
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
                        componentId: 'crud_users'
                      }
                    ]
                  }
                },
                body: [
                  {
                    type: 'input-text',
                    name: 'USERNAME',
                    label: '用户名'
                  },
                  {
                    type: 'input-text',
                    name: 'EMAIL',
                    label: '邮箱'
                  }
                ]
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
              url: '/sql/forge/api/json/delete/users',
              data: {
                '@where': [
                  {
                    column: 'ID',
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
  }
};

export const chartJson = {
  type: 'page',
  body: {
    type: 'chart',
    api: {
      method: 'post',
      url: '/sql/forge/api/calcite/execute/ApiCalciteTemplate-test',
      data: {
        ids: [1, 2, 3, 4, 5, 6, 7]
      }
    },
    height: '100vh',
    config: {
      xAxis: {
        type: 'category',
        data: '${items | pick:name}'
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: '${items | pick:grade}',
          type: 'bar'
        }
      ]
    }
  }
};