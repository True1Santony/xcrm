CREATE TABLE IF NOT EXISTS contactomensaje (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Identificador único para cada mensaje
    archivoDropboxUrl VARCHAR(255),        -- URL del archivo adjunto en Dropbox, si existe
    archivoNombre VARCHAR(255),            -- Nombre del archivo adjunto, si existe
    asunto VARCHAR(255),                   -- Asunto del mensaje
    email VARCHAR(255),                    -- Email del remitente
    fechaEnvio TIMESTAMP,                  -- Fecha y hora de envío del mensaje
    mensaje TEXT,                          -- Contenido del mensaje
    nombre VARCHAR(255),                   -- Nombre del remitente
    usuarioId VARCHAR(36) NOT NULL,        -- ID del usuario que envió el mensaje (vinculado a la tabla 'users')
    CONSTRAINT fk_usuarioId FOREIGN KEY (usuarioId) REFERENCES users (id)  -- Relación con la tabla 'users'
        ON DELETE CASCADE                  -- Si el usuario se elimina, también se eliminan sus mensajes
        ON UPDATE CASCADE                  -- Si el ID del usuario cambia, también se actualiza el ID en los mensajes
) ENGINE=InnoDB DEFAULT CHARSET=latin1;