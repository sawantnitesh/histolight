package com.hl.rest;

import com.hl.dto.DataQualityRequest;
import com.hl.dto.DataQualityReport;
import com.hl.dto.DataUpdateReport;
import com.hl.dto.DataUpdateRequest;
import com.hl.service.DataAnalysisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
public class HLController {

    @Autowired
    DataAnalysisServiceImpl daService;

    @RequestMapping(value = "/createDataQualityReport", method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DataQualityReport> createDataQualityReport(@RequestBody DataQualityRequest dataRequest) {

        DataQualityReport dataQualityReport = null;

        try {
            dataQualityReport = daService.createDataQualityReport(dataRequest.getTable(),
                    dataRequest.getColumn(), dataRequest.getClasses());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<DataQualityReport>(dataQualityReport, HttpStatus.OK);
    }


    @RequestMapping(value = "/getAllTables", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAllTables() {

        List<String> allTables = daService.getAllTables();

        return new ResponseEntity<List<String>>(allTables, HttpStatus.OK);
    }


    @RequestMapping(value = "/getAllColumns", method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getAllColumns(@RequestBody DataQualityRequest dataRequest) {

        List<String> allColumns = daService.getAllColumns(dataRequest.getTable());

        return new ResponseEntity<List<String>>(allColumns, HttpStatus.OK);
    }


    @RequestMapping(value = "/updateValues", method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DataUpdateReport> createDataQualityReport(@RequestBody DataUpdateRequest dataUpdateRequest) {

        DataUpdateReport dataUpdateReport = null;

        try {
            dataUpdateReport = daService.updateValues(dataUpdateRequest.getTable(),
                    dataUpdateRequest.getColumn(), dataUpdateRequest.getCurrentValue(), dataUpdateRequest.getNewValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<DataUpdateReport>(dataUpdateReport, HttpStatus.OK);
    }

}