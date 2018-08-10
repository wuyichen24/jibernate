package personal.wuyi.jibernate.entitymanager;

/**
 * The plug-in interface so that you can manually start or stop an event. 
 * 
 * @author  Wuyi Chen
 * @date    08/08/2018
 * @version 1.0
 * @since   1.0
 */
public interface Plugin {
	/**
	 * Start an event.
	 * 
     * @since   1.0
	 */
	public void start();
	
	/**
	 * Stop an event.
	 * 
     * @since   1.0
	 */
	public void stop();
}
