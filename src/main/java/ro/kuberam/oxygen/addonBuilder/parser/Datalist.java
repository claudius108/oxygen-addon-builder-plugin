package ro.kuberam.oxygen.addonBuilder.parser;

public class Datalist {
	private String id;
	private String labels;
	private String values;

	public Datalist(String id, String labels, String values) {
		this.setId(id);
		this.setLabels(labels);
		this.setValues(values);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}
}
