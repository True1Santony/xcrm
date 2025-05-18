CREATE TABLE IF NOT EXISTS `users` (
  `id` binary(16) NOT NULL,                         -- UUID almacenado en formato binario (16 bytes)
  `username` varchar(255) NOT NULL,                 -- Nombre de usuario, obligatorio
  `password` varchar(255) DEFAULT NULL,             -- Contraseña del usuario, puede ser nula
  `enabled` tinyint(1) NOT NULL,                    -- Indica si el usuario está activo (1) o no (0)
  `fotoUrl` varchar(255),                           -- URL de la foto del usuario, opcional
  `organizacion_id` bigint(11) DEFAULT NULL,        -- ID de la organización (clave foránea), puede ser nulo
  `email` varchar(255) NOT NULL,                    -- Correo electrónico del usuario obligatorio
  `plan` varchar(255) DEFAULT NULL,                 -- Columna plan, opcional
  PRIMARY KEY (`id`),                               -- Clave primaria basada en el UUID
  KEY `fk_organizacion` (`organizacion_id`),        -- Índice para búsquedas rápidas por organización
  CONSTRAINT `fk_organizacion`                      -- Clave foránea que enlaza con `organizaciones`
    FOREIGN KEY (`organizacion_id`)
    REFERENCES `organizaciones` (`id`)
    ON DELETE CASCADE                               -- Elimina los usuarios si se elimina la organización
) ENGINE=InnoDB                                     -- Motor de almacenamiento con soporte de transacciones
DEFAULT CHARSET=utf8mb4                             -- Soporte completo para emojis y multilenguaje
COLLATE=utf8mb4_0900_as_cs;                         -- Ordenación sensible a mayúsculas y acentos