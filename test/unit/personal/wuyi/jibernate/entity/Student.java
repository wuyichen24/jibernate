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

@Entity
@Table(name="student")
public class Student extends AbstractAppEntity {
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
}
