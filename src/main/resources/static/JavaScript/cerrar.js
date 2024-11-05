// Almacenar en sessionStorage al iniciar sesión
sessionStorage.setItem("loggedIn", "true");

// Al cerrar la ventana
window.onbeforeunload = function() {
    sessionStorage.removeItem("loggedIn");
};

// Verificar al cargar la página
window.onload = function() {
    if (!sessionStorage.getItem("loggedIn")) {
        // Redirigir al usuario a la página de inicio de sesión
        window.location.href = "/";
    }
};
