package ro.kuberam.oxygen.addonBuilder.oxyFormControlDescriptors;

import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.oxygen.addonBuilder.oxyFormControlDescriptors.OxyEditorDescriptor;

public class OxyFormControlDescriptorsTest {

	@Test
	public void testInitializeOxyEditorDescriptor() {
		OxyEditorDescriptor oxyEditorDescriptor = new OxyEditorDescriptor();
		oxyEditorDescriptor
				.setRendererClassName("ro.kuberam.oxygen.addonBuilder.templates.java.button.ButtonEditor");
		oxyEditorDescriptor
				.setSwingEditorClassName("ro.kuberam.oxygen.addonBuilder.templates.java.button.ButtonEditor");
		oxyEditorDescriptor.setActionID("placeNameWrapping");
		oxyEditorDescriptor.setActionContext("caret");
		oxyEditorDescriptor.setTransparent("true");

		Assert.assertEquals(
				"oxy_editor(rendererClassName, \"ro.kuberam.oxygen.addonBuilder.templates.java.button.ButtonEditor\", swingEditorClassName, \"ro.kuberam.oxygen.addonBuilder.templates.java.button.ButtonEditor\", actionID, \"placeNameWrapping\", actionContext, caret, transparent, true)",
				oxyEditorDescriptor.toString());

	}

	@Test
	public void testSetPredefinedParameter() {
		OxyEditorDescriptor oxyEditorDescriptor = new OxyEditorDescriptor();
		oxyEditorDescriptor.setCustomProperty("actionID", "actionID value");

		Assert.assertEquals("actionID, \"actionID value\", ", oxyEditorDescriptor.getActionID());
	}

	@Test
	public void testCheckWrongParameterExists() {
		OxyEditorDescriptor oxyEditorDescriptor = new OxyEditorDescriptor();
		String propertyName = "nonExistingProperty";
		oxyEditorDescriptor.setCustomProperty(propertyName, "value");

		Assert.assertEquals("value", oxyEditorDescriptor.getCustomProperty(propertyName));
	}

	@Test
	public void testFormatOxyXpathExpression() {
		String values = "@usage-options";
		
		OxyEditorDescriptor oxyEditorDescriptor = new OxyEditorDescriptor();
		oxyEditorDescriptor.setValues(values);
		
		Assert.assertEquals("values, @usage-options, ", oxyEditorDescriptor.getValues());
	}
	
}
