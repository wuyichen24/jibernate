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
 * Transformer interface for converting a source object into another. 
 * 
 * <p>Transformations may be chained together via decorator pattern, 
 * consequently there is an implicit assertion that transformed objects 
 * are similar (i.e. the implement a common interface)
 *
 * @param  <X>
 *         The class / type of the original object (before transforming).
 *         
 * @param  <Y>
 *         The class / type of the transformed object (After transforming).
 * 
 * @author  Wuyi Chen
 * @date    10/10/2018
 * @version 1.0
 * @since   1.0
 */
@FunctionalInterface
public interface Transformer<X,Y> {
	/**
	 * Transforms source object to alternate, equivalent representation.
	 * 
	 * <p>Method of transformation may be a straightforward copy between 
	 * equivalent objects, or may result in a more complex change such as 
	 * composition or filtering.
	 *
	 * @param  source
	 * 	       The object needs to be transformed.
	 *
	 * @param  context
	 *         Optional contexts.
	 *
	 * @return  The transformed object.
	 * 
     * @since   1.0
	 */
	Y transform(X source, Object... context);
}
