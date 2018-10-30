# jibernate
[![Build Status](https://travis-ci.org/wuyichen24/jibernate.svg?branch=master)](https://travis-ci.org/wuyichen24/jibernate)
[![Coverage Status](https://coveralls.io/repos/github/wuyichen24/jibernate/badge.svg?branch=master)](https://coveralls.io/github/wuyichen24/jibernate?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0) 

The JPA (Java Persistence API) module based on the Hibernate ORM framework with the enhanced and simplified query and expression modules.

## Overview
This is a JPA (Java Persistence API) module based on the Hibernate ORM framework. The entity and column mapping is based on annotations rather than a standalone mapping XML file. For example, there is a student table in the database:
```sql
CREATE TABLE `student` (
  `id`                 bigint(20)   NOT NULL AUTO_INCREMENT,
  `first_name`         varchar(255) DEFAULT NULL,
  `last_name`          varchar(255) DEFAULT NULL,
  `dob`                datetime     DEFAULT NULL,
  `gpa`                double       DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
```

So the persistence entity class which maps to this student table will look like this:
```java
@Entity
@Table(name="student")
public class Student extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")           private Long      id;
    @Column(name="first_name")   private String    firstName;
    @Column(name="last_name")    private String    lastName;
    @Column(name="dob")          private Date      dob;
    @Column(name="gpa")          private double    gpa;
}
```

Also, this module avoid writing the persistence file (XML) so that you will not be trapped by too detailed database configurations, you can use specific database configuration class for passing your basic database connection parameters to this module. For example:
```java
MysqlDbConfig dbConfig = new MysqlDbConfig("config/MysqlDb.properties").initialize();
MysqlEntityManagerDao dao = = new MysqlEntityManagerDao(dbConfig);
```

Another highlight of this project is it simplify the query, you don’t have to construct a long JPQL statement. It comes up the idea of Expression which has chain methods for you build up a complex query.
```java
EntityQuery<Student> query = new EntityQuery<Student>(Student.class);
query.setCriteria(new Expression("firstName", Expression.EQUAL, "John"));
List<Student> studentList = dao.read(query);
```

## Getting Started
Please see our [Wiki](https://github.com/wuyichen24/jibernate/wiki/Getting-Started) page.

## Documentation
Please see our [Wiki](https://github.com/wuyichen24/jibernate/wiki) page.

## Download
- [Download ZIP](https://github.com/wuyichen24/jibernate/archive/master.zip)
- [Download JAR](https://github.com/wuyichen24/jibernate/releases/download/v1.0/jibernate-1.0.jar)

## Contributing

## License
[Apache-2.0](https://opensource.org/licenses/Apache-2.0)

## Authors
- **[Wuyi Chen](https://www.linkedin.com/in/wuyichen24/)**
