package personal.wuyi.jibernate.io.persist.core;


import java.time.ZonedDateTime;


/**
 * Versioned
 *
 * Defines interface for version controlled data. Versioned objects have a 
 * "revision" number indicating their sequential historical ordering. The most 
 * recent revision is designated as the "master".
 */
public interface Versioned extends Persisted {
    /**
     * Get the sequential historical revision order
     *
     * @return
     */
    Integer getRevision();

    /**
     * Set the sequential historical revision order
     *
     * @param revision
     */
    void setRevision(Integer revision);

    /**
     * Get the revision date time
     *
     * @return
     */
    ZonedDateTime getRevisionDate();

    /**
     * Set the revision date time
     *
     * @param revisionDate
     */
    void setRevisionDate(ZonedDateTime revisionDate);


    /**
     * Check the current revision is the head revision or not
     *
     * @return
     */
    Boolean getHead();


    /**
     * Set the current revision as the head revision.
     *
     * @param head
     */
    void setHead(Boolean head);
}
