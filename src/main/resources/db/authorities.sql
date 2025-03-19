CREATE TABLE IF NOT EXISTS `authorities` (
  `id` binary(16) NOT NULL, -- UUID almacenado como binario
  `user_id` binary(16) NOT NULL,
  `authority` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;