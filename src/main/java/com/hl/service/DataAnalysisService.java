package com.hl.service;

import com.hl.dto.DataQualityReport;
import com.hl.dto.DataUpdateReport;

import java.util.List;

public interface DataAnalysisService {

    public List<String> getAllTables();

    public List<String> getAllColumns(String table);

    public DataQualityReport createDataQualityReport(String table, String column, int classes) throws InterruptedException;

    public DataUpdateReport updateValues(String table, String column, String currentValue, String newValue);
}
