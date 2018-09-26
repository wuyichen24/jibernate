package personal.wuyi.jibernate.entitymanager;

import java.util.List;

import personal.wuyi.jibernate.entity.Persisted;
import personal.wuyi.jibernate.entity.Uri;
import personal.wuyi.jibernate.exception.DatabaseOperationException;
import personal.wuyi.jibernate.query.JQuery;

/**
 * The interface to provides some specific data operations without exposing 
 * details of the database.
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.0
 * @since   1.0
 */
public interface Dao extends Plugin {
	/**
	 * Query a single record from database. 
	 * 
	 * <p>For different types of data, it can define their way to identify 
	 * each unique record. Commonly, the combination of the table name and the 
	 * primary key for that table will be the unique identifier. The unique 
	 * identifier is the only way to retrieve a single record from database.
	 * 
	 * @param  uri
	 *         The {@code URI} to identify the single.
	 *         
	 * @return  A single record / object.
	 * 
     * @since   1.0
	 */
    public <T extends Persisted> T read(Uri uri);
    
    /**
     * Query a list of records from database by a certain criteria.
     * 
     * <p>If there is no matched records for a query. This method will return 
     * a empty list.
     * 
     * @param  query
     *         The {@code Query} as criteria to limit the set of results.
     *     
     * @return  A list of the matched records.
     * 
     * @since   1.0
     */
    public <T extends Persisted> List<T> read(JQuery<T> query);
    
    /**
     * Query a list of records from database for only selected field(s).
     * 
     * <p>This method will return {@code List<List<?>>}. The element in the 
     * outer list is by record. The elements in the inner list 
     * ({@code List<?>}) are the values of the selected fields.
     * 
     * <p>For example, if you only select 2 fields and there are only 3 
     * matched records. The result will look like: 
     * <pre>
     * {
     *     {Record1Field1Value, Record1Field2Value},  
     *     {Record2Field1Value, Record2Field2Value}, 
     *     {Record3Field1Value, Record3Field2Value}
     * }
     * <pre>
     * 
     * <p>If there is no matched records for a query. This method will return 
     * a empty list.
     * 
     * @param  query
     *         The {@code Query} as criteria to limit the set of results.
     * 
     * @param  fieldNames
     *         The array of field names in Java class (not the column names in 
     *         database).
     *         
     * @return  The nested {@code List} of {@code List<?>}. The element in the 
     *          outer list is by record. The elements in the inner list 
     *          ({@code List<?>}) are the values of the selected fields.
     *          
     * @since   1.0
     */
    public List<List<?>> read(JQuery<? extends Persisted> query, String... fieldNames);
    
    /**
     * Count the number of matched records for a certain criteria.
     * 
     * @param  query
     *         The {@code Query} as criteria to limit the set of results.
     *         
     * @return  The number of matched records.
     * 
     * @since   1.0
     */
    public <T extends Persisted> long count(JQuery<T> query);
    
    /**
     * Insert a new record or update a existing record to database.
     * 
     * @param  t
     *         The record needs to be inserted or updated.
     *         
     * @throws  DatabaseOperationException
     *          There is an error occurred when writing a record.
     *       
     * @since   1.0
     */
    public <T extends Persisted> void write(T t) throws DatabaseOperationException;
    
    /**
     * Insert a list of new records or update a list of existing records to 
     * database.
     * 
     * @param  tList
     *         The list of records needs to be inserted or updated.
     *         
     * @throws  DatabaseOperationException
     *          There is an error occurred when writing a record.
     *       
     * @since   1.0
     */
    public <T extends Persisted> void write(List<T> tList) throws DatabaseOperationException;
    
    /**
     * Delete an record from database.
     * 
     * <p>You can not remove an record which is not managed by the entity 
     * manager, so this method will check the record is managed or not. 
     * If not, that record will be managed first an then deleted.
     * 
     * @param  t
     *         The record needs to be deleted.
     *         
     * @throws  DatabaseOperationException
     *          There is an error occurred when deleting a record.
     *          
     * @since   1.0
     */
    public <T extends Persisted> void delete(T t) throws DatabaseOperationException;
    
    /**
     * Delete a list of records from database.
     * 
     * <p>You can not remove an record which is not managed by the entity 
     * manager, so this method will check the record is managed or not. 
     * If not, that record will be managed first an then deleted.
     * 
     * @param  tList
     *         The list of the records needs to be deleted.
     *         
     * @throws  DatabaseOperationException
     *          There is an error occurred when deleting a list of records.
     *          
     * @since   1.0
     */
    public <T extends Persisted> void delete(List<T> tList) throws DatabaseOperationException;
}
