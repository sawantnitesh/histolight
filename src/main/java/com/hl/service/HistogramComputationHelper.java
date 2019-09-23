package com.hl.service;

import com.hl.dto.DataQualityReport;

import java.util.List;

public class HistogramComputationHelper {

    public static DataQualityReport computeForValules(String table, String column, List<Double> values, int classes,
                                                      double min, double max) {

        int numberOfHistogramClasses = classes;
        int widthOfHistogramClass = (int) Math.ceil((double) (max - min) / numberOfHistogramClasses);

        DataQualityReport dataQualityReport = new DataQualityReport(table, column);

        for (int i = 0; i < numberOfHistogramClasses; i++) {

            double start = min + i * widthOfHistogramClass;
            double end = start + widthOfHistogramClass;

            DataQualityReport.IntervalInfo intervalInfo = new DataQualityReport.IntervalInfo(start, end, 0);
            dataQualityReport.addIntervalInfo(intervalInfo);

            double endToCompare = end;
            if (i == numberOfHistogramClasses - 1) {
                endToCompare = end + 1;
            }

            for (double v : values) {

                if (v >= start && v < endToCompare) {

                    intervalInfo.setFrequency(intervalInfo.getFrequency() + 1);
                }
            }
        }

        return dataQualityReport;
    }
}
