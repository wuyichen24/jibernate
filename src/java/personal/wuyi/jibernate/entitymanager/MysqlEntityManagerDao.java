/*
 * Copyright 2018 Wuyi Chen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package personal.wuyi.jibernate.entitymanager;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import personal.wuyi.jibernate.config.MysqlDbConfig;

/**
 * The DAO (Data Access Object) for MySQL database.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.0
 * @since   1.0
 */
public class MysqlEntityManagerDao extends AbstractEntityManagerDao {
	MysqlDbConfig config;
	
	/**
	 * Constructs a {@code MysqlEntityManagerDao}.
	 * 
	 * @param  config
	 *         The configuration for MySQL connection.
	 * 
     * @since   1.0
	 */
	public MysqlEntityManagerDao(MysqlDbConfig config) {
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
        dbcpDataSource.setUsername(config.getUsername());
        dbcpDataSource.setPassword(config.getPassword());
        dbcpDataSource.setInitialSize(10);
        dbcpDataSource.setMaxTotal(20);
        return dbcpDataSource;
	}

	@Override
	protected String getPersistenceUnit() {
		return "mysql.persistence";
	}

}
