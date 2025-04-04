package com.xcrm.controller.web;

import com.xcrm.model.Organizacion;
import com.xcrm.model.User;
import com.xcrm.service.OrganizacionService;
import com.xcrm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private OrganizacionService organizacionService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String mostrarIndex(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = (
                        authentication != null                                        // 1. Verifica si el objeto de autenticación no es nulo
                        && authentication.isAuthenticated()                           // 2. Verifica si el usuario está autenticado
                        && !(authentication instanceof AnonymousAuthenticationToken)  // 3. Verifica que no sea un usuario anónimo(por defecto lo es si no se ha logeado)
        );
        model.addAttribute("loggedIn", isLoggedIn);
        logger.info("Estado de login: " + (isLoggedIn ? "Autenticado" : "No autenticado"));
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

    @GetMapping("/contacto")
    public String mostrarContacto(Model model) {
        model.addAttribute("titulo", "Contacto de XCRM");
        return "contacto";
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "login";
    }

    @GetMapping("/mi-cuenta")
    public String mostrarMiCuenta(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        //obtengo de la Base de datos la Organizacion a partir del username
        Optional <Organizacion> organizacionOptional = organizacionService
                .findById(userService
                        .obtenerUsuarioPorNombre(username)
                        .getOrganizacion()
                        .getId());

        model.addAttribute("organizacion", organizacionOptional.get()); // Paso la organización activa
        model.addAttribute("nuevoUsuario", new User());

        return "mi-cuenta";
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
                                        Model model) {

        model.addAttribute("titulo", "REGISTRO");
        List<String> errores = new ArrayList<>();

        // Verificar si el email ya está registrado
        if (organizacionService.buscarPorEmail(email).isPresent()) {
            errores.add("El email ya está en uso.");
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
            Organizacion nuevaOrganizacion = organizacionService.crearOrganizacion(nombreEmpresa, email, plan);

            // Guardar al usuario administrador
            userService.crearUsuarioConOrganizacion(nombreAdmin, password, nuevaOrganizacion, "ROLE_ADMIN");

        } catch (IllegalArgumentException e) {
            errores.add( e.getMessage());  // Capturamos la excepción y la pasamos al modelo
            model.addAttribute("errores",errores);
            return "registro"; // Regresamos al formulario con el mensaje de error
        }
        model.addAttribute("success", "Registro exitoso. Ahora puedes iniciar sesión.");
        return "redirect:/login"; // Redirigir al login después de registrarse
    }


}
