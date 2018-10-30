CREATE DATABASE IF NOT EXISTS `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `test`;

CREATE TABLE IF NOT EXISTS `student` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `dob` datetime DEFAULT NULL,
  `gpa` double DEFAULT NULL,
  `race` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

INSERT INTO student
(first_name,last_name,dob,gpa,race) 
VALUES ('John','Doe','1997-06-10', 3.46, 'ASIAN');

INSERT INTO student
(first_name,last_name,dob,gpa,race) 
VALUES ('John','Doe','1997-06-10', 3.48, 'ASIAN');

INSERT INTO student
(first_name,last_name,dob,gpa,race) 
VALUES ('John','Doe','1997-06-10', 3.52, 'ASIAN');

INSERT INTO student
(first_name,last_name,dob,gpa,race) 
VALUES ('John','Doe','1997-06-10', 3.54, 'ASIAN');

INSERT INTO student
(first_name,last_name,dob,gpa,race) 
VALUES ('John','Doe','1997-06-10', 3.56, 'ASIAN');

INSERT INTO student
(first_name,last_name,dob,gpa,race) 
VALUES ('John','Doe','1997-06-10', 3.58, 'ASIAN');

