package com.xcrm.service.report;

import com.xcrm.DTO.ReportResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
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
import org.hibernate.Session;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    public ReportResponseDto generateReport(String reportName, String format) throws JRException {
        InputStream reportStream = getReportStream(reportName);
        Map<String, Object> parameters = prepareReportParameters();

        try (Session session = entityManager.unwrap(Session.class)) {
            Connection connection = session.doReturningWork(conn -> conn);
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);
            ByteArrayOutputStream outputStream = exportReport(jasperPrint, format);

            String contentType = getContentType(format);
            String fileName = reportName + getFileExtension(format);

            log.info("El reporte : " + reportName + " se ha generado correctamente.");
            return new ReportResponseDto(outputStream.toByteArray(), contentType, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }

    private InputStream getReportStream(String reportName) {
        InputStream stream = this.getClass().getResourceAsStream("/reports/" + reportName + ".jasper");
        if (stream == null) {
            throw new RuntimeException("No se encontró el reporte: " + reportName);
        }
        return stream;
    }

    private Map<String, Object> prepareReportParameters() {
        Map<String, Object> parameters = new HashMap<>();
        InputStream logoStream = this.getClass().getResourceAsStream("/static/images/logo5.jpeg");

        if (logoStream == null) {
            throw new RuntimeException("No se encontró el logo");
        }

        parameters.put("logo", logoStream);
        return parameters;
    }

    private ByteArrayOutputStream exportReport(JasperPrint jasperPrint, String format) throws JRException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        switch (format.toLowerCase()) {
            case "pdf":
                JasperExportManager.exportReportToPdfStream(jasperPrint, out);
                break;
            case "html":
                HtmlExporter htmlExporter = new HtmlExporter();
                htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                htmlExporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
                htmlExporter.exportReport();
                break;
            case "xlsx":
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

        return out;
    }

    private String getContentType(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF_VALUE;
            case "html" -> MediaType.TEXT_HTML_VALUE;
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> throw new IllegalArgumentException("Formato no soportado: " + format);
        };
    }

    private String getFileExtension(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> ".pdf";
            case "html" -> ".html";
            case "xlsx" -> ".xlsx";
            default -> throw new IllegalArgumentException("Formato no soportado: " + format);
        };
    }
}

