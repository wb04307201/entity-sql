export function SchemaTableTypeTableInfo() {
}

export interface DatabaseInfo {
    databaseInfo: unknown,
    schemaTableTypeTables: SchemaTableTypeTable[]
}

export interface SchemaTableTypeTable {
    schema: { tableSchema: string },
    tableTypeTables: TableTypeTable[]
}

export interface TableTypeTable{
    tableType: string,
    tables: TableColumn[]
}

export interface TableColumn {
    table: { tableName: string },
    columns: ColumnInfo[],
    primaryKeys: { columnName: string }[]
}

export interface ColumnInfo {
    columnName:string,
    typeName:string,
    columnSize:number,
    decimalDigits:number,
    remarks:string
}

export interface AmisTemplateCrudMethods {
    getContext: () => string;
}

export interface OptionType {
    label:string,
    value:string
}


