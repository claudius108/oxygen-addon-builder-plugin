package ro.kuberam.oxygen.addonBuilder.mutations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.kuberam.oxygen.addonBuilder.mutations.ObserverConnection;


@SuppressWarnings("unchecked")
public class MutationObservers {

	public static Map<String, String[]> observers;
	private static Map<String, ObserverConnection> connectObserverActions = new HashMap<String, ObserverConnection>();
	private static Map<String, String> nodeSelectors = new HashMap<String, String>();

	static {
		ObjectInputStream observersOis = null;
		ObjectInputStream connectObserverActionsOis = null;
		ObjectInputStream nodeSelectorsOis = null;

		try {
			observersOis = new ObjectInputStream(
					MutationObservers.class.getResourceAsStream("observers.ser"));
			connectObserverActionsOis = new ObjectInputStream(
					MutationObservers.class.getResourceAsStream("connectObserverActions.ser"));
			nodeSelectorsOis = new ObjectInputStream(
					MutationObservers.class.getResourceAsStream("nodeSelectors.ser"));

			observers = (Map<String, String[]>) observersOis.readObject();
			connectObserverActions = (Map<String, ObserverConnection>) connectObserverActionsOis
					.readObject();
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

	public static void main(String args[]) {
		String observedElement = "//idno";

		ObserverConnection observerConnection = connectObserverActions.get(observedElement);
		String observerHandler = observerConnection.getObserverHandler();
		Map<String, Object> options = observerConnection.getOptions();
//		System.out.println("observerHandler: " + observerHandler);
//		System.out.println("options: " + observerConnection.getOptions());
		List<String> attributeFilter = (List<String>) options.get("attributeFilter");
		System.out.println("size: " + attributeFilter.size());
		System.out.println("item 0: " + attributeFilter.get(0));
		

		String[] actionHandlers = (String[]) observers.get(observerHandler);
		for (String actionHandler : actionHandlers) {
//			System.out.println("actionHandler: " + actionHandler);
		}

//		System.out.println("nodeSelectors: " + nodeSelectors);
	}
}
