package personal.wuyi.jibernate.transformer;

/**
 * Abstract transformer
 * 
 * <p>By default, most Transformers are expected to implements the decorator 
 * pattern, but are not strictly required to do so.
 * 
 * @author  Wuyi Chen
 * 
 * @param <X>
 * @param <Y>
 */
public abstract class AbstractTransformer<X,Y> implements Transformer<X,Y> {
	private Transformer<X,Y> transformer = null;
	
	public AbstractTransformer() {}

	public AbstractTransformer(Transformer<X,Y> transformer) {
		this.transformer = transformer;
	}

	@Override
	public Y transform(X source, Object... context)  {
		@SuppressWarnings("unchecked")
		Y transformed = (Y) source;

		if(transformer != null) {
			transformed = transformer.transform(source, context);
		}

		return transformed;
	}
}
