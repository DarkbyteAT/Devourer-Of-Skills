package net.atlne.dos.scenes.windows;

import com.kotcrab.vis.ui.widget.VisWindow;

public class Window extends VisWindow {
	public Window(String title) {
		super(title);
		addCloseButton();
		pack();
	}
}