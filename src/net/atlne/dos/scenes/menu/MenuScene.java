package net.atlne.dos.scenes.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import net.atlne.dos.Core;
import net.atlne.dos.graphics.ui.SceneTitle;
import net.atlne.dos.scenes.Scene;
import net.atlne.dos.scenes.game.GameScene;
import net.atlne.dos.scenes.menu.windows.CharacterSelectionWindow;
import net.atlne.dos.scenes.windows.settings.SettingsWindow;

public class MenuScene extends Scene {
	
	/**Stores the title screen text.*/
	public static final String TITLE = "Devourer of Skills";
	
	/**Stores the menu scene title.*/
	private SceneTitle title;
	/**Stores the table to organise the buttons within.*/
	private VisTable table;
	/**Stores the start button.*/
	private VisTextButton start;
	/**Stores the settings button.*/
	private VisTextButton settings;
	/**Stores the exit button.*/
	private VisTextButton exit;
	
	/**Stores the character selection window.*/
	private CharacterSelectionWindow characterSelectionWindow;
	/**Stores the settings window.*/
	private SettingsWindow settingsWindow;

	public MenuScene() {
		super(false);
		
		title = new SceneTitle(TITLE);
		table = new VisTable();
		characterSelectionWindow = new CharacterSelectionWindow();
		settingsWindow = new SettingsWindow();
		characterSelectionWindow.centerWindow();
		settingsWindow.centerWindow();
		
		start = new VisTextButton("Start", new ChangeListener() {
			@Override
			public void changed(ChangeEvent e, Actor actor) {
				//addActor(characterSelectionWindow);
				Core.getScenes().popScene();
				Core.getScenes().pushScene(new GameScene());
				dispose();
			}
		});
		
		settings = new VisTextButton("Settings", new ChangeListener() {
			@Override
			public void changed(ChangeEvent e, Actor actor) {
				addActor(settingsWindow);
			}
		});
		
		exit = new VisTextButton("Exit", new ChangeListener() {
			@Override
			public void changed(ChangeEvent e, Actor actor) {
				Gdx.app.exit();
			}
		});
		
		start.setTransform(true);
		settings.setTransform(true);
		exit.setTransform(true);
		
		table.add(start).center().padBottom(16);
		table.row();
		table.add(settings).center().padTop(16).padBottom(16);
		table.row();
		table.add(exit).center().padTop(16);
		table.row();
		table.setTransform(true);
		table.setFillParent(true);
		
		addActor(title);
		addActor(table);
	} 
	
	/**Draws the title text at the top of the screen.*/
	@Override
	public void draw() {
		Core.getAudio().playMusic("menu");
		super.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		title.resize(width, height);
	}
}