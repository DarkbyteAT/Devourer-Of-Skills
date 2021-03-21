package net.atlne.dos.scenes.windows.settings;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import net.atlne.dos.Core;
import net.atlne.dos.scenes.windows.Window;
import net.atlne.dos.scenes.windows.settings.tabs.AudioSettingsTab;
import net.atlne.dos.scenes.windows.settings.tabs.ControlSettingsTab;

public class SettingsWindow extends Window {
	
	/**Stores the table to arrange the elements in the window.*/
	private VisTable table;
	/**Stores the table for displaying the current tab.*/
	private VisTable contentTable;
	/**Stores the tab pane for displaying the current tab.*/
	private TabbedPane tabPane;
	
	public SettingsWindow(Core core) {
		super(core, "Settings");
		setResizable(true);
		
		table = new VisTable();
		contentTable = new VisTable();
		tabPane = new TabbedPane();
		tabPane.addListener(new TabbedPaneAdapter() {
			@Override
			public void switchedTab(Tab tab) {
				float oldX = getX(), oldY = getY(),
						oldWidth = getWidth(), oldHeight = getHeight();
				contentTable.clearChildren();
				contentTable.add(tab.getContentTable()).expand();
				table.pack();
				pack();
				setPosition(oldX - getWidth() + oldWidth, oldY - getHeight() + oldHeight);
			}
		});
		
		tabPane.add(new ControlSettingsTab(core));
		tabPane.add(new AudioSettingsTab(core));
		table.add(tabPane.getTable()).top().left().padBottom(8).fillX().row();
		table.add(contentTable);
		add(table);
		table.pack();
		pack();
		//debugAll();
	}
}