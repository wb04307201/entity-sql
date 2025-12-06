package cn.wubo.sql.forge;


import cn.wubo.sql.forge.map.ParamMap;
import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Executor(DataSource dataSource) {

    private void buildPrepareStatement(@NotNull PreparedStatement preparedStatement, ParamMap params) throws SQLException {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                Integer index = entry.getKey();
                Object value = entry.getValue();

                if (index == null) {
                    throw new SQLException("Parameter index cannot be null");
                }

                if (index <= 0) {
                    throw new SQLException("Parameter index must be positive, got: " + entry.getKey());
                }

                preparedStatement.setObject(index, value);
            }
        }
    }


    private List<RowMap> resultSetToList(@NonNull ResultSet rs) throws SQLException {
        List<RowMap> list = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        try {
            if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
                rs.last();
                int rowCount = rs.getRow();
                if (rowCount > 0) {
                    list = new ArrayList<>(rowCount);
                }
                rs.beforeFirst();
            }
        } catch (SQLException e) {
            list = new ArrayList<>();
        }

        while (rs.next()) {
            RowMap row = new RowMap(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }
            list.add(row);
        }

        return list;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    public List<RowMap> executeQuery(@Valid SqlScript sqlScript) throws
            SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSetToList(resultSet);
                }
            }
        }
    }

    public Object executeInsert(@Valid SqlScript sqlScript) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());

                preparedStatement.executeUpdate();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        return generatedKeys.getObject(1);
                    else
                        return null;
                }
            }
        }
    }

    public int executeUpdate(@Valid SqlScript sqlScript) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());
                return preparedStatement.executeUpdate();
            }
        }
    }

    public long executeLargeUpdate(@Valid SqlScript sqlScript) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());
                if (preparedStatement.isWrapperFor(PreparedStatement.class))
                    return preparedStatement.executeLargeUpdate();
                else
                    return preparedStatement.executeUpdate();
            }
        }
    }

    public Object[] executeBatchInsert(@NotBlank String sql, List<ParamMap> paramsList) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (ParamMap params : paramsList) {
                    buildPrepareStatement(preparedStatement, params);
                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    Object[] ids = new Object[paramsList.size()];
                    int index = 0;
                    while (generatedKeys.next() && index < ids.length) {
                        ids[index++] = generatedKeys.getObject(1);
                    }
                    return ids;
                }
            }
        }
    }

    public int[] executeBatch(@NotBlank String sql, List<ParamMap> paramsList) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (ParamMap params : paramsList) {
                    buildPrepareStatement(preparedStatement, params);
                    preparedStatement.addBatch();
                }

                return preparedStatement.executeBatch();
            }
        }
    }

    public Object execute(@Valid SqlScript sqlScript) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());
                boolean isResultSet = preparedStatement.execute();
                if (isResultSet) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        return resultSetToList(resultSet);
                    }
                } else {
                    return preparedStatement.getUpdateCount();
                }
            }
        }
    }

    public List<Object> executeByLine(@Valid List<SqlScript> sqlScripts) throws SQLException {
        List<Object> list = new ArrayList<>();
        for (SqlScript sqlScript : sqlScripts) {
            list.add(execute(sqlScript));
        }
        return list;
    }
}
