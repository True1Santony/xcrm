CREATE TABLE IF NOT EXISTS `clientes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `organizacion_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cliente_organizacion` (`organizacion_id`),
  CONSTRAINT `fk_cliente_organizacion` FOREIGN KEY (`organizacion_id`) REFERENCES `organizaciones` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;