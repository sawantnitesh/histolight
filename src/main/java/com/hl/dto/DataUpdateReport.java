package com.hl.dto;

import com.hl.validation.AppError;

public class DataUpdateReport {

    private String table;

    private String column;

    private int rowsUpdatedCount;

    private AppError appError;

    public DataUpdateReport(String table, String column, int rowsUpdatedCount) {

        this.table = table;
        this.column = column;
        this.rowsUpdatedCount = rowsUpdatedCount;
    }

    public DataUpdateReport(String table, String column, AppError appError) {

        this.table = table;
        this.column = column;
        this.appError = appError;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getRowsUpdatedCount() {
        return rowsUpdatedCount;
    }

    public void setRowsUpdatedCount(int rowsUpdatedCount) {
        this.rowsUpdatedCount = rowsUpdatedCount;
    }

    public AppError getAppError() {
        return appError;
    }

    public void setAppError(AppError appError) {
        this.appError = appError;
    }
}
