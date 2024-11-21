CREATE TABLE IF NOT EXISTS `comerciales_campanias` (
  `comercial_username` varchar(255) NOT NULL,
  `campania_id` bigint(20) NOT NULL,
  PRIMARY KEY (`comercial_username`, `campania_id`),
  KEY `fk_comercial` (`comercial_username`),
  KEY `fk_campania` (`campania_id`),
  CONSTRAINT `fk_comercial` FOREIGN KEY (`comercial_username`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  CONSTRAINT `fk_campania` FOREIGN KEY (`campania_id`) REFERENCES `campanias` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;