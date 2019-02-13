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
 * Abstract transformer.
 * 
 * <p>This class will be an abstract transformer and needs to be initialized 
 * by a specific transformer. The transform method will transform a object to 
 * another object in different or same type.
 * 
 * <p>By default, most transformers are expected to implements the decorator 
 * pattern, but are not strictly required to do so.
 * 
 * @param  <X>
 *         The class / type of the original object (before transforming).
 *         
 * @param  <Y>
 *         The class / type of the transformed object (After transforming).
 * 
 * @author  Wuyi Chen
 * @date    10/09/2018
 * @version 1.1
 * @since   1.0
 */
public abstract class AbstractTransformer<X,Y> implements Transformer<X,Y> {
	private Transformer<X,Y> transformer = null;
	
	/**
	 * Constructs a {@code AbstractTransformer}.
	 * 
     * @since   1.0
	 */
	public AbstractTransformer() {}

	/**
	 * Constructs a {@code AbstractTransformer}.
	 * 
	 * @param  transformer
	 *         The specific transformer object will do the transforming.
	 *         
     * @since   1.0
	 */
	public AbstractTransformer(Transformer<X,Y> transformer) {
		this.transformer = transformer;
	}

	@Override
	public Y transform(X source, Object... context)  {
		@SuppressWarnings("unchecked")
		Y transformed = (Y) source;

		if(transformer != null) {
			transformed = transformer.transform(source, context);
		}

		return transformed;
	}
}
