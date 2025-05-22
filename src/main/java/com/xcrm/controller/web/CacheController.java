package com.xcrm.controller.web;

import com.xcrm.service.report.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/cache")
@AllArgsConstructor
public class CacheController {

    private final ReportService reportService;

    @PostMapping("/clear")
    public String clearCache(RedirectAttributes redirectAttrs) {
        reportService.clearAllCaches();
        redirectAttrs.addFlashAttribute("message", "Caché limpiado con éxito.");
        return "redirect:/mi-cuenta";
    }
}
