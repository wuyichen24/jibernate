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

package personal.wuyi.jibernate.query;

import personal.wuyi.jibernate.entity.Persisted;

/**
 * The class represent the query to the database.
 *
 * @author  Wuyi Chen
 * @date    09/19/2018
 * @version 1.1
 * @since   1.0
 */
public class EntityQuery<E extends Persisted> extends JQuery<E> {
    protected String jpql;

    /**
     * Constructs a {@code EntityQuery}.
     * 
     * @since   1.0
     */
    protected EntityQuery() {}

    /**
     * Constructs a {@code EntityQuery}.
     * 
     * @param  clazz
     *         The class of an entity needs to be queried.
     * 
     * @since   1.0
     */
    public EntityQuery(Class<E> clazz) {
        super(clazz);
    }

    public String getJpql()            { return jpql;      }
    public void   setJpql(String jpql) { this.jpql = jpql; }
}
