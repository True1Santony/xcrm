<script>
    console.log('Script de registro cargado');
        document.getElementById('btnRegistrarUsuario').addEventListener('click', async () => {
        console.log('Botón de registro presionado');  // Verifica si el evento se ejecuta

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const organizacionId = document.getElementById('organizacionId').value;

            // Crear el payload
            const payload = {
                username: username,
                password: password
            };

            try {
                // Hacer la solicitud POST a la API
                const response = await fetch(`/api/admin/addUser?organizacionId=${organizacionId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    const mensaje = await response.text();
                    alert(mensaje); // Mostrar mensaje de éxito
                    location.reload(); // Recargar la página
                } else {
                    const error = await response.text();
                    alert(`Error: ${error}`); // Mostrar mensaje de error
                }
            } catch (error) {
                console.error('Error al registrar usuario:', error);
                alert('Ocurrió un error al registrar el usuario.');
            }
        });
    </script>