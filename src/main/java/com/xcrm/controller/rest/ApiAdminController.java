package com.xcrm.controller.rest;

import com.xcrm.model.Organizacion;
import com.xcrm.model.User;
import com.xcrm.service.OrganizacionService;
import com.xcrm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class ApiAdminController {

    @Autowired
    private OrganizacionService organizacionService;

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<Organizacion>> getAllOnganisations(){
       List<Organizacion> organizaciones = organizacionService.obtenerTodasLasOrganizaciones();

       if (organizaciones.isEmpty()){
           return ResponseEntity.noContent().build();
       }

        return ResponseEntity.ok(organizaciones);
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody User nuevoUsuario, @RequestParam Long organizacionId) {
        Organizacion organizacion = organizacionService.buscarPorId(organizacionId);

        if (organizacion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La organización no existe.");
        }

        try {
            userService.crearNuevoUsuario(nuevoUsuario.getUsername(), nuevoUsuario.getPassword(), organizacion);
            return ResponseEntity.ok("Usuario creado exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
