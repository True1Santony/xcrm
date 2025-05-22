package com.xcrm.controller.web;

import com.xcrm.service.report.ReportServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/cache")
@AllArgsConstructor
public class CacheController {

    private final ReportServiceImpl reportServiceImpl;

    @PostMapping("/clear")
    public String clearCache(RedirectAttributes redirectAttrs) {
        reportServiceImpl.clearAllCaches();
        redirectAttrs.addFlashAttribute("message", "Caché limpiado con éxito.");
        return "redirect:/mi-cuenta";
    }
}
