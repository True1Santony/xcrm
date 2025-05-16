CREATE TABLE `contacto_mensaje` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `archivoDropboxUrl` varchar(255) DEFAULT NULL,
  `archivoNombre` varchar(255) DEFAULT NULL,
  `asunto` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `fechaEnvio` datetime(6) DEFAULT NULL,
  `mensaje` varchar(5000) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `usuarioId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs