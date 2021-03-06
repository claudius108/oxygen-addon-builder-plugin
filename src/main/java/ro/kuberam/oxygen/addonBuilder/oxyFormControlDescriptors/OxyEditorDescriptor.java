package ro.kuberam.oxygen.addonBuilder.oxyFormControlDescriptors;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OxyEditorDescriptor {

	private String type = "";
	private String edit;
	private String rendererClassName;
	private String swingEditorClassName;
	private String actionID;
	private String action = "";
	private String actionContext;
	private String transparent;
	private String visible;
	private String disabled;
	private String showIcon;
	private String editable;
	private String labels = "";
	private String values = "";
	private String columns;
	private String rows;
	private String contentType;
	private String selectionMode;
	private String color;
	private String uncheckedValues;
	private String hasMultipleValues;
	private String href;
	private String width;
	private String height;
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
		return (swingEditorClassName != null ? "swingEditorClassName, \"" + swingEditorClassName + "\", " : "");
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
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
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
			String entryKey = entry.getKey();
			String entryValue = "\"" + entry.getValue() + "\"";

			buffer.append(entryKey);
			buffer.append(keyValueSeparator);
			buffer.append(entryValue);
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
		action = (action != "" ? "action, \"" + action + "\", " : "");
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
		values = (values != "" ? "values, " + _formatOxyXpathExpression(values) + ", " : "");
		values = (values.contains("\"@")) ? values.replace("\"", "") : values;

		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public String getLabels() {
		labels = (labels != "" ? "labels, " + _formatOxyXpathExpression(labels) + ", " : "");
		labels = (labels.contains("\"@")) ? labels.replace("\"", "") : labels;

		return labels;
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

	public String getUncheckedValues() {
		return (uncheckedValues != null ? "uncheckedValues, " + _formatOxyXpathExpression(uncheckedValues) + ", " : "");
	}

	public void setUncheckedValues(String uncheckedValues) {
		this.uncheckedValues = uncheckedValues;
	}

	public String getHasMultipleValues() {
		return (hasMultipleValues != null ? "hasMultipleValues, " + hasMultipleValues + ", " : "");
	}

	public void setHasMultipleValues(String hasMultipleValues) {
		this.hasMultipleValues = hasMultipleValues;
	}

	public String getHref() {
		return (href != null ? "href, '" + href + "', " : "");
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getWidth() {
		return (width != null ? "width, " + width + "px, " : "");
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return (height != null ? "height, " + height + "px, " : "");
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void processAndSetLabelsAndValues(Node node) {
		NodeList nodeChildNodes = node.getChildNodes();
		String labels = "";
		String values = "";

		if (nodeChildNodes.getLength() != 0) {
			labels = IntStream.range(0, nodeChildNodes.getLength()).mapToObj(nodeChildNodes::item)
					.filter(Element.class::isInstance).map(Element.class::cast)
					.map(el -> el.getAttributes().getNamedItem("label").getNodeValue())
					.collect(Collectors.joining(","));
			values = IntStream.range(0, nodeChildNodes.getLength()).mapToObj(nodeChildNodes::item)
					.filter(Element.class::isInstance).map(Element.class::cast)
					.map(el -> el.getAttributes().getNamedItem("value").getNodeValue())
					.collect(Collectors.joining(","));
		} else {
			String listId = Optional.ofNullable(node.getAttributes().getNamedItem("list")).map(Node::getNodeValue)
					.orElse("");
			labels = "@" + listId + "-labels";
			values = "@" + listId + "-values";
		}

		this.setValues(values);
		this.setLabels(labels);
	}

	public String toString() {
		return ("oxy_editor(" + getType() + getRendererClassName() + getSwingEditorClassName() + getEdit()
				+ getActionID() + getAction() + getActionContext() + getTransparent() + getVisible() + getDisabled()
				+ getShowIcon() + getValues() + getLabels() + getColumns() + getRows() + getContentType()
				+ getSelectionMode() + getEditable() + getColor() + getUncheckedValues() + getHasMultipleValues()
				+ getHref() + getWidth() + getHeight() + getCustomProperties()).replaceAll(", $", "") + ")";
	}

	public String shortDescription() {
		return ("oxy_" + type + "(" + getRendererClassName() + getSwingEditorClassName() + getEdit() + getActionID()
				+ getAction() + getActionContext() + getTransparent() + getVisible() + getDisabled() + getShowIcon()
				+ getValues() + getLabels() + getColumns() + getRows() + getContentType() + getSelectionMode()
				+ getEditable() + getColor() + getUncheckedValues() + getHasMultipleValues() + getHref() + getWidth()
				+ getHeight() + getCustomProperties()).replaceAll(", $", "") + ")";
	}
}
