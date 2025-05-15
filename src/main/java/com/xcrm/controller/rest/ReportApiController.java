package com.xcrm.controller.rest;

import com.xcrm.service.report.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ReportApiController {

    private final ReportService reportService;

    @GetMapping("/ventas-por-comercial")
    public List<Map<String, Object>> getVentasPorComercial() {
      return reportService.getVentasPorComercial();
    }

    @GetMapping("/interacciones-por-venta")
    public List<Map<String, Object>> getInteraccionesPorVenta() {
        return reportService.getInteraccionesPorVenta();
    }

}
