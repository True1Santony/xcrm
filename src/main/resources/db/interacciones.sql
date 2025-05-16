CREATE TABLE IF NOT EXISTS `interacciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comercial_id` binary(16) NOT NULL,
  `cliente_id` bigint(20) NOT NULL,
  `campania_id` bigint(20) DEFAULT NULL,
  `fecha_hora` datetime NOT NULL,
  `tipo` varchar(50) NOT NULL, -- Ej: 'llamada', 'reunión', 'email'
  `estado` varchar(50) NOT NULL, -- Ej: 'pendiente', 'realizada', 'no contestada', 'cancelada'
  `notas` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_interaccion_comercial` (`comercial_id`),
  KEY `fk_interaccion_cliente` (`cliente_id`),
  KEY `fk_interaccion_campania` (`campania_id`),
  CONSTRAINT `fk_interaccion_comercial` FOREIGN KEY (`comercial_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_interaccion_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_interaccion_campania` FOREIGN KEY (`campania_id`) REFERENCES `campanias` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;