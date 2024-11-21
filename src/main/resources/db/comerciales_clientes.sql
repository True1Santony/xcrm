CREATE TABLE IF NOT EXISTS `comerciales_clientes` (
  `comercial_username` varchar(255) NOT NULL,
  `cliente_id` bigint(20) NOT NULL,
  PRIMARY KEY (`comercial_username`, `cliente_id`),
  KEY `fk_comercial_cliente` (`comercial_username`),
  KEY `fk_cliente_comercial` (`cliente_id`),
  CONSTRAINT `fk_comercial_cliente` FOREIGN KEY (`comercial_username`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  CONSTRAINT `fk_cliente_comercial` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;