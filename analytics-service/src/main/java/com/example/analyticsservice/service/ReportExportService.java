package com.example.analyticsservice.service;

import com.example.analyticsservice.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Stub service for report export. Returns placeholder file paths.
 * Actual file generation (Apache POI, etc.) will be added later.
 */
@Service
@Slf4j
public class ReportExportService {

    private static final String REPORTS_DIR = "/reports/generated/";

    public String exportReport(Report report) {
        String extension = report.getFormat().name().toLowerCase();
        String fileName = report.getReportType().toLowerCase().replaceAll("\\s+", "_")
                + "_" + report.getId().toString().substring(0, 8)
                + "." + extension;

        String filePath = REPORTS_DIR + fileName;
        log.info("Stub: generated report file at {}", filePath);
        return filePath;
    }
}
