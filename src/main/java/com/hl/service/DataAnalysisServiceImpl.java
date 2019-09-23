package com.hl.service;

import com.hl.dao.DataAccess;
import com.hl.dto.DataQualityReport;
import com.hl.dto.DataUpdateReport;
import com.hl.validation.AppError;
import com.hl.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {

    @Autowired
    private Environment env;

    @Autowired
    DataAccess dataAccess;

    @Autowired
    HistogramComputationService histogramComputationService;

    @Override
    public List<String> getAllTables() {

        return dataAccess.getAllTables();
    }

    @Override
    public List<String> getAllColumns(String table) {

        return dataAccess.getAllColumns(table);
    }

    @Override
    public DataQualityReport createDataQualityReport(String table, String column, int classes) throws InterruptedException {

        return analyze(table, column, classes);
    }

    @Override
    public DataUpdateReport updateValues(String table, String column, String currentValue, String newValue) {

        try {
            int rowsUpdatedCount = dataAccess.updateValues(table, column, currentValue,
                    newValue);

            return new DataUpdateReport(table, column, rowsUpdatedCount);

        } catch (Exception ex) {

            ex.printStackTrace();
            return new DataUpdateReport(table, column, new AppError("UPDATE_ERROR", "Error updating specified values"));
        }
    }

    private DataQualityReport analyze(String table, String column, int classes) throws InterruptedException {

        double min = dataAccess.getMin(table, column);

        double max = dataAccess.getMax(table, column);

        min = Math.floor(min);
        max = Math.ceil(max);

        AppError appError = Validator.validate(min, max, classes);

        if (appError != null) {

            return new DataQualityReport(table, column, appError);
        }

        long countOfRows = dataAccess.getCountOfAllRows(table);

        long smallDatasetThreshold = Long.parseLong(env.getProperty("small_dataset_threshold"));

        if (countOfRows > smallDatasetThreshold) {

            return histogramComputationService.computeOnStream(table, column, min, max, classes, countOfRows);

        } else {

            return histogramComputationService.compute(table, column, min, max, classes);
        }
    }
}
