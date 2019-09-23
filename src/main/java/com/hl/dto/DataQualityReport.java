package com.hl.dto;

import com.hl.validation.AppError;

import java.util.ArrayList;
import java.util.List;

public class DataQualityReport {

    private String table;

    private String column;

    private List<IntervalInfo> intervalInfoList;

    private AppError appError;

    public DataQualityReport(String table, String column) {

        this.table = table;
        this.column = column;
        this.intervalInfoList = new ArrayList<>();
    }

    public DataQualityReport(String table, String column, AppError appError) {

        this.table = table;
        this.column = column;
        this.appError = appError;
    }

    public String getTable() {
        return table;
    }

    public String getColumn() {
        return column;
    }

    public List<IntervalInfo> getIntervalInfoList() {
        return intervalInfoList;
    }

    public void setIntervalInfoList(List<IntervalInfo> intervalInfoList) {
        this.intervalInfoList = intervalInfoList;
    }

    public void addIntervalInfo(IntervalInfo intervalInfo) {

        intervalInfoList.add(intervalInfo);
    }

    public AppError getAppError() {
        return appError;
    }

    public static class IntervalInfo {

        private double start;

        private double end;

        private long frequency;

        public IntervalInfo(double start, double end, long frequency) {

            this.start = start;
            this.end = end;
            this.frequency = frequency;
        }

        public double getStart() {
            return start;
        }

        public void setStart(double start) {
            this.start = start;
        }

        public double getEnd() {
            return end;
        }

        public void setEnd(double end) {
            this.end = end;
        }

        public long getFrequency() {
            return frequency;
        }

        public void setFrequency(long frequency) {
            this.frequency = frequency;
        }
    }
}
