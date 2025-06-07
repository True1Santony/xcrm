-- Tabla que guarda los mensajes enviados desde el formulario de contacto.
-- Registra datos del remitente (nombre, email, asunto, mensaje), posibles archivos adjuntos,
-- la fecha de envío y el ID del usuario que lo envió.
-- Está vinculada a la tabla 'users' mediante una clave foránea para mantener relación entre mensajes y usuarios.
-- Si se borra o actualiza un usuario, los mensajes relacionados se actualizan automáticamente (ON DELETE/UPDATE CASCADE).
CREATE TABLE IF NOT EXISTS contacto_mensaje (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,         -- ID único del mensaje
    nombre VARCHAR(255),                          -- Nombre del remitente
    email VARCHAR(255),                           -- Email del remitente
    asunto VARCHAR(255),                          -- Asunto del mensaje
    mensaje TEXT,                                 -- Contenido del mensaje
    archivoDropboxUrl VARCHAR(255),               -- URL del archivo en Dropbox (si lo hay)
    nombreArchivoAdjunto VARCHAR(255),                   -- Nombre del archivo adjunto (si lo hay)
    fechaEnvio TIMESTAMP,                         -- Fecha y hora en que se envió el mensaje
    usuarioId VARCHAR(36) NOT NULL,               -- ID del usuario que lo envía (relacionado con 'users')

    -- Clave foránea: si se elimina o cambia el usuario, se actualiza también aquí
    CONSTRAINT fk_usuarioId
        FOREIGN KEY (usuarioId)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_as_cs;
