package com.xcrm.controller.web;

import com.xcrm.service.OrganizationService;
import com.xcrm.service.report.ReportService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;
    private final OrganizationService organizationService;

    public ReportController(ReportService reportService, OrganizationService organizationService) {
        this.reportService = reportService;
        this.organizationService = organizationService;
    }

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
            byte[] reportBytes = reportService.generateReport(reportType, format);

            String contentType;
            String fileExtension;

            switch (format.toLowerCase()) {
                case "pdf":
                    contentType = MediaType.APPLICATION_PDF_VALUE;
                    fileExtension = ".pdf";
                    break;
                case "html":
                    contentType = MediaType.TEXT_HTML_VALUE;
                    fileExtension = ".html";
                    break;
                case "xlsx":
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    fileExtension = ".xlsx";
                    break;
                default:
                    throw new IllegalArgumentException("Formato no soportado: " + format);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportType + fileExtension)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(reportBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
