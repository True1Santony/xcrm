package com.xcrm.controller.web;

import com.xcrm.DTO.ReportResponseDto;
import com.xcrm.service.OrganizationService;
import com.xcrm.service.report.ReportService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@AllArgsConstructor
@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;
    private final OrganizationService organizationService;

    @GetMapping()
    public String showReportsDashboard(Model model) {
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        return "reports-administration-dashboard";
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam("tipoReporte") String reportType,
            @RequestParam("formato") String format) {
        try {
            ReportResponseDto response = reportService.generateReport(reportType, format);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + response.getFileName())
                    .contentType(MediaType.parseMediaType(response.getContentType()))
                    .body(response.getContent());

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/admin/ventas")
    public ResponseEntity<byte[]> ventasPorComercial() throws JRException {
        ReportResponseDto response = reportService.generateReport("ComercialesVentas", "pdf");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ventas_comerciales.pdf")
                .body(response.getContent());
    }

    @GetMapping("/admin/interacionesPorVenta")
    public ResponseEntity<byte[]> interacionesPorVenta() throws JRException {
        ReportResponseDto response = reportService.generateReport("interporVenta", "pdf");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ventas_comerciales.pdf")
                .body(response.getContent());
    }

}
