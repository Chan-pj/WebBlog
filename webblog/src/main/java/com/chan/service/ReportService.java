package com.chan.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public interface ReportService {

    Map<String, String> buildReportData();

    byte[] generateWordReport() throws Exception;

    byte[] generatePdfReport() throws Exception;
}