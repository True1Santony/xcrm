CREATE TABLE IF NOT EXISTS `interacciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comercial_username` varchar(255) NOT NULL,
  `cliente_id` bigint(20) NOT NULL,
  `campania_id` bigint(20) DEFAULT NULL,
  `fecha_hora` datetime NOT NULL,
  `tipo` varchar(50) NOT NULL, -- Ej: 'llamada', 'reunión', 'email'
  `estado` varchar(50) NOT NULL, -- Ej: 'pendiente', 'realizada', 'no contestada', 'cancelada'
  `notas` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_interaccion_comercial` (`comercial_username`),
  KEY `fk_interaccion_cliente` (`cliente_id`),
  KEY `fk_interaccion_campania` (`campania_id`),
  CONSTRAINT `fk_interaccion_comercial` FOREIGN KEY (`comercial_username`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  CONSTRAINT `fk_interaccion_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_interaccion_campania` FOREIGN KEY (`campania_id`) REFERENCES `campanias` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;