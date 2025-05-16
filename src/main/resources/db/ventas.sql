CREATE TABLE IF NOT EXISTS `ventas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `interaccion_id` bigint(20) NOT NULL,
  `monto` decimal(10,2) NOT NULL, -- Monto de la venta
  `producto` varchar(255) NOT NULL, -- Producto o servicio vendido
  `fecha` date NOT NULL, -- Fecha de cierre de la venta
  PRIMARY KEY (`id`),
  KEY `fk_venta_interaccion` (`interaccion_id`),
  CONSTRAINT `fk_venta_interaccion` FOREIGN KEY (`interaccion_id`) REFERENCES `interacciones` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;