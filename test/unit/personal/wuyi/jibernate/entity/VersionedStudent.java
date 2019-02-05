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
 *  Version Student class for testing other modules.
 * 
 * @author  Wuyi Chen
 * @date    02/05/2018
 * @version 1.1
 * @since   1.1
 */
public class VersionedStudent extends Student implements Versioned {
	private static final long serialVersionUID = 1L;
	
	private Integer       revision;
	private ZonedDateTime revisionDate;
	private Boolean       head;

	@Override
	public Integer getRevision() {
		return revision;
	}

	@Override
	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	@Override
	public ZonedDateTime getRevisionDate() {
		return revisionDate;
	}

	@Override
	public void setRevisionDate(ZonedDateTime revisionDate) {
		this.revisionDate = revisionDate;
	}

	@Override
	public Boolean isHead() {
		return head;
	}

	@Override
	public void setHead(Boolean head) {
		this.head = head;
	}
}
