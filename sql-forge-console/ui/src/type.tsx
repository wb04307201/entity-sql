export function DictJoinType() {}

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
  columnSize: number;
  decimalDigits: number;
  remarks: string;
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
  isPrimaryKey: boolean;
  isTableable: boolean;
  isSearchable: boolean;
  isShowCheck: boolean;
  isInsertable: boolean;
  isUpdatable: boolean;
  join: JoinInfo;
}

export type JoinType = 'dict';

export interface BaseJoinInfo {
  joinType: JoinType;
}

export interface DictJoinInfo extends BaseJoinInfo {
  joinType: 'dict';
  dict?: string;
}

export type JoinInfo = DictJoinInfo | undefined;
