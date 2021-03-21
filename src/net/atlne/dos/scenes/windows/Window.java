package net.atlne.dos.scenes.windows;

import com.kotcrab.vis.ui.widget.VisWindow;

import net.atlne.dos.Core;

public class Window extends VisWindow {

	/**Stores the core class instance.*/
	protected Core core;
	
	public Window(Core core, String title) {
		super(title);
		this.core = core;
		addCloseButton();
		pack();
	}
	
	public Core getCore() {
		return core;
	}
}