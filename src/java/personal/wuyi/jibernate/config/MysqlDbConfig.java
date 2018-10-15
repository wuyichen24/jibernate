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

package personal.wuyi.jibernate.config;

import java.io.FileNotFoundException;

import personal.wuyi.jpropertiesorm.annotation.PathX;
import personal.wuyi.jpropertiesorm.annotation.ValueX;
import personal.wuyi.jpropertiesorm.core.ConfigurationX;

/**
 * The configuration class for MySQL connection.
 * 
 * @author  Wuyi Chen
 * @date    08/07/2018
 * @version 1.0
 * @since   1.0
 */
public class MysqlDbConfig {
	@PathX                           private String path;
	
	@ValueX(value="driverClassName") private String driverClassName;
	@ValueX(value="host")            private String host;
	@ValueX(value="port")            private String port;
	@ValueX(value="database")        private String database;
	@ValueX(value="username")        private String username;
	@ValueX(value="password")        private String password;
	
	/**
	 * Constructs a {@code MysqlDbConfig}.
	 * 
	 * @param  path
	 *         The path of the external configuration file.
	 *         
     * @since   1.0
	 */
	public MysqlDbConfig(final String path) {
		this.path = path;
	}
	
	/**
	 * Load parameters from the external configuration file.
	 * 
	 * @return  A new {@code MysqlDbConfig} with the loaded parameters. 
	 * 
	 * @throws  IllegalAccessException
	 *          If a certain field is enforcing Java language access control 
	 *          and the underlying field is either inaccessible or final.
	 *          
	 * @throws  FileNotFoundException
	 *          If the external configuration file does not exist, is a 
	 *          directory rather than a regular file, or for some other reason 
	 *          cannot be opened for reading.
	 *          
     * @since   1.0
	 */
	public MysqlDbConfig initialize() throws IllegalAccessException, FileNotFoundException  {
		return ConfigurationX.bindExternalConfigurationWithInstanceFieldsUsingPathX(this);
	}

	public String getPath()                                  { return path;                            }
	public void   setPath(String path)                       { this.path = path;                       }
	public String getDriverClassName()                       { return driverClassName;                 }
	public void   setDriverClassName(String driverClassName) { this.driverClassName = driverClassName; }
	public String getHost()                                  { return host;                            }
	public void   setHost(String host)                       { this.host = host;                       }
	public String getPort()                                  { return port;                            }
	public void   setPort(String port)                       { this.port = port;                       }
	public String getDatabase()                              { return database;                        }
	public void   setDatabase(String database)               { this.database = database;               }
	public String getUsername()                              { return username;                        }
	public void   setUsername(String username)               { this.username = username;               }
	public String getPassword()                              { return password;                        }
	public void   setPassword(String password)               { this.password = password;               }
}
