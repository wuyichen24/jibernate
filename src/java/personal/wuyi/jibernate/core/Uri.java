package personal.wuyi.jibernate.core;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Arrays;
import java.util.List;

/**
 * URI
 */
public class Uri {
    public final static String SEPARATOR = "/";

    private String uri;

    /** cache for convenience but do not serialize or persist */
    private transient Class<?> type;
    private transient Object   id;

    /**
     * Constructs a {@code Uri}
     * 
     * <p>Should not use the constructor without parameter.
     */
    private Uri() {}

    /**
     * Constructs a {@code Uri}
     *
     * @param type
     */
    public Uri(Class<?> type) {
        this.type = type;
        this.uri  = getPath(type);
    }

    /**
     * Constructs a {@code Uri}
     *
     * @param type
     * @param id
     */
    public Uri(Class<?> type, Object id) {
        this.type = type;
        this.id = id;

        StringBuilder sb = new StringBuilder();
        sb.append(getPath(type));
        if(id != null) {
            // path should end with a '/'
            sb.append(id.toString());
        }

        this.uri = sb.toString();
    }

    /**
     * Parse URI from input path.  Will attempts to determine an implementing Java class corresponding to the URI path.
     *
     * By convention, there is a direct correlation between URI and Class
     *
     * @param path
     * @return
     * @throws ValidationException
     */
    public static Uri parse(String path) {
        Uri uri = new Uri();
        uri.uri = path;
        uri.type = getType(path); // make sure it maps to a concrete class

        String subpath = getPath(uri.type);

        if( subpath.length() < path.length() ) {
            String id = path.substring( subpath.length(), path.length() );
            //id = id.replace("/", "");
            id = id.trim();

            if( id.length() > 0 ) {
                uri.id = id;
            }
        }

        return uri;
    }


    /**
     * Get the ID portion of URI.
     *
     * @return
     */
    public Object getId() {
        // since ID and TYPE are transient, may need to parse
        if(id == null && type == null) {
            if(uri != null) {
                try {
                    Uri parsed = Uri.parse(uri);
                    this.id = parsed.id;
                    this.type = parsed.type;
                }
                catch(Exception e) {}
            }
        }

        return this.id;
    }

    /**
     * Get the path portion of URI (uri - path).
     *
     * @return
     */
    public String getPath() {
       return getPath(this.type);
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
            name = name.substring(4, name.length());  // remove "com" prefix

            path.append(SEPARATOR).append(name.replaceAll("\\.", SEPARATOR)).append(SEPARATOR);
        }

        return path.toString();
    }

	/**
	 * Get the type (class)
	 *
	 * @return
	 */
	public Class<?> getType() {
		// since ID and TYPE are transient, may need to parse
		if (id == null && type == null) {
			if (uri != null) {
				try {
					Uri parsed = Uri.parse(uri);
					this.id = parsed.id;
					this.type = parsed.type;
				} catch (Exception e) {
				}
			}
		}

		return this.type;
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
        name = name.replaceAll( SEPARATOR, "." );
        Class<?> type = findClass( name );
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
        if( subPath > 0 ) {
            name = name.substring(0, subPath);
            return findClass(name);
        }
        
        return null;
    }
}

