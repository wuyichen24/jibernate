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

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The tool class for manipulating {@code String}.
 *
 * @author  Wuyi Chen
 * @date    10/12/2018
 * @version 1.0
 * @since   1.0
 */
public class StringUtil {
	private StringUtil() {}
	
	/**
     * Join a collection to objects in string using default Object.toString() 
     * method.
     *
     * @param  delimiter
     *         The delimiter for concatenating each string.
     *         
     * @param  collection
     *         The collection of objects.
     *         
     * @return  The joined string of the collection.
     * 
     * @since   1.0
     */
    public static <T> String join(String delimiter, Collection<T> collection) {
        return join(delimiter, collection, n -> n.toString());
    }

    /**
     * Join a collection to objects in string.
     *
     * @param  delimiter
     *         The delimiter for concatenating each string.
     *         
     * @param  collection
     *         The collection of objects.
     *         
     * @param  formatter
     *         The function to generate the string by element in the list.
     * 
     * @return  The joined string of the collection.
     * 
     * @since   1.0
     */
    public static <T> String join(String delimiter, Collection<T> collection, Function<T,String> formatter) {
        return collection.stream()
                .map(formatter)
                .collect(Collectors.joining(delimiter));

    }
    
    /**
     * Replace a string with another string.
     *
     * @param  string       
     *         The input string.
     * 
     * @param  targetStr
     *         The string to match.
     *         
     * @param  replaceStr
     *         The string to replace by.
     * 
     * @param  isWholeWord  
     *         The flag indicating that matching should occur on whole word 
     *         or phrases only.
     *         
     * @param  isIgnoreCase 
     *         The flag indicating that matching should ignore case.
     *         
     * @return  The modified string or null if not matched.
     * 
     * @since   1.0
     */
    public static String replace(String string, String targetStr, String replaceStr, boolean isWholeWord, boolean isIgnoreCase) {
        return replace(string, Arrays.asList(targetStr), replaceStr, isWholeWord, isIgnoreCase);
    }


    /**
     * Find all terms occurring within argument text, and replace with argument value.
     *
     * @param  string
     *         The input string.
     *         
     * @param  targetCollection
     *         The set of strings to match.
     *         
     * @param  replaceStr
     *         The string to replace by.
     *         
     * @param  isWholeWord  
     *         The flag indicating that matching should occur on whole word 
     *         or phrases only.
     *         
     * @param  isIgnoreCase 
     *         The flag indicating that matching should ignore case or not.
     *         
     * @return  The modified string or null if not matched.
     * 
     * @since   1.0
     */
    public static String replace(String string, Collection<String> targetCollection, final String replaceStr, boolean isWholeWord, boolean isIgnoreCase) {
        Matcher matcher = getMatcher(string, targetCollection, isWholeWord, isIgnoreCase);
        return matcher.replaceAll(replaceStr);
    }
    
    /**
     * Build a {@code Matcher} for matching a list of string.
     * 
     * <p>This method will iterate each string in the collection and 
     * concatenate the regular expression for matching.
     *
     * @param  string
     *         The input string.
     *         
     * @param  targetCollection
     *         The set of strings to match.
     *         
     * @param  isWholeWord
     *         The flag indicating that matching should occur on whole word 
     *         or phrases only.
     *         
     * @param  isIgnoreCase
     *         The flag indicating that matching should ignore case or not.
     * 
     * @return  The new {@code Matcher} object.
     * 
     * @since   1.0
     */
    public static Matcher getMatcher(String string, Collection<String> targetCollection, boolean isWholeWord, boolean isIgnoreCase) {
    	Collection<String> quoted = targetCollection.stream().map(s -> Pattern.quote(s)).collect(Collectors.toList());
    	String regex = join("|", quoted);
    	return getMatcher(string, regex, isWholeWord, isIgnoreCase);
    }
   
    /**
     * Build a {@code Matcher} based on a regular expression
     *
     * @param  string
     *         The input string.
     * 
     * @param  regex
     *         The regular expression.
     * 
     * @param  isWholeWord
     *         The flag indicating that matching should occur on whole word 
     *         or phrases only.
     * 
     * @param  isIgnoreCase
     *         The flag indicating that matching should ignore case or not.
     * 
     * @return  The new {@code Matcher} object.
     * 
     * @since   1.0
     */
    public static Matcher getMatcher(String string, String regex, boolean isWholeWord, boolean isIgnoreCase) {
    	regex = "(" + regex + ")";

    	if(isWholeWord) {
    		regex = "\\b" + regex + "\\b";
    	}

    	Pattern pattern = (isIgnoreCase) ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex) ;
    	return pattern.matcher(string);
    }
}
