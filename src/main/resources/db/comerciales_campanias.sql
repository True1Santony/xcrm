CREATE TABLE IF NOT EXISTS `comerciales_campanias` (
  `comercial_id` binary(16) NOT NULL,
  `campania_id` bigint(20) NOT NULL,
  PRIMARY KEY (`comercial_id`, `campania_id`),
  KEY `fk_comercial` (`comercial_id`),
  KEY `fk_campania` (`campania_id`),
  CONSTRAINT `fk_comercial` FOREIGN KEY (`comercial_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_campania` FOREIGN KEY (`campania_id`) REFERENCES `campanias` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;