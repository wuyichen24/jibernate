package personal.wuyi.jibernate.util;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import personal.wuyi.jibernate.entity.Student;

/**
 * The test class for {@code StringUtil}.
 * 
 * @author  Wuyi Chen
 * @date    10/12/2018
 * @version 1.1
 * @since   1.0
 */
public class StringUtilTest {
	@Test
	public void joinTest() {
		Assert.assertEquals("John,Sanberg:98.3|Mary,Rualsae:97.3|Deea,Trealse:95.3", StringUtil.join("|", 
				Arrays.asList(
						new Student("John", "Sanberg", 98.3),
						new Student("Mary", "Rualsae", 97.3),
						new Student("Deea", "Trealse", 95.3))));
		
		Assert.assertEquals("John,Sanberg:98.3Mary,Rualsae:97.3Deea,Trealse:95.3", StringUtil.join("", 
				Arrays.asList(
						new Student("John", "Sanberg", 98.3),
						new Student("Mary", "Rualsae", 97.3),
						new Student("Deea", "Trealse", 95.3))));
	}
	
	@Test
	public void replaceTest() {
		Assert.assertEquals("The slow brown fox jumps over the lazy dog",   StringUtil.replace("The quick brown fox jumps over the lazy dog", "quick", "slow", true, true));
		Assert.assertEquals("The slow brown fox jumps over the lazy dog",   StringUtil.replace("The quick brown fox jumps over the lazy dog", "quick", "slow", true, false));
		Assert.assertEquals("The quick brown fox jumps over the lazy dog",  StringUtil.replace("The quick brown fox jumps over the lazy dog", "QUICK", "slow", true, false));  // case sensitive, should not replace
		Assert.assertEquals("The slow brown fox jumps over the lazy dog",   StringUtil.replace("The quick brown fox jumps over the lazy dog", "QUICK", "slow", true, true));    // not case sensitive, should replace
		Assert.assertEquals("The quick brown fox jumps over the lazy dog",  StringUtil.replace("The quick brown fox jumps over the lazy dog", "qui", "slow", true, true));     // whole word, should not replace
		Assert.assertEquals("The slowck brown fox jumps over the lazy dog", StringUtil.replace("The quick brown fox jumps over the lazy dog", "qui", "slow", false, true));    // not whole word , should replace
		
		Assert.assertEquals("a quick brown fox jumps over a lazy dog",      StringUtil.replace("The quick brown fox jumps over the lazy dog", Arrays.asList("The", "the"), "a", true, true));
		Assert.assertEquals("a quick brown fox jumps over the lazy dog",    StringUtil.replace("The quick brown fox jumps over the lazy dog", Arrays.asList("The", "DOG"), "a", true, false));
		Assert.assertEquals("The quick brown cat jumps over the lazy cat",  StringUtil.replace("The quick brown fox jumps over the lazy dog", Arrays.asList("fox", "dog"), "cat", true, false));
	}
}
