package com.xcrm.controller.web;

import com.xcrm.model.Organization;
import com.xcrm.service.OrganizationService;
import com.xcrm.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

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

    private final OrganizationService organizationService;
    private final UserService userService;

    /**
     * Controlador para la página principal.
     * Solo muestra la vista "index" y registra la acción en logs.
     */
    @GetMapping("/")
        public String mostrarIndex(Model model){
            log.info("Página de inicio mostrada");
        return "index";
    }

    /**
     * Controlador para mostrar la página de características.
     * Añade un título a la vista para mostrar información estática.
     */
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

    /**
     * Controlador para la página de login.
     * Muestra mensajes de error si las credenciales son incorrectas.
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model, @RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        model.addAttribute("titulo", "Inicio de Sesión");
        return "login";
    }

    /**
     * Controlador para mostrar la página de "Mi Cuenta".
     * Obtiene y muestra la organización actual asociada al usuario.
     */
    @GetMapping("/mi-cuenta")
    public String mostrarMiCuenta(Model model) {
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        return "mi-cuenta";
    }

    /**
     * Controlador para la página del aviso legal.
     * Añade título estático para mostrar el aviso legal.
     */
    @GetMapping("/aviso-legal")
    public String mostrarAvisoLegal(Model model) {
        model.addAttribute("titulo", "Aviso Legal de XCRM");
        return "aviso-legal";
    }

    /**
     * Controlador para mostrar el formulario de registro.
     * Añade título para la vista de registro.
     */
    @GetMapping("/registro")
    public String muestraRegistro(Model model){
        model.addAttribute("titulo", "REGISTRO");
        return "registro";
    }

    /**
     * Procesa el envío del formulario de registro de nueva organización y usuario administrador.
     * Valida los datos recibidos, muestra errores si los hay y crea la organización y usuario si es válido.
     * Finalmente redirige a la página de login con un mensaje de éxito.
     */
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

        // Valida todos los datos del formulario y agrega posibles errores a la lista
        validarDatosRegistro(nombreEmpresa, nombreAdmin, email, confirmEmail, password, confirmPassword, errores);

        // Si existen errores, los muestra en la página de registro
        if (!errores.isEmpty()) {
            model.addAttribute("errores", errores);
            return "registro";
        }

        // Intenta crear la organización y el usuario administrador, capturando posibles errores
        try {
            Organization nuevaOrganization = organizationService.crearOrganizacion(nombreEmpresa, email, plan);
            userService.crearUsuarioConOrganizacion(nombreAdmin, password, nuevaOrganization, "ROLE_ADMIN");
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
            model.addAttribute("errores", errores);
            return "registro";
        }

        // Si todo sale bien, redirige al login con mensaje de éxito
        redirectAttributes.addFlashAttribute("success", "Registro exitoso. Ahora puedes iniciar sesión.");
        return "redirect:/login";
    }

    /**
     * Método privado que valida los datos de registro recibidos.
     * Verifica unicidad de email, usuario y organización.
     * Comprueba que emails y contraseñas coincidan.
     * Aplica una expresión regular para validar la complejidad de la contraseña.
     * Todos los errores se añaden a la lista recibida para mostrarlos posteriormente.
     */
    private void validarDatosRegistro(String nombreEmpresa, String nombreAdmin, String email, String confirmEmail,
                                      String password, String confirmPassword, List<String> errores) {

        if (organizationService.findByEmail(email).isPresent()) {
            errores.add("El email ya está en uso.");
        }

        if (userService.findByUsername(nombreAdmin) != null) {
            errores.add("El usuario ya existe");
        }

        if (organizationService.findByNombre(nombreEmpresa).isPresent()) {
            errores.add("La organización, con este nombre, ya existe.");
        }

        if (!email.equals(confirmEmail)) {
            errores.add("Los emails no coinciden.");
        }

        if (!password.equals(confirmPassword)) {
            errores.add("Las contraseñas no coinciden.");
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$";
        if (!Pattern.matches(regex, password)) {
            errores.add("La contraseña debe tener al menos una letra mayúscula, una minúscula, un número y al menos 6 caracteres.");
        }
    }
}