package personal.wuyi.jibernate.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtil {
	/**
     * Format collection to string using default Object.toString() behavior.
     *
     * @param delimiter
     * @param collection
     * @param formatter
     * @param <T>
     * @return
     */
    public static <T> String join( String delimiter, Collection<T> collection ) {

        return join( delimiter, collection, (n) -> n.toString() );
    }


    /**
     * Format collection to string
     *
     * @param delimiter
     * @param collection
     * @param formatter
     * @param <T>
     * @return
     */
    public static <T> String join( String delimiter, Collection<T> collection, Function<T,String> formatter ) {

        String joined = collection.stream()
                .map( formatter )
                .collect( Collectors.joining( delimiter ));

        return joined;
    }
    
    /**
     * Prepend and append the argument "wrapper" value on either side of the argument string.
     *
     * @param s
     * @param wrapper
     * @return
     */
    public static String wrap( String s, String wrapper ) {

        if( s == null ) {
            s = "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append( wrapper ).append( s ).append( wrapper );

        return sb.toString();
    }
    
    /**
     * Find subtext occurring within argument text, and replace with argument value.
     *
     * @param text       the text input.
     * @param find       the subtext to match
     * @param replace    the value that will replace matched sub-text ($1 may be used to decorate matched value, e.g. "prefix-$1-postfix")
     * @param wholeWord  flag indicating that matching should occur on whole word phrases only
     * @param ignoreCase flag indicating that matching should ignore case
     * @return The modified string or null if not matched.
     */
    public static String replace( String text, String find, String replace, boolean wholeWord, boolean ignoreCase ) {

        return replace( text, Arrays.asList( find ), replace, wholeWord, ignoreCase );
    }


    /**
     * Find all terms occurring within argument text, and replace with argument value.
     *
     * @param text       the text input.
     * @param find       a set of sub-text that replacement will match any
     * @param replace    the value that will replace matched sub-text ($1 may be used to decorate matched value, e.g. "prefix-$1-postfix")
     * @param wholeWord  flag indicating that matching should occur on whole word phrases only
     * @param ignoreCase flag indicating that matching should ignore case
     * @return The modified string or null if not matched.
     */
    public static String replace( String text, Collection<String> find, final String replace, boolean wholeWord, boolean ignoreCase ) {

        Matcher matcher = getMatcher( text, find, wholeWord, ignoreCase );
        String replaced = matcher.replaceAll( replace );

        return replaced;

    }
    
    /**
    *
    * @param text
    * @param find
    * @param wholeWord
    * @param ignoreCase
    * @return
    */
   public static Matcher getMatcher( String text, Collection<String> find, boolean wholeWord, boolean ignoreCase ) {

       // iterate over terms and escape any special regex characters
       Collection<String> quoted = find.stream().map( s -> Pattern.quote( s ) ).collect( Collectors.toList() );

       String regex = join( "|", quoted );

       return getMatcher( text, regex, wholeWord, ignoreCase );
   }
   
   /**
   *
   * @param text
   * @param find
   * @param wholeWord
   * @param ignoreCase
   * @return
   */
  public static Matcher getMatcher( String text, String regex, boolean wholeWord, boolean ignoreCase ) {

      regex = "(" + regex + ")";  // define grouping

      // match word boundaries
      if( wholeWord ) {
          regex = "\\b" + regex + "\\b";
      }

      Pattern pattern = ( ignoreCase) ? Pattern.compile( regex, Pattern.CASE_INSENSITIVE ) : Pattern.compile( regex ) ;
      Matcher matcher = pattern.matcher( text );

      return matcher;
  }
}
