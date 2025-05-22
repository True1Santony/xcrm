package com.xcrm.service.report;

import com.xcrm.dto.ReportResponseDto;
import net.sf.jasperreports.engine.JRException;

import java.util.List;
import java.util.Map;

public interface ReportService {
    ReportResponseDto generateReport(String reportName, String format) throws JRException;
    List<Map<String, Object>> getVentasPorComercial();
    List<Map<String, Object>> getInteraccionesPorVenta();
    List<Map<String, Object>> getVentasPorCompania();
    String clearAllCaches();
}
