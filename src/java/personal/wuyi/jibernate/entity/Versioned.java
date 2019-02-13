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

import java.time.ZonedDateTime;


/**
 * The interface for version-controlled data. 
 * 
 * <p>The versioned objects have a "revision" number indicating their 
 * sequential historical ordering. The most recent revision is designated as 
 * the "master".
 * 
 * @author  Wuyi Chen
 * @date    08/10/2018
 * @version 1.1
 * @since   1.0
 */
public interface Versioned extends Persisted {
    /**
     * Get the sequential historical revision order.
     *
     * @return  The revision number of the current entity.
     * 
     * @since   1.0
     */
    Integer getRevision();

    /**
     * Set the sequential historical revision order.
     *
     * @param  revision
     *         The revision number of the current entity. 
     *         
     * @since   1.0
     */
    void setRevision(Integer revision);

    /**
     * Get the revision date time.
     *
     * @return  The date time of the revision.
     * 
     * @since   1.0
     */
    ZonedDateTime getRevisionDate();

    /**
     * Set the revision date time.
     *
     * @param  revisionDate
     *         The date time of the revision.
     *         
     * @since   1.0
     */
    void setRevisionDate(ZonedDateTime revisionDate);


    /**
     * Check the current revision is the head revision or not.
     *
     * @return  {@code true} if the current revision is the head revision;
     *          {@code false} otherwise.
     *          
     * @since   1.0
     */
    Boolean isHead();


    /**
     * Set the current revision as the head revision.
     *
     * @param  head
     *         The current revision is the head revision or not.
     *         
     * @since   1.0
     */
    void setHead(Boolean head);
}
