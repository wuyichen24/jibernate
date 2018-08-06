package personal.wuyi.jibernate.transformer;

/**
 * Transformer.java
 *
 * <p>Defines an interface for converting a source object into another. 
 * Transformations may be chained together via decorator pattern, consequently 
 * there is an implicit assertion that transformed objects are similar (i.e. 
 * the implement a common interface)
 * 
 * @author  Wuyi Chen
 *
 * @param <X>
 * @param <Y>
 */
@FunctionalInterface
public interface Transformer<X,Y> {
	/**
	 * Transforms source object to alternate, equivalent representation
	 * 
	 * <p>Method of transformation may be a straightforward copy between 
	 * equivalent objects, or may result in a more complex change such as 
	 * composition or filtering.
	 *
	 * @param  source
	 * 	       The source of transformation (NON-NULL)
	 *
	 * @param  context
	 *         Optional context
	 *
	 * @return
	 * @throws ServerException
	 * @throws ValidationException
	 */
	Y transform(X source, Object... context);
}
