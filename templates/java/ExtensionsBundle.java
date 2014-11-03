package ${frameworkId};

public class ExtensionsBundle extends ro.sync.ecss.extensions.api.ExtensionsBundle {

	@Override
	public String getDescription() {
		return "${frameworkId} extensions bundle implementation";
	}

	@Override
	public String getDocumentTypeID() {
		return "${frameworkId}.document.type";
	}

}

