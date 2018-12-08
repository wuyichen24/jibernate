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
package personal.wuyi.jibernate.test;

import java.sql.ResultSet;
import java.sql.SQLException;

import personal.wuyi.client.database.GenericDbClient;

/**
 * The tool class for GenericDbClientUtil.
 * 
 * <p>This tool class is for testing only.
 * 
 * @author  Wuyi Chen
 * @date    12/07/2018
 * @version 1.1
 * @since   1.1
 */
public class GenericDbClientUtil {
	private GenericDbClientUtil() {}
	
	/**
	 * Get the number of records in a {@code ResultSet}.
	 * 
	 * @param  dbService
	 *         The instance of the {@code GenericDbClient} object.
	 * 
	 * @param  tableName
	 *         The name of the table needs to be queried.
	 * 
	 * @param  whereClause
	 *         The criteria of the query.
	 * 
	 * @return  The number of records in the {@code ResultSet}.
	 * 
	 * @throws  SQLException
	 *          If there is any error occurred when executing the query.
	 *          
     * @since   1.1
	 */
	public static int getNumberOfRecords(GenericDbClient dbService, String tableName, String whereClause) throws SQLException {
		String sql = "select count(*) from " + tableName + " where " + whereClause;
		ResultSet rs = dbService.executeQuery(sql);
		int expectedCount = 0;
		while (rs.next()) {
			expectedCount = rs.getInt(1);
	    }
		return expectedCount;
	}
}
