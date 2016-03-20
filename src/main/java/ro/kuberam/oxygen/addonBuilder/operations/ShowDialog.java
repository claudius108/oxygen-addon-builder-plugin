package ro.kuberam.oxygen.addonBuilder.operations;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import ro.kuberam.oxygen.addonBuilder.javafx.DialogModel;
import ro.kuberam.oxygen.addonBuilder.javafx.JavaFXDialog;
import ro.kuberam.oxygen.addonBuilder.javafx.bridges.BaseBridge;
import ro.kuberam.oxygen.addonBuilder.mutations.ProcessMutationRecord;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

public class ShowDialog implements AuthorOperation {

	/**
	 * Logger for logging.
	 */
	private static final Logger logger = Logger.getLogger(ShowDialog.class.getName());

	/**
	 * The dialog id. The value is <code>string</code> .
	 */
	protected String ARGUMENT_DIALOG_ID = "dialogId";

	/**
	 * The arguments of the operation.
	 */
	private ArgumentDescriptor[] arguments = null;

	/**
	 * Constructor.
	 */
	public ShowDialog() {
		arguments = new ArgumentDescriptor[1];

		ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(ARGUMENT_DIALOG_ID,
				ArgumentDescriptor.TYPE_STRING, "The identificator of dialog.");
		arguments[0] = argumentDescriptor;
	}

	@Override
	public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws IllegalArgumentException,
			AuthorOperationException {
		Object dialogIdObj = args.getArgumentValue(ARGUMENT_DIALOG_ID);
		String dialogId = (String) dialogIdObj;
		logger.debug("dialogId = " + dialogId);

		JFrame parentFrame = (JFrame) authorAccess.getWorkspaceAccess().getParentFrame();
		DialogModel dialog = ProcessMutationRecord.dialogs.get(dialogId);
		logger.debug("dialog = " + dialog.toString());
		dialog.setJavafxBridge(new BaseBridge());
		
		new JavaFXDialog(parentFrame, dialog);
		// listAllComponentsIn(parentFrame.getContentPane());
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
	 */
	@Override
	public ArgumentDescriptor[] getArguments() {
		return arguments;
	}

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Show a dialog window.";
	}

	public void listAllComponentsIn(Container parent) {
		for (Component c : parent.getComponents()) {

			if (c instanceof JTextArea) {
				JTextArea ta = (JTextArea) c;
				System.out.println("\n" + ta.getName());
				System.out.println(ta.getText());
			}

			if (c instanceof Container) {
				listAllComponentsIn((Container) c);
			}

		}
	}

}
