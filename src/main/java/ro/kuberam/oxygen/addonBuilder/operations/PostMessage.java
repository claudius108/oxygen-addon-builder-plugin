package ro.kuberam.oxygen.addonBuilder.operations;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.Main;

public class PostMessage {
	public static void main(String[] args) {
		Context cx = ContextFactory.getGlobal().enterContext();
		cx.setOptimizationLevel(-1);
		cx.setLanguageVersion(Context.VERSION_1_5);
		Global global = Main.getGlobal();
		global.init(cx);
		Main.processSource(cx, "post-message.js");
	}
}
