package com.hl.dao;

import com.hl.dto.DataUpdateReport;
import com.hl.service.StreamCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataAccess {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public List<Double> getAllValues(String table, String column) {

        List<Double> allValues = jdbcTemplate.query("SELECT " + column + " FROM " + table, new RowMapper<Double>() {

            @Override
            public Double mapRow(ResultSet resultSet, int i) throws SQLException {

                return resultSet.getDouble(column);
            }
        });

        return allValues;
    }

    public long getCountOfAllRows(String table) {

        String sql = "SELECT COUNT(*) FROM " + table;

        return jdbcTemplate.queryForObject(sql, Long.class);
    }


    public Double getMin(String table, String column) {

        String sql = "SELECT MIN(" + column + ") FROM " + table;

        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public Double getMax(String table, String column) {

        String sql = "SELECT MAX(" + column + ") FROM " + table;

        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public List<String> getAllTables() {

        String sql = "SELECT TABLENAME FROM SYS.SYSTABLES WHERE TABLETYPE = 'T'";

        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> getAllColumns(String table) {

        String sql = "SELECT COLUMNNAME, C.COLUMNDATATYPE FROM SYS.SYSTABLES T, SYS.SYSCOLUMNS C WHERE TABLEID = " +
                "REFERENCEID AND TABLENAME = '" + table + "'";

        return jdbcTemplate.query(sql, new ResultSetExtractor<List<String>>() {

            @Override
            public List<String> extractData(ResultSet rs) throws SQLException,
                    DataAccessException {

                List<String> numericColumns = new ArrayList<>();

                while (rs.next()) {

                    String columnDataType = rs.getString("COLUMNDATATYPE");

                    if (columnDataType.toUpperCase().contains("NUMERIC")) {
                        numericColumns.add(rs.getString("COLUMNNAME"));
                    }
                }

                return numericColumns;
            }
        });

    }

    public void fetchAllValuesParallelly(StreamCallbackHandler streamCallbackHandler) {

        String sql = "SELECT " + streamCallbackHandler.getColumn() + " FROM " + streamCallbackHandler.getTable();

        jdbcTemplate.query(sql, streamCallbackHandler);
    }

    public int updateValues(String table, String column, String currentValue, String newValue) {

        String sql =
                "UPDATE " + table + " SET " + column + " = ?  WHERE " + column + " = ? ";

        return jdbcTemplate.update(sql, newValue, currentValue);
    }

}
