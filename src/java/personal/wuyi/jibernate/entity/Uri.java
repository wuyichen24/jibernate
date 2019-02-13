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

import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

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
 * @version 1.1
 * @since   1.0
 */
public class Uri {
    public static final String SEPARATOR = "/";

    private String uriString;

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
        this.clazz     = clazz;
        this.uriString = getPath(clazz);
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
        this.clazz     = clazz;
        this.id        = id;
        this.uriString = Joiner.on("").skipNulls().join(getPath(clazz), id.toString());
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
    	if(id == null && clazz == null && uriString != null) {
            final Uri parsed = Uri.parse(uriString);
            this.id          = parsed.id;
            this.clazz       = parsed.clazz;
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
        uri.uriString   = uriString;
        String path     = getClassPathWithoutId(uriString);
        uri.clazz       = getType(path);

        final String classpath = getPath(uri.clazz);

        // if the uri string contains the unique ID.
        if(classpath.length() < uriString.length()) {
            String id = uriString.substring(classpath.length(), uriString.length());
            id = id.trim();

            if(id.length() > 0) {
                uri.id = Integer.parseInt(id);
            }
        }
        return uri;
    }
    
    private static String getClassPathWithoutId(String uriString) {
        List<String> classPathList = Lists.newArrayList(Splitter.on('/').omitEmptyStrings().split(uriString));
        if (NumberUtils.isCreatable(classPathList.get(classPathList.size() - 1))) {
        		classPathList.remove(classPathList.size() - 1);	
        }
        return Joiner.on("").join(SEPARATOR, Joiner.on("/").join(classPathList), SEPARATOR);
    }

    /**
     * Get the class path of the class which this {@code URI} represents of.
     *
     * @return  The class path of a class.
     * 
     * @since   1.0
     */
    public String getPath() {
    	return getPath(this.clazz);
    }

    /**
     * Get the class path of a class.
     * 
     * <p>If the {@code Class} object is null, the class path will be empty 
     * string.
     *
     * @param  clazz
     *         The class need to get path.
     *         
     * @return  The class path of the class.
     * 
     * @since   1.0
     */
    public static String getPath(Class<?> clazz) {
        if(clazz != null) {
            return Joiner.on("").join(SEPARATOR, clazz.getName().replaceAll("\\.", SEPARATOR), SEPARATOR);
        } else {
        	return null;
        }
    }

    /**
     * Get the {@code Class} by the class path.
     * 
     * <p>If the class is not found based on the path or the path is null or 
     * empty, this function will return {@code null}.
     *
     * @param  path
     *         The class path of a class.
     *         
     * @return  The {@code Class} based on the path.
     * 
     * @since   1.0
     */
    public static Class<?> getType(String path) {
        if(Strings.isNullOrEmpty(path)) {
            return null;
        }

        int last  = (path.endsWith("/")) ? path.length() - 1: path.length();
        int first = (path.startsWith("/")) ? 1 : 0;
        String packageName = path.substring(first, last).replaceAll(SEPARATOR, ".");
        try {
        		return Class.forName(packageName);
        } catch (ClassNotFoundException e) {
        		return null;
        }
    }

    @Override
    public String toString() {
        return uriString;
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

        return isEqualPath(uri) && isEqualId(uri);
    }
    
    /**
     * Check the class path of this {@code Uri} is same with class path of the 
     * provided {@code Uri}.
     * 
     * @param  uri
     *         The {@code Uri} needs to be compared with this {@code Uri}.
     *         
     * @return  {@code true} if the class path is equal between this and the 
     *          provided {@code Uri};
     *          {@code false} otherwise.
     *          
     * @since   1.0
     */
    private boolean isEqualPath(Uri uri) {
    	if (getPath() != null) {
            if(uri.getPath() != null) {
                if(!getPath().equals(uri.getPath())) {
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
    	
    	return true;
    }
    
    /**
     * Check the unique ID of this {@code Uri} is same with unique ID of the 
     * provided {@code Uri}.
     * 
     * @param  uri
     *         The {@code Uri} needs to be compared with this {@code Uri}.
     *         
     * @return  {@code true} if the unique ID is equal between this and the 
     *          provided {@code Uri};
     *          {@code false} otherwise.
     *          
     * @since   1.0
     */
    private boolean isEqualId(Uri uri) {
    	if (getId() != null) {
            if(uri.getId() != null) {
                if(!getId().equals(uri.getId())) {
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

