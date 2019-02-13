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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Student class for testing other modules.
 * 
 * @author  Wuyi Chen
 * @date    10/15/2018
 * @version 1.1
 * @since   1.0
 */
@Entity
@Table(name="student")
public class Student extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")           private Long      id;
	@Column(name="first_name")   private String    firstName;
	@Column(name="last_name")    private String    lastName;
	@Column(name="dob")          private Date      dob;
	@Column(name="gpa")          private double    gpa;
	@Enumerated(EnumType.STRING)
	@Column(name="race")         private Ethnicity race;
	
	public Long      getId()                        { return id;                  }
	public void      setId(Long id)                 { this.id = id;               }
	public String    getFirstName()                 { return firstName;           }
	public void      setFirstName(String firstName) { this.firstName = firstName; }
	public String    getLastName()                  { return lastName;            }
	public void      setLastName(String lastName)   { this.lastName = lastName;   }
	public Date      getDob()                       { return dob;                 }
	public void      setDob(Date dob)               { this.dob = dob;             }
	public double    getGpa()                       { return gpa;                 }
	public void      setGpa(double gpa)             { this.gpa = gpa;             }
	public Ethnicity getRace()                      { return race;                }
	public void      setRace(Ethnicity race)        { this.race = race;           }
	
	public Student() { }
	
	public Student(String firstName, String lastName, double gpa) {
		this.firstName = firstName;
		this.lastName  = lastName;
		this.gpa       = gpa;
	}
	
	public String toString() {
		return firstName + "," + lastName + ":" + gpa;
	}
}
