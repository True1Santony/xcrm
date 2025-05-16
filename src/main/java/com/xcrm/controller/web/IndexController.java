package com.xcrm.controller.web;

import com.xcrm.model.Organization;
import com.xcrm.model.User;
import com.xcrm.service.OrganizationService;
import com.xcrm.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Log4j2
@AllArgsConstructor
@Controller
public class IndexController {

    private OrganizationService organizationService;
    private UserService userService;

    @GetMapping("/")
        public String mostrarIndex(Model model){
            log.info("Página de inicio mostrada");
        return "index";
    }

    @GetMapping("/caracteristicas")
    public String mostrarCaracteristicas(Model model) {
        model.addAttribute("titulo", "Características de XCRM");
        return "caracteristicas";
    }

    @GetMapping("/precios")
    public String mostrarPrecios(Model model) {
        model.addAttribute("titulo", "Precios de XCRM");
        return "precios";
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model, @RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        model.addAttribute("titulo", "Inicio de Sesión");
        return "login";
    }

    @GetMapping("/mi-cuenta")
    public String mostrarMiCuenta(Model model) {
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        return "mi-cuenta";
    }


    @GetMapping("/aviso-legal")
    public String mostrarAvisoLegal(Model model) {
        model.addAttribute("titulo", "Aviso Legal de XCRM");
        return "aviso-legal";
    }


    @GetMapping("/registro")
    public String muestraRegistro(Model model){
        model.addAttribute("titulo", "REGISTRO");
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarOrganizacion(@RequestParam String nombreEmpresa,
                                        @RequestParam String nombreAdmin,
                                        @RequestParam String email,
                                        @RequestParam String confirmEmail,
                                        @RequestParam String password,
                                        @RequestParam String confirmPassword,
                                        @RequestParam String plan,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {

        model.addAttribute("titulo", "REGISTRO");
        List<String> errores = new ArrayList<>();

        // Verificar si el email ya está registrado
        if (organizationService.buscarPorEmail(email).isPresent()) {
            errores.add("El email ya está en uso.");
        }

        if (userService.findByUsername(nombreAdmin) != null) {
            errores.add("El usuario ya existe");
        }

        if(organizationService.findByNombre(nombreEmpresa).isPresent()){
            errores.add("La organización, con este nombre, ya existe.");
        }

        // Validar que el email y el email de confirmación coincidan
       if (!email.equals(confirmEmail)) {
           errores.add("Los emails no coinciden.");
        }

        // Validar que la contraseña y la confirmación coincidan
       if (!password.equals(confirmPassword)) {
           errores.add("Las contraseñas no coinciden.");
        }
        // Validación de la contraseña con expresión regular
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$";
        if (!Pattern.matches(regex, password)) {
            errores.add("La contraseña debe tener al menos una letra mayúscula, una minúscula, un número y al menos 6 caracteres.");
        }

       // Si hay errores, devolver al formulario con los errores
        if (!errores.isEmpty()) {
            model.addAttribute("errores", errores);
            return "registro"; // Regresamos al formulario con los mensajes de error
        }

        try {
            // Guardar una nueva organización
            Organization nuevaOrganization = organizationService.crearOrganizacion(nombreEmpresa, email, plan);

            // Guardar al usuario administrador
            userService.crearUsuarioConOrganizacion(nombreAdmin, password, nuevaOrganization, "ROLE_ADMIN");

        } catch (IllegalArgumentException e) {
            errores.add( e.getMessage());  // Capturamos la excepción y la pasamos al modelo
            model.addAttribute("errores",errores);
            return "registro"; // Regresamos al formulario con el mensaje de error
        }
        redirectAttributes.addFlashAttribute("success", "Registro exitoso. Ahora puedes iniciar sesión.");
        return "redirect:/login"; // Redirigir al login después de registrarse
    }
}