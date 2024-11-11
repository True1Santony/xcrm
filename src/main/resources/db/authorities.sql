CREATE TABLE IF NOT EXISTS `authorities` (
  `username` varchar(255) NOT NULL,
  `authority` varchar(255) NOT NULL,
  PRIMARY KEY (`username`, `authority`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;