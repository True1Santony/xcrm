package com.xcrm.service.report;

import com.xcrm.dto.ReportResponseDto;
import com.xcrm.service.OrganizationService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Log4j2
@Service
public class ReportServiceImpl implements ReportService {

    private final EntityManager entityManager;
    private final OrganizationService organizationService;

    @Override
    @Cacheable(value = "generatedReports", key = "'generateReport_' + #reportName + '_' + #format + '_' + @organizationServiceImpl.getCurrentOrganization().id")
    public ReportResponseDto generateReport(String reportName, String format) throws JRException {
        InputStream reportStream = getReportStream(reportName);
        Map<String, Object> parameters = prepareReportParameters();

        try (Connection connection = getConnection()) {
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

    public InputStream getReportStream(String reportName) {
        InputStream stream = this.getClass().getResourceAsStream("/reports/" + reportName + ".jasper");
        if (stream == null) {
            throw new RuntimeException("No se encontró el reporte: " + reportName);
        }
        return stream;
    }

    public InputStream getLogoStream() {
        InputStream logoStream = this.getClass().getResourceAsStream("/static/images/logo5.jpeg");
        if (logoStream == null) {
            throw new RuntimeException("No se encontró el logo");
        }
        return logoStream;
    }
    protected Connection getConnection() {
        Session session = entityManager.unwrap(Session.class);
        return session.doReturningWork(conn -> conn);
    }

    protected Map<String, Object> prepareReportParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logo", getLogoStream());
        return parameters;
    }

    public ByteArrayOutputStream exportReport(JasperPrint jasperPrint, String format) throws JRException {
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

    @Override
    @Cacheable(value = "ventasPorComercial", key = "'ventasPorComercial_' + @organizationServiceImpl.getCurrentOrganization().id")
    public List<Map<String, Object>> getVentasPorComercial() {
        String sql = """
        SELECT u.username AS comercial, COUNT(v.id) AS total_ventas
        FROM users u
        LEFT JOIN interacciones i ON i.comercial_id = u.id
        LEFT JOIN ventas v ON v.interaccion_id = i.id
        GROUP BY u.username
        ORDER BY total_ventas DESC
    """;

        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();

        List<Map<String, Object>> mapped = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> m = new HashMap<>();
            m.put("comercial", row[0]);
            m.put("total_ventas", ((Number) row[1]).intValue());
            mapped.add(m);
        }

        return mapped;
    }

    @Override
    @Cacheable(value = "interaccionesPorVenta", key = "'interaccionesPorVenta_' + @organizationServiceImpl.getCurrentOrganization().id")
    public List<Map<String, Object>> getInteraccionesPorVenta() {
        String sql = """
        SELECT 
            u.username AS comercial_username,
            CONCAT('Venta #', v.id) AS venta_label,
            COUNT(i_cont.id) AS total_interacciones
        FROM ventas v
        JOIN interacciones i ON v.interaccion_id = i.id
        JOIN clientes c ON i.cliente_id = c.id
        JOIN users u ON i.comercial_id = u.id
        JOIN interacciones i_cont ON i_cont.cliente_id = c.id AND i_cont.comercial_id = u.id
        GROUP BY v.id, u.id
        ORDER BY u.username
    """;

        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();

        List<Map<String, Object>> mapped = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> m = new HashMap<>();
            m.put("comercial", row[0]);
            m.put("venta", row[1]);
            m.put("interacciones", ((Number) row[2]).intValue());
            mapped.add(m);
        }

        return mapped;
    }

    @Override
    @Cacheable(value = "ventasPorCompania", key = "'ventasPorCompania_' + @organizationServiceImpl.getCurrentOrganization().id")
    public List<Map<String, Object>> getVentasPorCompania() {
        String sql = """
        SELECT\s
                        c.nombre AS nombre_campania,
                        COUNT(v.id) AS total_ventas
                    FROM\s
                        campanias c
                    LEFT JOIN\s
                        interacciones i ON c.id = i.campania_id
                    LEFT JOIN\s
                        ventas v ON v.interaccion_id = i.id
                    GROUP BY\s
                        c.nombre
                    ORDER BY\s
                        total_ventas DESC;
    """;

        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();

        List<Map<String, Object>> mapped = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> m = new HashMap<>();
            m.put("campania", row[0]);
            m.put("ventas", ((Number) row[1]).intValue());
            mapped.add(m);
        }

        return mapped;
    }

    @Override
    @CacheEvict(value = {"ventasPorComercial", "interaccionesPorVenta", "ventasPorCompania", "generatedReports"}, allEntries = true)
    public String clearAllCaches() {
        log.info("Todos los cachés han sido limpiados.");
        return "Todos los cachés han sido limpiados";
    }
}

