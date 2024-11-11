package com.xcrm.controller.rest;

import com.xcrm.model.Organizacion;
import com.xcrm.service.OrganizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
public class ApiAdminController {

    @Autowired
    private OrganizacionService organizacionService;

    @GetMapping("/all")
    public ResponseEntity<List<Organizacion>> getAllOnganisations(){
       List<Organizacion> organizaciones = organizacionService.obtenerTodasLasOrganizaciones();

       if (organizaciones.isEmpty()){
           return ResponseEntity.noContent().build();
       }

        return ResponseEntity.ok(organizaciones);
    }

}
