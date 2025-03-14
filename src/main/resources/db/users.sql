CREATE TABLE IF NOT EXISTS `users` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `organizacion_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`username`),
  KEY `fk_organizacion` (`organizacion_id`),
  CONSTRAINT `fk_organizacion` FOREIGN KEY (`organizacion_id`) REFERENCES `organizaciones` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;