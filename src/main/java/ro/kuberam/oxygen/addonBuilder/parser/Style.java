package ro.kuberam.oxygen.addonBuilder.parser;

public class Style {

	public int width;
	public int height;
	public String resize = "none";
	public String[] margin = new String[] { "auto" };

	public Style(String elementName) {
		setProperties(elementName);
	}

	public Style(String elementName, String styleString) {

		String[] styleProperties = styleString.split(";");

		setProperties(elementName);

		if (styleProperties != null) {
			for (String styleProperty : styleProperties) {
				if (styleProperty.contains("width:")) {
					this.width = extractDigits(styleProperty);
				}
				if (styleProperty.contains("height:")) {
					this.height = extractDigits(styleProperty);
				}
				if (styleProperty.contains("resize:")) {
					this.resize = styleProperty.substring(styleProperty.indexOf(":") + 1).trim();
				}
				if (styleProperty.contains("margin:")) {
					this.margin = styleProperty.substring(styleProperty.indexOf(":") + 1).trim().split(" ");
				}
			}
		}
	}

	public static int extractDigits(final CharSequence input) {
		final StringBuilder sb = new StringBuilder(input.length());
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (c > 47 && c < 58) {
				sb.append(c);
			}
		}
		return Integer.parseInt(sb.toString());
	}

	private void setProperties(String elementName) {
		switch (elementName) {
		case "tree":
			this.width = 700;
			this.height = 700;
			break;
		case "select":
			this.width = 22;
			this.height = 700;
			break;
		}
	}

}
