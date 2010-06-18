package org.rcsb.sequence.util;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;



/** A utility class that deals with loading data from properties files.
 * Example usage:
 * <pre>
 		ResourceManager rm = new ResourceManager("sequenceview");
		MIN_DISPLAY_LABEL = Integer.parseInt(rm.getString("MIN_DISPLAY_LABEL"));	
 </pre>
 * 
 * @author Andreas Prlic
 *
 */
public class ResourceManager {

	String bundleName;

	
	/** get a new manager class
	 * 
	 * @param propFileName
	 */
	public ResourceManager(String propFileName) {
		bundleName = propFileName;
	}

	public String getString(String key) {
		ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(bundleName,Locale.getDefault());
		try {			
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			System.err.println(e.getMessage());
			return '!' + key + '!';
		}
	}
}

