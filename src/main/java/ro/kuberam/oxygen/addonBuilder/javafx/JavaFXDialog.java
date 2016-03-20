package ro.kuberam.oxygen.addonBuilder.javafx;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ro.kuberam.oxygen.addonBuilder.javafx.bridges.BaseBridge;
import ro.kuberam.oxygen.addonBuilder.parser.Style;

public class JavaFXDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -679885858688312152L;

	public JavaFXDialog(final JFrame parentFrame, final DialogModel dialogModel) {
		super(parentFrame, dialogModel.title, (dialogModel.type.equals("modal")) ? true : false);

		final JavaFXDialog dialogWindow = this;

		boolean setResizeable = (dialogModel.resize.equals("both")) ? true : false;
		int dialogWindowWidth = dialogModel.width;
		int dialogWindowHeight = dialogModel.height;
		String[] dialogWindowMargin = dialogModel.margin;
		int marginTopProperty = 0;
		int marginRightProperty = 0;
		int marginBottomProperty = 0;
		int marginLeftProperty = 0;

		float horizontalAlignmentFactor = 1;
		float verticalAlignmentFactor = 1;

		switch (dialogWindowMargin.length) {
		case 1:
			String marginProperty = dialogWindowMargin[0];
			if (!marginProperty.equals("auto")) {
				marginTopProperty = marginRightProperty = marginBottomProperty = marginLeftProperty = Style
						.extractDigits(marginProperty);
			} else {
				horizontalAlignmentFactor = 2;
				verticalAlignmentFactor = 2;
			}
			break;
		case 4:
			if (dialogWindowMargin[0].equals("auto") && dialogWindowMargin[2].equals("auto")) {
				verticalAlignmentFactor = 2;
			}
			if (dialogWindowMargin[1].equals("auto") && dialogWindowMargin[3].equals("auto")) {
				horizontalAlignmentFactor = 2;
			}
			if (!dialogWindowMargin[0].equals("auto")) {
				marginTopProperty = Style.extractDigits(dialogWindowMargin[0]);
			}
			if (!dialogWindowMargin[1].equals("auto")) {
				marginRightProperty = Style.extractDigits(dialogWindowMargin[1]);
			}
			if (!dialogWindowMargin[2].equals("auto")) {
				marginBottomProperty = Style.extractDigits(dialogWindowMargin[2]);
			}
			if (!dialogWindowMargin[3].equals("auto")) {
				marginLeftProperty = Style.extractDigits(dialogWindowMargin[3]);
			}
			break;
		}

		BaseBridge bridge = dialogModel.javafxBridge;
		bridge.setDialogWindow(dialogWindow);
		JavaFXPanel panel = new JavaFXPanel(dialogModel.content, dialogModel.dataSrc, bridge,
				dialogModel.jsObjectName);

		int parentFrameWidth = parentFrame.getWidth();
		int parentFrameHeight = parentFrame.getHeight();

		dialogWindowWidth = (dialogWindowWidth > parentFrameWidth) ? (parentFrameWidth - 70)
				: dialogWindowWidth;
		dialogWindowHeight = (dialogWindowHeight > parentFrameHeight) ? (parentFrameHeight - 70)
				: dialogWindowHeight;
		this.setSize(dialogWindowWidth, dialogWindowHeight);

		int left = (int) Math
				.round((parentFrameWidth - dialogWindowWidth - marginLeftProperty - marginRightProperty)
						/ horizontalAlignmentFactor);
		int top = (int) Math
				.round((parentFrameHeight - dialogWindowHeight - marginBottomProperty - marginTopProperty)
						/ verticalAlignmentFactor);
		this.setLocation(left, top);

		this.setResizable(setResizeable);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getContentPane().add(panel);
		this.setVisible(true);
	}

	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DialogModel dialogModel = new DialogModel("rich-textarea-dialog", "modal", "Dialog Title",
						500, 900, "both", new String[] { "auto", "0", "auto", "auto" }, JavaFXDialog.class
								.getResource("manage-framework-dialog.html").toExternalForm(),
						"OxygenAddonBuilder", new BaseBridge(), "");
				JFrame parentFrame = new JFrame();
				Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
				int parentFrameWidth = screenDimension.width;
				int parentFrameHeight = screenDimension.height;

				parentFrame.setPreferredSize(new Dimension(parentFrameWidth, parentFrameHeight));
				parentFrame.pack();

				new JavaFXDialog(parentFrame, dialogModel);
			}
		});
	}
}
