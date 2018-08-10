package personal.wuyi.jibernate.entity;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.List;

/**
 * The URI (Uniform Resource Identifier) for entity.
 * 
 * <p>The core of this URI class is the string of URI. That string is the 
 * combination of the class path of that entity class and the unique ID 
 * (primary key) of the entity record.
 * 
 * <p>For example, you have an {@code Student} entity record which is under the 
 * com.aabbcc.entity package and its primary key is 24, so the URI string will 
 * look like:
 * <pre>
 *     /com/aabbcc/entity/Student/24
 * </pre>
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.0
 * @since   1.0
 */
public class Uri {
    public final static String SEPARATOR = "/";

    private String uri;

    /** cache for convenience but do not serialize or persist */
    private transient Class<?> clazz;
    private transient Object   id;

    private Uri() {}

    /**
     * Constructs a {@code Uri}.
     *
     * @param  clazz
     *         The class of the entity.
     *         
     * @since   1.0
     */
    public Uri(Class<?> clazz) {
        this.clazz = clazz;
        this.uri   = getPath(clazz);
    }

    /**
     * Constructs a {@code Uri}.
     *
     * @param  clazz
     *         The class of the entity.
     *         
     * @param  id
     *         The unique ID (primary key) of the entity.
     *         
     * @since   1.0
     */
    public Uri(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id    = id;
        this.uri   = Joiner.on("").skipNulls().join(getPath(clazz), id.toString());
    }
    
    /**
	 * Get the {@code Class} of the URI that represents of. 
	 *
	 * @return  The {@code Class} object it represents.
	 * 
     * @since   1.0
	 */
	public Class<?> getType() {
		reload();
		return this.clazz;
	}
    
    /**
     * Get the unique ID of the entity that URI represents of.
     *
     * @return  The unique ID (primary key).
     * 
     * @since   1.0
     */
    public Object getId() {
    	reload();
        return this.id;
    }
    
    /**
     * Reload the unique ID and the class from the URI string.
     * 
     * <p>Because of ID and class are transient, need to parse the URI string 
     * when we want to retrieve them.
     * 
     * @since   1.0
     */
    public void reload() {
    	if(id == null && clazz == null) {
            if(uri != null) {
            	final Uri parsed = Uri.parse(uri);
                this.id    = parsed.id;
                this.clazz = parsed.clazz;
            }
        }
    }

    /**
     * Parse URI string.
     *
     * @param  uriString
     *         The URI string needs to be parsed.
     * 
     * @return  The {@code Uri} object.
     * 
     * @since   1.0
     */
    public static Uri parse(final String uriString) {
        final Uri uri   = new Uri();
        uri.uri         = uriString;
        uri.clazz       = getType(uriString);

        final String classpath = getPath(uri.clazz);

        // if the uri string contains the unique ID.
        if(classpath.length() < uriString.length()) {
            String id = uriString.substring(classpath.length(), uriString.length());
            id = id.trim();

            if(id.length() > 0) {
                uri.id = id;
            }
        }
        return uri;
    }

    /**
     * Get the path portion of URI (uri - path).
     *
     * @return
     */
    public String getPath() {
       return getPath(this.clazz);
    }

    /**
     * Get the URI path of a type (class)
     *
     * @param type
     * @return
     */
    public static String getPath(Class<?> type) {
        StringBuilder path = new StringBuilder();

        if(type != null) {
            String name = type.getName();

            path.append(SEPARATOR).append(name.replaceAll("\\.", SEPARATOR)).append(SEPARATOR);
        }

        return path.toString();
    }

	

    /**
     * Get the type (class) of a URI path
     *
     * @param path
     * @return
     */
    public static Class<?> getType(String path)  {
        if(path == null) {
            return null;
        }

        int last = (path.endsWith("/")) ? path.length() - 1: path.length();
        String name = path.substring(1, last);  // trim beginning/ending separators
        name = name.replaceAll(SEPARATOR, ".");
        Class<?> type = findClass(name);
        return type;
    }

    /**
     * Given URI, try to find a class which best matches pattern.  
     * 
     * <p>This is necessary because domain prefix may be ambiguous (com, org, 
     * net, etc.) and because the ID suffix may be non-numeric, in which case 
     * we may need to guess whether path is just class "type" or "type" + "id".
     *
     * @param name
     * @return
     */
    private static Class<?> findClass(String name) {
        int index = name.indexOf(".");
        String maybe = (index > 0) ? name.substring(0, index) : "";

        // check if domain prefix specified, otherwise try to infer/guess
        List<String> prefixes = Arrays.asList(maybe, "com", "org", "net");

        for(String prefix : prefixes) {
            String qname = prefix + "." + name;
            try {
                Class<?> c = Class.forName(qname);
                return c;
            } catch(Exception e) {
                // eat it
            }
        }

        // if not matched, try removing suffix which may actually be ID
        int subPath = name.lastIndexOf(".");
        if(subPath > 0) {
            name = name.substring(0, subPath);
            return findClass(name);
        }
        
        return null;
    }

    @Override
    public String toString() {
        return uri;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
        		.append(getPath())
        		.append(getId())
        		.toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Uri)) {
            return false;
        }

        Uri uri = (Uri) o;

        if (getPath() != null) {
            if(uri.getPath() != null) {
                if(getPath().equals(uri.getPath()) == false) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            if(uri.getPath() != null) {
                return false;
            }
        }

        if (getId() != null) {
            if(uri.getId() != null) {
                if(getId().equals(uri.getId()) == false) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            if(uri.getId() != null) {
                return false;
            }
        }

        return true;
    }


}

