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

package personal.wuyi.jibernate.entity;

/**
 * The abstract class for implementing generic entity classes.
 * 
 * <p>All other entity classes on your own should inherit this class.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.1
 * @since   1.0
 */
abstract class AbstractEntity implements ManagedEntity {
	private static final long serialVersionUID = 1L;

    @Override
	abstract public Long getId();

    @Override
    public Uri getUri() {
    	// Let it call the default method from interface
    	// https://zeroturnaround.com/rebellabs/java-8-explained-default-methods/
    	return ManagedEntity.super.getUri();
    }

    @Override
    public boolean isPersisted() {
        return (getId() != null);
    }
}
