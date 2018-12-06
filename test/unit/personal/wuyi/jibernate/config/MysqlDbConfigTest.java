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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for MysqlDbConfig
 * 
 * @author  Wuyi Chen
 * @date    12/06/2018
 * @version 1.1
 * @since   1.1
 */
public class MysqlDbConfigTest {
	private static MysqlDbConfig dbConfig;
	
	@Before
	public void setupDbConfig() throws IllegalAccessException, FileNotFoundException {
		dbConfig = new MysqlDbConfig("config/MysqlDb.properties").initialize();
	}
	
	@Test
	public void gettersAndSettersTest() {		
		dbConfig.setPath("config/MysqlDbAbc.properties");
		Assert.assertEquals("config/MysqlDbAbc.properties", dbConfig.getPath());
		
		dbConfig.setDriverClassName("com.mysql.jdbc.Driver");
		Assert.assertEquals("com.mysql.jdbc.Driver", dbConfig.getDriverClassName());
		
		dbConfig.setHost("us-west-2.rds.amazonaws.com");
		Assert.assertEquals("us-west-2.rds.amazonaws.com", dbConfig.getHost());
		
		dbConfig.setPort("2280");
		Assert.assertEquals("2280", dbConfig.getPort());
		
		dbConfig.setDatabase("ods_test");
		Assert.assertEquals("ods_test", dbConfig.getDatabase());
		
		dbConfig.setUsername("alexwang");
		Assert.assertEquals("alexwang", dbConfig.getUsername());
		
		dbConfig.setPassword("AjK4U8Pw");
		Assert.assertEquals("AjK4U8Pw", dbConfig.getPassword());
	}
}
