CREATE TABLE IF NOT EXISTS `clientes_campanias` (
  `cliente_id` bigint(20) NOT NULL,
  `campania_id` bigint(20) NOT NULL,
  PRIMARY KEY (`cliente_id`, `campania_id`),
  KEY `fk_cliente` (`cliente_id`),
  KEY `fk_campania_cliente` (`campania_id`),
  CONSTRAINT `fk_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_campania_cliente` FOREIGN KEY (`campania_id`) REFERENCES `campanias` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;