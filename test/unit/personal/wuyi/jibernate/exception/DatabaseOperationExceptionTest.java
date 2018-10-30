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

package personal.wuyi.jibernate.exception;

import org.junit.Test;
import junit.framework.Assert;

/**
 * Test class for {@code DatabaseOperationException}.
 * 
 * @author  Wuyi Chen
 * @date    10/30/2018
 * @version 1.1
 * @since   1.1
 */
public class DatabaseOperationExceptionTest {	
	@Test
	public void constructorTest() {
		Exception e1 = new DatabaseOperationException("aabbccdd");
		Assert.assertEquals("aabbccdd", e1.getMessage());
		
		Exception e2 = new DatabaseOperationException(new IllegalArgumentException());
		Assert.assertEquals(IllegalArgumentException.class, e2.getCause().getClass());
		
		Exception e3 = new DatabaseOperationException("aabbccdd", new IllegalArgumentException());
		Assert.assertEquals("aabbccdd", e3.getMessage());
		Assert.assertEquals(IllegalArgumentException.class, e3.getCause().getClass());
	}
}
