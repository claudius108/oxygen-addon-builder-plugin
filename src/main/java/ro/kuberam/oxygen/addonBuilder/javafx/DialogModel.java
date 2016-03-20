package ro.kuberam.oxygen.addonBuilder.javafx;

import java.io.Serializable;

import ro.kuberam.oxygen.addonBuilder.javafx.bridges.BaseBridge;

public class DialogModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4532913769719180776L;
	public String id;
	public String type;
	public String title;
	public int width;
	public int height;
	public String resize;
	public String[] margin;
	public String dataSrc;
	public String jsObjectName;
	public BaseBridge javafxBridge;
	final public String content;

	public DialogModel(String id, String type, String title, int width, int height, String resize,
			String[] margin, String dataSrc, String jsObjectName, String content) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.width = width;
		this.height = height;
		this.resize = resize;
		this.margin = margin;
		this.dataSrc = dataSrc;
		this.jsObjectName = jsObjectName;
		this.content = content;
	}

	public DialogModel(String id, String type, String title, int width, int height, String resize,
			String[] margin, String dataSrc, String jsObjectName, BaseBridge javafxBridge, String content) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.width = width;
		this.height = height;
		this.resize = resize;
		this.margin = margin;
		this.dataSrc = dataSrc;
		this.jsObjectName = jsObjectName;
		this.javafxBridge = javafxBridge;
		this.content = content;
	}

	public void setJavafxBridge(BaseBridge javafxBridge) {
		this.javafxBridge = javafxBridge;
	}

	public String toString() {
		String marginAsString = "";
		for (int i = 0; i < margin.length; i++) {
			marginAsString += margin[i] + " ";
		}
		String javafxbridgeClassName = (javafxBridge != null) ? javafxBridge.getClass().getCanonicalName() : "";

		return "Dialog: id = '" + id + "', type = '" + type + "', title = '" + title + "', width = "
				+ width + ", height = " + height + ", resize = " + resize + ", margin = "
				+ marginAsString.trim() + ", dataSrc = " + dataSrc + ", jsObjectName = " + jsObjectName
				+ ", javafxBridge = " + javafxbridgeClassName + ", content = "
				+ content;

	}
}
