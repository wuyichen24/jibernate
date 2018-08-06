CREATE TABLE `student` (
  `id`                 bigint(20)   NOT NULL AUTO_INCREMENT,
  `first_name`         varchar(255) DEFAULT NULL,
  `last_name`          varchar(255) DEFAULT NULL,
  `dob`                datetime     DEFAULT NULL,
  `gpa`                double       DEFAULT NULL,
  `race`               varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;