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

/**
 * The helper class for testing {@code AbstractTransformer}.
 * 
 * <p>Create a {@code DemoTransformer} class and let it implements {@code Transformer} interface, 
 * so that this class can be injected into {@code AbstractTransformer} class.
 * 
 * @author  Wuyi Chen
 * @date    02/12/2018
 * @version 1.1
 * @since   1.1
 */
public class DemoTransformer<X, Y> implements Transformer<X,Y> {
	@SuppressWarnings("unchecked")
	@Override
	public Y transform(X source, Object... context) {
		StringBuilder sb = new StringBuilder();
		sb.append(source.toString());
		if (context != null) {
			sb.append(": ");
			for (Object obj : context) {
				sb.append(obj.toString());
				sb.append(",");
			}
		}
		
		return (Y) sb.toString();
	}
}
