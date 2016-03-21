package ro.kuberam.oxygen.addonBuilder.oxyFormControlDescriptors;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class OxyEditorDescriptor {

	private String type = "";
	private String edit;
	private String rendererClassName;
	private String swingEditorClassName;
	private String actionID;
	private String action;
	private String actionContext;
	private String transparent;
	private String visible;
	private String disabled;
	private String showIcon;
	private String editable;
	private String values;
	private String labels;
	private String columns;
	private String rows;
	private String contentType;
	private String selectionMode;
	private String color;
	private Map<String, String> customProperties = new LinkedHashMap<String, String>();

	public OxyEditorDescriptor() {
	}

	public String getType() {
		return (type != "") ? "type, " + type + ", " : "";
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEdit() {
		return (edit != null ? "edit, \"" + edit + "\", " : "");
	}

	public String getEditValue() {
		return (edit != null ? edit : "");
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getRendererClassName() {
		return (rendererClassName != null ? "rendererClassName, \"" + rendererClassName + "\", " : "");
	}

	public void setRendererClassName(String rendererClassName) {
		this.rendererClassName = rendererClassName;
	}

	public String getSwingEditorClassName() {
		return (swingEditorClassName != null ? "swingEditorClassName, \"" + swingEditorClassName + "\", "
				: "");
	}

	public void setSwingEditorClassName(String swingEditorClassName) {
		this.swingEditorClassName = swingEditorClassName;
	}

	public String getCustomProperty(String propertyName) {
		return customProperties.get(propertyName);
	}

	public void setCustomProperty(String propertyName, String propertyValue) {
		Field property = null;

		try {
			property = this.getClass().getDeclaredField(propertyName);
			property.set(this, propertyValue);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			customProperties.put(propertyName, propertyValue);
		}
	}

	public String getCustomProperties() {
		String separator = ", ";
		String keyValueSeparator = ", ";

		StringBuffer buffer = new StringBuffer();

		Iterator<Entry<String, String>> entryIterator = customProperties.entrySet().iterator();

		while (entryIterator.hasNext()) {

			Entry<String, String> entry = entryIterator.next();

			buffer.append(entry.getKey());
			buffer.append(keyValueSeparator);
			buffer.append("\"" + entry.getValue() + "\"");
			buffer.append(separator);
		}

		return buffer.toString();
	}

	public String getActionID() {
		return (actionID != null ? "actionID, \"" + actionID + "\", " : "");
	}

	public void setActionID(String actionID) {
		this.actionID = actionID;
	}
	
	public String getAction() {
		action = (action != null ? "action, \"" + action + "\", " : "");
		action = (action.contains("\"@")) ? action.replace("\"", "") : action;
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}	

	public String getActionContext() {
		return (actionContext != null ? "actionContext, " + actionContext + ", " : "");
	}

	public void setActionContext(String actionContext) {
		this.actionContext = actionContext;
	}

	public String getTransparent() {
		return (transparent != null ? "transparent, " + transparent + ", " : "");
	}

	public void setTransparent(String transparent) {
		this.transparent = transparent;
	}

	public String getVisible() {
		return (visible != null ? "visible, " + visible + ", " : "");
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getDisabled() {
		return (disabled != null ? "disabled, " + disabled + ", " : "");
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getShowIcon() {
		return (showIcon != null ? "showIcon, " + showIcon + ", " : "");
	}

	public void setShowIcon(String showIcon) {
		this.showIcon = showIcon;
	}

	public String getEditable() {
		return (editable != null ? "editable, " + editable + ", " : "");
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public String getValues() {
		values = (values != null ? "values, " + _formatOxyXpathExpression(values) + ", " : "");
		values = (values.contains("\"@")) ? values.replace("\"", "") : values; 
		
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public String getLabels() {
		return (labels != null ? "labels, " + _formatOxyXpathExpression(labels) + ", " : "");
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getColumns() {
		return (columns != null ? "columns, " + columns + ", " : "");
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getRows() {
		return (rows != null ? "rows, " + rows + ", " : "");
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getContentType() {
		return (contentType != null ? "contentType, " + contentType + ", " : "");
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getSelectionMode() {
		return (selectionMode != null ? "selectionMode, \"" + selectionMode + "\", " : "");
	}

	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}

	private String _formatOxyXpathExpression(String oxyXpathExpression) {
		if (!oxyXpathExpression.startsWith("oxy_xpath")) {
			return "\"" + oxyXpathExpression + "\"";
		}
		return oxyXpathExpression;
	}

	public String getColor() {
		return (color != null ? "color, " + color + ", " : "");
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String toString() {
		return ("oxy_editor(" + getType() + getRendererClassName() + getSwingEditorClassName() + getEdit()
				+ getActionID() + getAction() + getActionContext() + getCustomProperties() + getTransparent()
				+ getVisible() + getDisabled() + getShowIcon() + getValues() + getLabels() + getColumns()
				+ getRows() + getContentType() + getSelectionMode() + getEditable() + getColor()).replaceAll(", $", "")
				+ ")";
	}
}