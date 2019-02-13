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
package personal.wuyi.jibernate.util;

import personal.wuyi.jibernate.entity.Student;

/**
 * The helper class for testing {@code ReflectUtil2}.
 * 
 * <p>Create this class for nesting {@code Student} class so that the 
 * ReflectUtil2.getPropertyMap() function can be tested with recursive option 
 * on.
 * 
 * @author  Wuyi Chen
 * @date    02/12/2018
 * @version 1.1
 * @since   1.1
 */
public class ClassRoom {
	private Student studentA;
	private Student studentB;
	private Student studentC;
	
	public Student getStudentA()                 { return studentA;          }
	public void    setStudentA(Student studentA) { this.studentA = studentA; }
	public Student getStudentB()                 { return studentB;          }
	public void    setStudentB(Student studentB) { this.studentB = studentB; }
	public Student getStudentC()                 { return studentC;          }
	public void    setStudentC(Student studentC) { this.studentC = studentC; }
}
