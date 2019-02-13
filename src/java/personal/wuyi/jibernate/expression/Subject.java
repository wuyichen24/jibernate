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

package personal.wuyi.jibernate.expression;

import java.io.Serializable;

/**
 * The class represents the subject of an expression.
 * 
 * @author  Wuyi Chen
 * @date    09/17/2018
 * @version 1.1
 * @since   1.0
 */
public class Subject implements Cloneable, Serializable {
	private static final long serialVersionUID = 1248241253649789268L;

	private           String name;
	private transient Object value;

	/**
	 * Constructs a {@code Subject}.
	 * 
     * @since   1.0 
	 */
	public Subject() {

	}

	/**
	 * Constructs a {@code Subject}.
	 * 
	 * @param  name
	 *         The name of a subject.
	 *         
     * @since   1.0 
	 */
	public Subject(String name) {
		this.name = name;
	}

	/**
	 * Constructs a {@code Subject}.
	 * 
	 * @param  name
	 *         The name of a subject.
	 *         
	 * @param  value
	 *         The value of a subject.
	 *         
     * @since   1.0 
	 */
	public Subject(String name, Object value) {
		this.name  = name;
		this.value = value;
	}
	
	public String getName()              { return name;        }
	public void   setName(String name)   { this.name = name;   }
	public Object getValue()             { return value;       }
	public void   setValue(Object value) { this.value = value; }

	@Override
	public Object clone() {
		try {
			Subject cloned = (Subject) super.clone();

			if(value != null) {
				Object copied = value;
				cloned.setValue(copied);
			}

			return(cloned);
		} catch(CloneNotSupportedException e) {
			throw(new InternalError("Cloneable support but cannot clone"));
		}
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}

		if(!(o instanceof Subject)) {
			return false;
		}

		Subject attribute = (Subject) o;

		if(getName() != null) {
			if(!getName().equals(attribute.getName())) {
				return(false);
			}
		} else if(attribute.getName() != null) {
			return(false);
		}

		if(getValue() != null) {
			if(!getValue().equals(attribute.getValue())) {
				return(false);
			}
		} else if(attribute.getValue() != null) {
			return(false);
		}

		return true;
	}
}
