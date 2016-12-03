package ro.kuberam.oxygen.addonBuilder.mutations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

@SuppressWarnings("unchecked")
public class MutationObservers {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(MutationObservers.class.getName());

	public static Map<String, String[]> observers;
	private static Map<String, ObserverConnection> connectObserverActions = new HashMap<String, ObserverConnection>();
	private static Map<String, String> nodeSelectors = new HashMap<String, String>();

	static {
		ObjectInputStream observersOis = null;
		ObjectInputStream connectObserverActionsOis = null;
		ObjectInputStream nodeSelectorsOis = null;

		try {
			observersOis = new ObjectInputStream(MutationObservers.class.getResourceAsStream("observers.ser"));
			connectObserverActionsOis = new ObjectInputStream(
					MutationObservers.class.getResourceAsStream("connectObserverActions.ser"));
			nodeSelectorsOis = new ObjectInputStream(MutationObservers.class.getResourceAsStream("nodeSelectors.ser"));

			observers = (Map<String, String[]>) observersOis.readObject();
			connectObserverActions = (Map<String, ObserverConnection>) connectObserverActionsOis.readObject();
			logger.debug("connectObserverActions = " + connectObserverActions);

			nodeSelectors = (Map<String, String>) nodeSelectorsOis.readObject();

			observersOis.close();
			connectObserverActionsOis.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
