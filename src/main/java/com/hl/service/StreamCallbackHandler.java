package com.hl.service;

import com.hl.dto.DataQualityReport;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class StreamCallbackHandler implements RowCallbackHandler {

    private String table;
    private String column;
    long smallDatasetThreshold;
    int classes;
    private double min;
    private double max;
    private long countOfRows;
    private long currentRowIndex;
    private DataQualityReport dataQualityReport;
    private List<Double> values;
    private CountDownLatch countDownLatch;

    public StreamCallbackHandler(String table, String column, long smallDatasetThreshold, int classes, double min,
                                 double max,
                                 long countOfRows,
                                 CountDownLatch countDownLatch) {

        this.table = table;
        this.column = column;
        this.smallDatasetThreshold = smallDatasetThreshold;
        this.classes = classes;
        this.min = min;
        this.max = max;
        this.countOfRows = countOfRows;
        this.countDownLatch = countDownLatch;
        this.currentRowIndex = 0;
        this.dataQualityReport = null;
        this.values = new ArrayList<>();
    }

    public DataQualityReport getDataQualityReport() {

        return dataQualityReport;
    }

    public String getColumn() {

        return this.column;
    }

    public String getTable() {

        return this.table;
    }

    @Override
    public void processRow(ResultSet resultSet) throws SQLException {

        while (resultSet.next()) {

            currentRowIndex++;

            Double value = resultSet.getDouble(column);
            values.add(value);

            if (currentRowIndex % smallDatasetThreshold == 0) {

                processValues(values, classes, min, max);
            }
        }

        if (values.size() > 0) {

            processValues(values, classes, min, max);
        }

        countDownLatch.countDown();
    }

    private void processValues(List<Double> values, int classes, Double min, Double max) {

        DataQualityReport dataQualityReport = HistogramComputationHelper.computeForValules(table, column, values,
                classes, min, max);

        if (this.dataQualityReport == null) {
            this.dataQualityReport = dataQualityReport;
        } else {
            mergeDataQualityReports(this.dataQualityReport, dataQualityReport);
        }

        //reset
        this.values = new ArrayList<>();
    }

    private void mergeDataQualityReports(DataQualityReport dataQualityReport1,
                                         DataQualityReport dataQualityReport2) {


        List<DataQualityReport.IntervalInfo> targetIntervalInfoList = dataQualityReport1.getIntervalInfoList();
        List<DataQualityReport.IntervalInfo> sourceInfoListTobeMerged =
                dataQualityReport2.getIntervalInfoList();

        for (int j = 0; j < targetIntervalInfoList.size(); j++) {

            DataQualityReport.IntervalInfo target = targetIntervalInfoList.get(j);
            DataQualityReport.IntervalInfo source = sourceInfoListTobeMerged.get(j);
            target.setFrequency(target.getFrequency() + source.getFrequency());
        }
    }
}
