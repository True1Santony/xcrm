package com.xcrm.controller.web;

import com.xcrm.model.Authority;
import com.xcrm.model.Organizacion;
import com.xcrm.model.User;
import com.xcrm.repository.AuthorityRepository;
import com.xcrm.service.AuthorityService;
import com.xcrm.service.OrganizacionService;
import com.xcrm.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizacionService organizacionService;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    private AuthorityService authorityService;

    private PasswordEncoder passwordEncoder;

    @ModelAttribute("titulo")
    public String title() {
        return "Registro de Usuarios";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid
                                       @ModelAttribute("nuvoUsuario") User nuevoUsuario,
                                   @RequestParam Long organizacionId, BindingResult almacenErrores,
                                   Model model) {

        // Buscar la organización por ID
        Organizacion organizacion = organizacionService.buscarPorId(organizacionId);

        if (organizacion == null) {
            model.addAttribute("error", "La organización no existe.");
            return "mi-cuenta"; // Regresar a la vista
        }

        try {
            userService.createUserInOrganization(nuevoUsuario.getUsername(), nuevoUsuario.getPassword(), organizacion);
            model.addAttribute("success", "Usuario registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("loggedIn", true);

        return "redirect:/usuarios/administration";
    }

    @GetMapping("/administration")
    public String getUserAdministrationDashboard(Model model){
        //esto se repite en indexController mi-cuenta, encapsular en un metodo de organizacion
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        //obtengo de la Base de datos la Organizacion a partir del username
        Optional<Organizacion> organizacionOptional = organizacionService
                .obtenerPorId(userService
                        .obtenerUsuarioPorNombre(username)
                        .getOrganizacion()
                        .getId());

        model.addAttribute("organizacion", organizacionOptional.get()); // Paso la organización activa
        model.addAttribute("nuevoUsuario", new User());

        return "user-administration-dashboard";
    }

    // Método para editar un usuario
    @Transactional
    @PostMapping("/editar")
    public String editarUsuario(@RequestParam("user_id") UUID userId,
                                @RequestParam("nuevoUsername") String nuevoUsername,
                                @RequestParam(value = "roles", required = false) String[] roles,
                                @RequestParam(value = "nuevoPassword", required = false) String nuevoPassword,
                                Model model) {
        // Obtener el usuario por su nombre de usuario


        User usuario = userService.find(userId);

        System.out.println(usuario.getId()+" "+ usuario.getUsername());

        // Si se ha cambiado el nombre de usuario, actualizarlo
        if (nuevoUsername != null && !nuevoUsername.isEmpty() && !nuevoUsername.equals(usuario.getUsername())) {
            usuario.setUsername(nuevoUsername);
        }

        // Si se ha proporcionado una nueva contraseña, actualizarla
        if (nuevoPassword != null && !nuevoPassword.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(nuevoPassword));
        }

        // Eliminar las autoridades existentes
        System.out.println("Antes de limpiar  "+usuario.getAuthorities()); // Limpiar las autoridades anteriores

        for (Authority authority : usuario.getAuthorities()) {
            authorityRepository.delete(authority);
        }
        usuario.getAuthorities().clear();



        userService.save(usuario);

        usuario = userService.find(userId);
        System.out.println("Despues de limpiar"+usuario.getAuthorities()); // Limpiar las autoridades anteriores


        // Actualizar los roles del usuario
        Set<Authority> authorities = new HashSet<Authority>();
        for(String rol : roles){
            Authority authority = new Authority(UUID.randomUUID(),usuario,rol);
            authorities.add(authority);
        }

        usuario.setAuthorities(authorities);

        userService.save(usuario);

        System.out.println(usuario.getAuthorities());

        model.addAttribute("mensaje", "Usuario actualizado correctamente");

        return "redirect:/usuarios/administration"; // Redirige al listado de usuarios o al lugar que desees
    }

    // Método para eliminar un usuario
    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam("username") String username, Model model) {
        // Lógica para eliminar el usuario
        // Obtener el usuario por su nombre de usuario y eliminarlo
        // Por ejemplo: usuarioRepository.deleteByUsername(username);

        model.addAttribute("mensaje", "Usuario eliminado correctamente");

        return "redirect:/usuarios"; // Redirige al listado de usuarios
    }

    // Método para convertir UUID a byte[]
    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

}
