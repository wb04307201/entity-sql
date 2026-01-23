export const buildTableData = (data: any[] | {rows: any[]} | any) => {
  let rows;
  if (Array.isArray(data)) {
    rows = data;
  } else if (data.rows) {
    rows = data.rows;
  }
  if (rows) {
    const row = rows[0];
    const columns = [];
    for (const key in row) {
      columns.push({
        title: key,
        dataIndex: key,
        key: key
      });
    }

    return {columns: columns, rows: rows};
  } else {
    const columns = [];
    for (const key in data) {
      columns.push({
        title: key,
        dataIndex: key,
        key: key
      });
    }
    return {columns: columns, rows: rows};
  }
};
