package com.xcrm.controller.web;

import com.xcrm.model.Organizacion;
import com.xcrm.model.User;
import com.xcrm.service.OrganizacionService;
import com.xcrm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class IndexController {

    @Autowired
    private OrganizacionService organizacionService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String mostrarIndex(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken));
        model.addAttribute("loggedIn", isLoggedIn);

        System.out.println("Estado de login: " + (isLoggedIn ? "Autenticado" : "No autenticado"));

        if (isLoggedIn) {
            // Imprime los roles del usuario
            System.out.println("Estado de login: Autenticado");
            System.out.println("Roles del usuario: " + authentication.getAuthorities());

            // Verifica si el usuario tiene el rol ADMIN
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            model.addAttribute("isAdmin", isAdmin);

            if (isAdmin) {
                System.out.println("El usuario tiene rol ADMIN.");
            } else {
                System.out.println("El usuario no tiene rol ADMIN.");
            }
        } else {
            System.out.println("Estado de login: No autenticado");
        }


        return "index";
    }


    @GetMapping("/caracteristicas")
    public String mostrarCaracteristicas(Model model) {
       // model.addAttribute("titulo", "Características de XCRM");
        return "caracteristicas"; // Necesitarás crear un archivo caracteristicas.html en templates
    }

    @GetMapping("/precios")
    public String mostrarPrecios(Model model) {
       // model.addAttribute("titulo", "Precios de XCRM");
        return "precios"; // Necesitarás crear un archivo precios.html en templates
    }

    @GetMapping("/contacto")
    public String mostrarContacto(Model model) {
        model.addAttribute("titulo", "Contacto de XCRM");
        return "contacto"; // Necesitarás crear un archivo contacto.html en templates
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("titulo", "Inicio de Sesión");
        return "login"; // Necesitarás crear un archivo login.html en templates
    }



    @GetMapping("/mi-cuenta")
    public String mostrarMiCuenta(Model model) {
        //model.addAttribute("titulo", "Mi Cuenta");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        List<Organizacion> organizaciones = organizacionService.buscarPorNombre(username);

        if (!organizaciones.isEmpty()) {
            model.addAttribute("organizacion", organizaciones.get(0)); // Pasamos la organización activa
        } else {
            System.out.println("la lista de la organizacion esta vacia");
            model.addAttribute("error", "No se encontró la organización.");
        }
        model.addAttribute("nuevoUsuario", new User());
        return "mi-cuenta"; // Necesitarás crear un archivo mi-cuenta.html en templates
    }

    @GetMapping("/registro")
    public String muestraRegistro(Model model){
        return "registro";
    }



    @PostMapping("/registro")
    public String registrarOrganizacion(@RequestParam String nombre,
                                        @RequestParam String email,
                                        @RequestParam String confirmEmail,
                                        @RequestParam String password,
                                        @RequestParam String confirmPassword,
                                        @RequestParam String plan,
                                        Model model) {

        // Verificar si el email ya está registrado
        /*if (organizacionService.buscarPorEmail(email).isPresent()) {
            model.addAttribute("error", "El email ya está en uso.");
            return "registro"; // Regresar al formulario de registro
        }*/

        // Validar que el email y el email de confirmación coincidan
       /* if (!email.equals(confirmEmail)) {
            model.addAttribute("error", "Los emails no coinciden.");
            return "registro";
        }*/

        // Validar que la contraseña y la confirmación coincidan
       /* if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }*/

        // guardar una nueva organización
       Organizacion nuevaOrganizacion = organizacionService.crearOrganizacion(nombre, email, plan);


        //guardo al usuario administrador
        userService.crearUsuarioConOrganizacion(nombre, password, nuevaOrganizacion, "ROLE_ADMIN");

       // model.addAttribute("success", "Registro exitoso. Ahora puedes iniciar sesión.");
        return "redirect:/login"; // Redirigir al login después de registrarse
    }


}
