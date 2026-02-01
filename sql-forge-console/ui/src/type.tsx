import {useState} from 'react';

export interface DatabaseInfo {
  databaseInfo: unknown;
  schemaTableTypeTables: SchemaTableTypeTable[];
}

export interface SchemaTableTypeTable {
  schema: {tableSchema: string};
  tableTypeTables: TableTypeTable[];
}

export interface TableTypeTable {
  tableType: string;
  tables: TableColumn[];
}

export interface TableColumn {
  table: {tableName: string};
  columns: ColumnInfo[];
  primaryKeys: PrimaryKey[];
  indexes: Index[];
}

export interface PrimaryKey {
  columnName: string;
}

export interface Index {
  indexName: string;
  columnName: string;
  nonUnique: boolean;
}

export interface ColumnInfo {
  columnName: string;
  dataType: number;
  javaSqlType: string;
  typeName: string;
  columnSize?: number;
  decimalDigits?: number;
  remarks?: string;
}

export interface AmisTemplateCrudMethods {
  getContext: () => string | undefined;
  getApiTemplateId: () => string | undefined;
}

export interface AmisTemplateCrudProps {}

export interface OptionType {
  label: string;
  value: string;
}

export interface DataType extends ColumnInfo {
  key: string;
  tableName: string;
  columnType: 'origin' | 'join';
  primary?: boolean;
  table?: boolean;
  table_hidden?: boolean;
  search?: boolean;
  check?: boolean;
  add?: boolean;
  add_hidden?: boolean;
  add_disabled?: boolean;
  edit?: boolean;
  edit_hidden?: boolean;
  edit_disabled?: boolean;
  join?: JoinInfo;
}

export type checkBoxItemsKey =
  | 'primary'
  | 'table'
  | 'table_hidden'
  | 'search'
  | 'check'
  | 'add'
  | 'add_hidden'
  | 'add_disabled'
  | 'edit'
  | 'edit_hidden'
  | 'edit_disabled';

export const checkBoxItems: {
  key: checkBoxItemsKey;
  label: string;
  slabel: string;
}[] = [
  {key: 'primary', label: '主键', slabel: '主'},
  {key: 'table', label: '表格', slabel: '表'},
  {key: 'table_hidden', label: '表格隐藏', slabel: '隐'},
  {key: 'search', label: '查询', slabel: '查'},
  {key: 'check', label: '选择', slabel: '选'},
  {key: 'add', label: '新增', slabel: '新'},
  {key: 'add_hidden', label: '新增隐藏', slabel: '隐'},
  {key: 'add_disabled', label: '新增禁用', slabel: '禁'},
  {key: 'edit', label: '修改', slabel: '改'},
  {key: 'edit_hidden', label: '修改隐藏', slabel: '隐'},
  {key: 'edit_disabled', label: '修改禁用', slabel: '禁'}
];

export type JoinType = 'dict' | 'table';

export interface BaseJoinInfo {
  joinType: JoinType;
  alias?: string;
}

export interface DictJoinInfo extends BaseJoinInfo {
  joinType: 'dict';
  dict?: string;
}

export interface TableJoinInfo extends BaseJoinInfo {
  joinType: 'table';
  schema?: string;
  table?: string;
  onColumn?: string;
  selectColumn?: string;
  extraSelectColumns?: [];
}

export type JoinInfo = DictJoinInfo | TableJoinInfo | undefined;
