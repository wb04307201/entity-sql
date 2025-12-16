package cn.wubo.sql.forge;


import cn.wubo.sql.forge.map.ParamMap;
import cn.wubo.sql.forge.map.RowMap;
import cn.wubo.sql.forge.records.SqlScript;
import cn.wubo.sql.forge.utils.ResultSetUtils;
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

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    public List<RowMap> executeQuery(@Valid SqlScript sqlScript) throws
            SQLException {
        Connection connection = getConnection();
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return ResultSetUtils.resultSetToList(resultSet);
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Object executeInsert(@Valid SqlScript sqlScript) throws SQLException {
        Connection connection = getConnection();
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql(), Statement.RETURN_GENERATED_KEYS)) {
                buildPrepareStatement(preparedStatement, sqlScript.params());

                preparedStatement.executeUpdate();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        return generatedKeys.getObject(1);
                    else
                        return null;
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public int executeUpdate(@Valid SqlScript sqlScript) throws SQLException {
        Connection connection = getConnection();
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());
                return preparedStatement.executeUpdate();
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public long executeLargeUpdate(@Valid SqlScript sqlScript) throws SQLException {
        Connection connection = getConnection();
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());
                if (preparedStatement.isWrapperFor(PreparedStatement.class))
                    return preparedStatement.executeLargeUpdate();
                else
                    return preparedStatement.executeUpdate();
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Object[] executeBatchInsert(@NotBlank String sql, List<ParamMap> paramsList) throws SQLException {
        Connection connection = getConnection();
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public int[] executeBatch(@NotBlank String sql, List<ParamMap> paramsList) throws SQLException {
        Connection connection = getConnection();
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (ParamMap params : paramsList) {
                    buildPrepareStatement(preparedStatement, params);
                    preparedStatement.addBatch();
                }

                return preparedStatement.executeBatch();
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Object execute(@Valid SqlScript sqlScript) throws SQLException {
        Connection connection = getConnection();
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlScript.sql())) {
                buildPrepareStatement(preparedStatement, sqlScript.params());
                boolean isResultSet = preparedStatement.execute();
                if (isResultSet) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        return ResultSetUtils.resultSetToList(resultSet);
                    }
                } else {
                    return preparedStatement.getUpdateCount();
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
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
