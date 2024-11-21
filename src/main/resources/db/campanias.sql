CREATE TABLE IF NOT EXISTS `campanias` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `organizacion_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_campania_organizacion` (`organizacion_id`),
  CONSTRAINT `fk_campania_organizacion` FOREIGN KEY (`organizacion_id`) REFERENCES `organizaciones` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;