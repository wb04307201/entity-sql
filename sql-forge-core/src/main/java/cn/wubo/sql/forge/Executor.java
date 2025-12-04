package cn.wubo.sql.forge;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Executor(DataSource dataSource) {

    private void buildPrepareStatement(@NotNull PreparedStatement preparedStatement, Map<Integer, Object> params) throws SQLException {
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


    private List<Map<String, Object>> resultSetToList(@NonNull ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            rs.last();
            int rowCount = rs.getRow();
            if (rowCount > 0) {
                list = new ArrayList<>(rowCount);
            }
            rs.beforeFirst();
        }

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
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

    public List<Map<String, Object>> executeQuery(@NotBlank String sql, Map<Integer, Object> params) throws
            SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                buildPrepareStatement(preparedStatement, params);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSetToList(resultSet);
                }
            }
        }
    }


    public int executeUpdate(String sql, Map<Integer, Object> params) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                buildPrepareStatement(preparedStatement, params);
                return preparedStatement.executeUpdate();
            }
        }
    }

    public Object execute(String sql, Map<Integer, Object> params) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                buildPrepareStatement(preparedStatement, params);
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
}
