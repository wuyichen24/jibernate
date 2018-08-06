package personal.wuyi.jibernate.io.persist.config;

import java.io.IOException;

import personal.wuyi.jpropertiesorm.annotation.PathX;
import personal.wuyi.jpropertiesorm.annotation.ValueX;
import personal.wuyi.jpropertiesorm.core.ConfigurationX;

public class MysqlDbConfig {
	@PathX
	private String path;
	
	@ValueX(value="driverClassName")
	private String driverClassName;
	@ValueX(value="host")
	private String host;
	@ValueX(value="port")
	private String port;
	@ValueX(value="database")
	private String database;
	@ValueX(value="username")
	private String username;
	@ValueX(value="password")
	private String password;
	
	public MysqlDbConfig(String path) {
		this.path = path;
	}
	
	public MysqlDbConfig initialize() throws IllegalArgumentException, IllegalAccessException, IOException {
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
