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
 * The interface to define common managed entity.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.1
 * @since   1.0
 */
public interface ManagedEntity extends Persisted {
    /**
     * Return the primary key.
     * 
     * <p>This is the unique identifier among entities in same type.
     *
     * @return  The object representing the primary key.
     * 
     * @since   1.0
     */
    Object getId();

    @Override
    default Uri getUri() {
        return new Uri(this.getClass(), getId());
    }
}
