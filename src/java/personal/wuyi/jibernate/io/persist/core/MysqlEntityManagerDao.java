package personal.wuyi.jibernate.io.persist.core;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import personal.wuyi.jibernate.io.persist.config.MysqlDbConfig;

public class MysqlEntityManagerDao extends AbstractEntityManagerDao {
	MysqlDbConfig config;
	
	protected MysqlEntityManagerDao(MysqlDbConfig config) {
		this.config = config;
	}
	
	@Override
	protected String getDialect() {
		return "org.hibernate.dialect.MySQLDialect";
	}

	@Override
	protected DataSource getDataSource() {
        BasicDataSource dbcpDataSource = new BasicDataSource();
        dbcpDataSource.setDriverClassName(config.getDriverClassName());
        dbcpDataSource.setUrl("jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabase() + "?useSSL=true");
        dbcpDataSource.setUsername("root");
        dbcpDataSource.setPassword("password1");
        dbcpDataSource.setInitialSize(10);
        dbcpDataSource.setMaxTotal(20);
        return dbcpDataSource;
	}

	@Override
	protected String getPersistenceUnit() {
		return "autostock.persistence";
	}

}
