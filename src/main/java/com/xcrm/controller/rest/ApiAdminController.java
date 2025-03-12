package com.xcrm.controller.rest;

import com.xcrm.DTO.UserCreateInOrganizationDTO;
import com.xcrm.model.Organizacion;
import com.xcrm.service.OrganizacionService;
import com.xcrm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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
    public String createUser(@ModelAttribute("nuevoUsuario") UserCreateInOrganizationDTO userDTO, Model model) {
        try {
            Organizacion organizacion = organizacionService.buscarPorId(userDTO.getOrganizationId());
            userService.createUserInOrganization(userDTO.getUsername(),userDTO.getPassword(),organizacion);
            model.addAttribute("success", "Usuario creado exitosamente.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el usuario: " + e.getMessage());
        }
        return "redirect:/mi-cuenta";
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la deserialización del JSON: " + e.getMessage());
    }


}
