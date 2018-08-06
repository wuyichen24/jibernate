package personal.wuyi.jibernate.entity;

import personal.wuyi.jibernate.core.ManagedEntity;
import personal.wuyi.jibernate.core.Uri;

public abstract class AbstractAppEntity implements ManagedEntity {
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
