package personal.wuyi.jibernate.io.persist.entity;

import personal.wuyi.jibernate.io.persist.core.ManagedEntity;
import personal.wuyi.jibernate.io.persist.core.Uri;

public abstract class AbstractAutoStockEntity implements ManagedEntity {
	private static final long serialVersionUID = 1L;

	abstract public Long getId();

    @Override
    public Uri getUri() {
        return new Uri(this.getClass(), this.getId());
    }

    @Override
    public boolean isPersisted() {
        return ( getId() != null );
    }
}
