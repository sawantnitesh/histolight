package com.hl.service;

import com.hl.dto.DataQualityReport;

public interface HistogramComputationService {

    public DataQualityReport compute(String table, String column, double min, double max, int classes);

    public DataQualityReport computeOnStream(String table, String column, double min, double max, int classes,
                                             long countOfRows) throws InterruptedException;

}
