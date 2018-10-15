# jibernate
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0) 

The JPA (Java Persistence API) module based on the Hibernate ORM framework with the enhanced and simplified query and expression modules.

## Overview
This is a JPA (Java Persistence API) module based on the Hibernate ORM framework. The entity and column mapping is based on annotations rather than a standalone mapping XML file. This module avoid writing the persistence file (XML) so that you will not be trapped by too detailed database configurations, you can use specific database configuration class for passing your basic database connection parameters to this module.

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
