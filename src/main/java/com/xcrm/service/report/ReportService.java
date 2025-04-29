package com.xcrm.service.report;

import com.xcrm.utils.CustomRoutingDataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    private CustomRoutingDataSource dataSource;

    public ReportService(CustomRoutingDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] generateReport(String reportName, String format) throws JRException {
        InputStream reportStream = this.getClass().getResourceAsStream("/reports/" + reportName + ".jasper");

        if (reportStream == null) {
            throw new RuntimeException("No se encontró el reporte: " + reportName);
        }

        Map<String, Object> parameters = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            switch (format.toLowerCase()) {
                case "pdf":
                    // Exportar como PDF
                    JasperExportManager.exportReportToPdfStream(jasperPrint, out);
                    break;
                case "html":
                    // Exportar como HTML
                    HtmlExporter htmlExporter = new HtmlExporter();
                    htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    htmlExporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
                    htmlExporter.exportReport();
                    break;
                case "xlsx":
                    // Exportar como Excel
                    JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                    xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                    SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                    configuration.setOnePagePerSheet(false);
                    xlsxExporter.setConfiguration(configuration);
                    xlsxExporter.exportReport();
                    break;
                default:
                    throw new IllegalArgumentException("Formato no soportado: " + format);
            }

            return out.toByteArray();
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }

}
