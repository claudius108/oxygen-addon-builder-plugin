package ro.kuberam.oxygen.addonBuilder.mutations;

import java.io.Serializable;
import java.util.Map;

public class ObserverConnection implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1544837091243537643L;
	private String observerHandler;
	private String nodeSelector;
	private Map<String, Object> options;
	
	public ObserverConnection(String observerHandler, String nodeSelector, Map<String, Object> options) {
		this.setObserverHandler(observerHandler);
		this.setNodeSelector(nodeSelector);
		this.setOptions(options);
	}

	public String getObserverHandler() {
		return observerHandler;
	}

	public void setObserverHandler(String observerHandler) {
		this.observerHandler = observerHandler;
	}

	public String getNodeSelector() {
		return nodeSelector;
	}

	public void setNodeSelector(String nodeSelector) {
		this.nodeSelector = nodeSelector;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}
	
	public String toString() {
		return observerHandler;
	}

}
