package personal.wuyi.jibernate.transformer;

/**
 * Transformer interface for converting a source object into another. 
 * 
 * <p>Transformations may be chained together via decorator pattern, 
 * consequently there is an implicit assertion that transformed objects 
 * are similar (i.e. the implement a common interface)
 *
 * @param  <X>
 *         The class / type of the original object (before transforming).
 *         
 * @param  <Y>
 *         The class / type of the transformed object (After transforming).
 * 
 * @author  Wuyi Chen
 * @date    10/10/2018
 * @version 1.0
 * @since   1.0
 */
@FunctionalInterface
public interface Transformer<X,Y> {
	/**
	 * Transforms source object to alternate, equivalent representation.
	 * 
	 * <p>Method of transformation may be a straightforward copy between 
	 * equivalent objects, or may result in a more complex change such as 
	 * composition or filtering.
	 *
	 * @param  source
	 * 	       The object needs to be transformed.
	 *
	 * @param  context
	 *         Optional contexts.
	 *
	 * @return  The transformed object.
	 * 
     * @since   1.0
	 */
	Y transform(X source, Object... context);
}
