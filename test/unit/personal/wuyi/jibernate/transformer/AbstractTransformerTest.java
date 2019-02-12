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
package personal.wuyi.jibernate.transformer;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

/**
 * The test class for {@code AbstractTransformer}.
 * 
 * @author  Wuyi Chen
 * @date    02/12/2018
 * @version 1.1
 * @since   1.1
 */
public class AbstractTransformerTest {
	/**
	 * Test AbstractTransformer(Transformer<X,Y> transformer)
	 * 
	 * <p>Let {@code TransformerHelper} class be the sub-class of {@code AbstractTransformer} class
	 * so that it can call that AbstractTransformer(Transformer<X,Y> transformer) constructor 
	 * (sub-class constructor can call super-class constructor).
	 * 
	 * <p>Create a {@code DemoTransformer} class and let it implements {@code Transformer} interface, 
	 * so that this class can be injected into {@code AbstractTransformer} class.
	 * 
	 * <p>Verify the "transformer" field in {@code AbstractTransformer} is an instance of {@code DemoTransformer} 
	 * class after injection.
	 */
	@Test
	public void constructorTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TransformerHelper<String, String> helper  = new TransformerHelper<>(new DemoTransformer<>());
		Field f = helper.getClass().getSuperclass().getDeclaredField("transformer");
		f.setAccessible(true);
		Object value = f.get(helper);
		Assert.assertTrue(value instanceof DemoTransformer);
	}
	
	@Test
	public void transformTest() {
		// test the case: transformer has NOT been injected into AbstractTransformer
		TransformerHelper<String, String> helper1 = new TransformerHelper<>();
		Assert.assertEquals("abc", helper1.transform("abc", "123", "XYZ", 679));
		
		// test the case: transformer has been injected into AbstractTransformer
		TransformerHelper<String, String> helper2 = new TransformerHelper<>(new DemoTransformer<>());
		Assert.assertEquals("abc: 123,XYZ,679,", helper2.transform("abc", "123", "XYZ", 679));
	}
}
