package com.hl.service;

import com.hl.dao.DataAccess;
import com.hl.dto.DataQualityReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class HistogramComputationServiceImpl implements HistogramComputationService {

    @Autowired
    private Environment env;

    @Autowired
    DataAccess dataAccess;

    @Override
    public DataQualityReport compute(String table, String column, double min, double max, int classes) {

        return HistogramComputationHelper.computeForValules(table, column, dataAccess.getAllValues(table, column),
                classes, min, max);
    }

    @Override
    public DataQualityReport computeOnStream(String table, String column, double min, double max, int classes,
                                             long countOfRows) throws InterruptedException {

        long smallDatasetThreshold = Long.parseLong(env.getProperty("small_dataset_threshold"));

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StreamCallbackHandler streamCallbackHandler = new StreamCallbackHandler(table, column,
                smallDatasetThreshold, classes,
                min, max, countOfRows, countDownLatch);

        dataAccess.fetchAllValuesParallelly(streamCallbackHandler);

        countDownLatch.await();

        return streamCallbackHandler.getDataQualityReport();
    }
}
