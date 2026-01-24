import {DataType, Index, PrimaryKey} from '../../type';

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

export const getPrimaryKey = (
  primaryKeys: PrimaryKey[]
): string | undefined => {
  if (primaryKeys.length > 0) {
    return primaryKeys[0].columnName;
  } else {
    return 'ID';
  }
};

export const getIndex = (
  primaryKey: string | undefined,
  indexes: Index[],
  excludeColumnName: string[] = []
): string | undefined => {
  const indexNames =
    indexes
      .filter(item => !item.nonUnique)
      .map(item => item.indexName)
      .filter(
        (item, index, self) => index === self.findIndex(t => t === item)
      ) || [];
  const tempIndexes =
    indexNames
      .map(item => {
        const temp = {indexName: item, columns: []};
        indexes
          .filter(index => index.indexName === item)
          .forEach(index => {
            if (!excludeColumnName.includes(index.columnName))
              temp.columns.push(index.columnName);
          });
        return temp;
      })
      .filter(item => (item.columns.length = 1))
      .map(item => {
        return {indexNames: item.indexName, columnName: item.columns[0]};
      }) || [];
  return (
    tempIndexes.find(item => item.columnName != primaryKey)?.columnName ||
    primaryKey
  );
};

export const buildSingleTable = (
  componentId: string,
  table: string,
  tableData: DataType[],
  customSelectData: any | undefined = undefined,
  hideColumns: string[] = [],
  disabledInsert:string[] = [],
  disabledUpdate:string[] = [],
) => {
  let selectData = customSelectData;
  if (!selectData) {
    selectData = {
      '@column': tableData
        .filter(item => item.isPrimaryKey || item.isTableable)
        .map(item => {
          if (
            item.isTableable &&
            item.join &&
            item.join.joinType === 'dict' &&
            item.join.dict
          ) {
            return `${item.join.dict}.${ITEM_NAME} as ${item.columnName}`;
          } else {
            return `${table}.${item.columnName}`;
          }
        }),
      '@where': tableData
        .filter(item => item.isTableable && item.isSearchable)
        .map(item => {
          if (item.join && item.join.joinType === 'dict' && item.join.dict) {
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
        .filter(
          item =>
            item.isTableable &&
            item.join &&
            item.join.joinType === 'dict' &&
            item.join.dict
        )
        .map(item => {
          return {
            type: 'LEFT_OUTER_JOIN',
            joinTable: `${SYS_DICT_ITEM} ${item.join.dict}`,
            on: `${table}.${item.columnName} = ${item.join.dict}.${ITEM_CODE} and ${item.join.dict}.${DICT_CODE} = '${item.join.dict}'`
          };
        })
    };
  }

  let sourceDict: {[key: string]: any} = {};
  tableData
    .filter(
      item =>
        item.isTableable &&
        item.join &&
        item.join.joinType === 'dict' &&
        item.join.dict
    )
    .forEach(item => {
      sourceDict[item.join.dict] = {
        method: 'post',
        url: `/sql/forge/api/json/select/${SYS_DICT_ITEM}`,
        data: {
          '@column': [ITEM_CODE, ITEM_NAME],
          '@where': [
            {
              column: DICT_CODE,
              condition: 'EQ',
              value: item.join.dict
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
          precision: item.decimalDigits,
          disabled: disabledInsert.includes(item.columnName)
        };
      } else if (item.join && item.join.joinType === 'dict' && item.join.dict) {
        return {
          type: 'select',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          maxLength: item.columnSize,
          source: sourceDict[item.join.dict],
          clearable: true,
          disabled: disabledInsert.includes(item.columnName)
        };
      } else {
        return {
          type: 'input-text',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          maxLength: item.columnSize,
          disabled: disabledInsert.includes(item.columnName)
        };
      }
    });

  const updateForm = tableData
    .filter(item => item.isTableable && item.isUpdatable)
    .map(item => {
      if (isNumberJavaSqlType(item.javaSqlType)) {
        return {
          type: 'input-number',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          precision: item.decimalDigits,
          disabled: disabledUpdate.includes(item.columnName)
        };
      } else if (item.join && item.join.joinType === 'dict' && item.join.dict) {
        return {
          type: 'select',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          maxLength: item.columnSize,
          source: sourceDict[item.join.dict],
          clearable: true,
          disabled: disabledUpdate.includes(item.columnName)
        };
      } else {
        return {
          type: 'input-text',
          name: `${item.columnName}`,
          label: `${item.remarks ? item.remarks : item.columnName}`,
          maxLength: item.columnSize,
          disabled: disabledUpdate.includes(item.columnName)
        };
      }
    });

  const showCheckColumns =
    tableData.filter(item => item.isTableable && item.isShowCheck) || [];
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
        let col = {
          name: item.columnName,
          label: item.remarks ? item.remarks : item.columnName,
          sortable: true,
          align: 'right',
          hidden:hideColumns.includes(item.columnName),
          searchable: undefined
        };
        if (item.isSearchable) {
          col.searchable = {
            type: 'input-number',
            name: item.columnName,
            label: item.remarks ? item.remarks : item.columnName,
            precision: item.decimalDigits,
            placeholder: `输入${item.remarks ? item.remarks : item.columnName}`
          };
        }
        return col;
      } else {
        const col = {
          name: item.columnName,
          label: item.remarks ? item.remarks : item.columnName,
          sortable: true,
          hidden: hideColumns.includes(item.columnName),
          searchable: undefined
        };
        if (
          item.isSearchable &&
          item.join &&
          item.join.joinType === 'dict' &&
          item.join.dict
        ) {
          col.searchable = {
            type: 'select',
            name: item.columnName,
            label: item.remarks ? item.remarks : item.columnName,
            maxLength: item.columnSize,
            placeholder: `输入${item.remarks ? item.remarks : item.columnName}`,
            source: sourceDict[item.join.dict],
            clearable: true
          };
        } else {
          col.searchable = {
            type: 'input-text',
            name: item.columnName,
            label: item.remarks ? item.remarks : item.columnName,
            maxLength: item.columnSize,
            placeholder: `输入${item.remarks ? item.remarks : item.columnName}`
          };
        }
        return col;
      }
    });

  return {
    type: 'crud',
    id: `${componentId}`,
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
                    componentId: `${componentId}`
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
                        componentId: `${componentId}`
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

export const buildMainDetailTable = (
  componentId: string,
  table: string,
  column: string,
  tableData: DataType[],
  detailComponentId: string,
  detailTable: string,
  detailColumn: string,
  detailTableData: DataType[]
) => {
  const mainCrudTable = {
    ...buildSingleTable(componentId, table, tableData),
    onEvent: {
      rowClick: {
        actions: [
          {
            actionType: 'reload',
            componentId: `${detailComponentId}`,
            data: {
              [detailColumn]: `$\{event.data.item.${column}\}`
            }
          }
        ]
      }
    }
  };

  const detailCustomSelectData = {
    '@column': detailTableData
      .filter(item => item.isPrimaryKey || item.isTableable)
      .map(item => {
        if (
          item.isTableable &&
          item.join &&
          item.join.joinType === 'dict' &&
          item.join.dict
        ) {
          return `${item.join.dict}.${ITEM_NAME} as ${item.columnName}`;
        } else {
          return `${detailTable}.${item.columnName}`;
        }
      }),
    '@where': [
      {
        column: `${detailColumn}`,
        condition: 'EQ',
        value: `$\{${column} | default:\"\"\}`
      }
    ],
    '@join': detailTableData
      .filter(
        item =>
          item.isTableable &&
          item.join &&
          item.join.joinType === 'dict' &&
          item.join.dict
      )
      .map(item => {
        return {
          type: 'LEFT_OUTER_JOIN',
          joinTable: `${SYS_DICT_ITEM} ${item.join.dict}`,
          on: `${table}.${item.columnName} = ${item.join.dict}.${ITEM_CODE} and ${item.join.dict}.${DICT_CODE} = '${item.join.dict}'`
        };
      })
  };

  const detailCrudTable = buildSingleTable(
    detailComponentId,
    detailTable,
    detailTableData,
    detailCustomSelectData,
    [detailColumn],
    [detailColumn],
    [detailColumn]
  );

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
        body: mainCrudTable
      },
      {
        style: {
          width: '50%',
          height: '100%'
        },
        type: 'wrapper',
        body: detailCrudTable
      }
    ]
  };
};
