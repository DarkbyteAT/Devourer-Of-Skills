package net.atlne.dos.scenes.windows.settings.tabs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

import net.atlne.dos.Core;
import net.atlne.dos.utils.text.HAStrings;

public class ControlSettingsTab extends Tab {
	
	/**Stores the core class instance.*/
	private Core core;
	/**Stores the table for the tab.*/
	private VisTable table;
	/**Stores the table for arranging the elements.*/
	private VisTable contentTable;
	/**Stores the scroll pane for allowing for scrollable tabs.*/
	private VisScrollPane scrollPane;
	/**Stores the secondary table to fit within the scroll pane.*/
	private BindFindTable bindTable;
	/**Stores the labels for each bind.*/
	private VisLabel[] bindLabels;
	/**Stores the buttons for each bind.*/
	private VisTextButton[] bindButtons;
	
	/**Stores the current index of the bind to set on the next key press. <br>
	 * -1 is stored when the bind index is nothing.*/
	private int bindIndex = -1;

	public ControlSettingsTab(Core core) {
		super(false, false);
		this.core = core;
		table = new VisTable();
		contentTable = new VisTable();
		scrollPane = new VisScrollPane(contentTable);
		bindTable = new BindFindTable(this);
		bindLabels = new VisLabel[core.getInput().getBinds().size()];
		bindButtons = new VisTextButton[bindLabels.length];
		
		for(int i = 0; i < bindLabels.length; i++) {
			final int INDEX = i;
			int bind = core.getInput().getBinds().get(core.getInput().getBinds().keySet().toArray()[i]);
			bindLabels[i] = new VisLabel(HAStrings.dashToCapitalisedDisplay(core.getInput().getBinds().keySet().toArray()[i].toString()) + ": ");
			
			/**Determines the bind key from the bind code, if negative, converts to mouse buttons.
			 * Sets the current bind key to this bind button's index.*/
			bindButtons[i] = new VisTextButton(bind < 0 ? "MOUSE" + (-(bind + 1)) : core.getInput().getKeyName(bind),
					new ChangeListener() {
						@Override
						public void changed(ChangeEvent e, Actor actor) {
							bindIndex = INDEX;
						}
					});
			
			bindTable.add(bindLabels[i]).left().padBottom(8);
			bindTable.add(bindButtons[i]).right().padBottom(8).row();
		}
		
		contentTable.add(bindTable);
		table.add(scrollPane);
		contentTable.pack();
		table.pack();
	}

	@Override
	public Table getContentTable() {
		return table;
	}

	@Override
	public String getTabTitle() {
		return "Controls";
	}
	
	public Core getCore() {
		return core;
	}
	
	public VisLabel[] getBindLabels() {
		return bindLabels;
	}

	public VisTextButton[] getBindButtons() {
		return bindButtons;
	}

	public int getBindIndex() {
		return bindIndex;
	}
	
	public void setBindIndex(int bindIndex) {
		this.bindIndex = bindIndex;
	}
}

class BindFindTable extends VisTable {
	
	/**Stores an instance of the control settings tab.*/
	private ControlSettingsTab tab;
	
	public BindFindTable(ControlSettingsTab tab) {
		this.tab = tab;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		/**Checks if there's an active bind selection.*/
		if(tab.getBindIndex() != -1) {
			/**Checks for any pressed keys.*/
			if(tab.getCore().getInput().getKeysJustPressed().size() > 0) {
				tab.getBindButtons()[tab.getBindIndex()].setText(tab.getCore().getInput().getKeyName(tab.getCore().getInput().getKeysJustPressed().get(0)));
				tab.getCore().getInput().getBinds().put(tab.getCore().getInput().getBinds().keySet().toArray()[tab.getBindIndex()].toString(),
						tab.getCore().getInput().getKeysJustPressed().get(0));
				tab.setBindIndex(-1);
			} else {
				/**Checks for any pressed mouse buttons.*/
				for(int i = 0; i < 16; i++) {
					if(tab.getCore().getInput().justClicked(i)) {
						tab.getBindButtons()[tab.getBindIndex()].setText("MOUSE" + i);
						tab.getCore().getInput().getBinds().put(tab.getCore().getInput().getBinds().keySet().toArray()[tab.getBindIndex()].toString(), -(i + 1));
						tab.setBindIndex(-1);
						break;
					}
				}
			}
		}
	}
}