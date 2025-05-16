CREATE TABLE IF NOT EXISTS `comerciales_clientes` (
  `comercial_id` binary(16) NOT NULL, -- Coincide con `id` en `users`
  `cliente_id` bigint(20) NOT NULL,
  PRIMARY KEY (`comercial_id`, `cliente_id`),
  KEY `fk_comercial_cliente` (`comercial_id`),
  KEY `fk_cliente_comercial` (`cliente_id`),
  CONSTRAINT `fk_comercial_cliente` FOREIGN KEY (`comercial_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cliente_comercial` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;