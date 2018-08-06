package personal.wuyi.autostock.io.persist.expression;

import java.io.Serializable;

/**
 * Subject
 * 
 * <p>This class represents the subject of an expression
 */
public class Subject implements Cloneable, Serializable {
	private static final long serialVersionUID = 1248241253649789268L;

	private String name = null;
	private Object value = null;

	/**
	 * Constructs a {@code Subject}
	 */
	public Subject() {

	}

	/**
	 * Constructs a {@code Subject}
	 * 
	 * @param name
	 */
	public Subject(String name) {
		this.name = name;
	}

	/**
	 * Constructs a {@code Subject}
	 * 
	 * @param name
	 * @param value
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

		if(o instanceof Subject == false) {
			return false;
		}

		Subject attribute = (Subject) o;

		if(getName() != null) {
			if(getName().equals(attribute.getName()) == false) {
				return(false);
			}
		} else if(attribute.getName() != null) {
			return(false);
		}

		if(getValue() != null) {
			if(getValue().equals(attribute.getValue()) == false) {
				return(false);
			}
		} else if(attribute.getValue() != null) {
			return(false);
		}

		return true;
	}
}
