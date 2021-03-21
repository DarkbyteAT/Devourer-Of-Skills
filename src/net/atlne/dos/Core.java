package net.atlne.dos;

import java.util.logging.LogManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

import net.atlne.dos.audio.AudioHelper;
import net.atlne.dos.debug.DebugOverlay;
import net.atlne.dos.graphics.GraphicsManager;
import net.atlne.dos.input.InputHelper;
import net.atlne.dos.maps.MapLoader;
import net.atlne.dos.physics.PhysicsShapeLoader;
import net.atlne.dos.scenes.LoadingScene;
import net.atlne.dos.scenes.SceneManager;
import net.atlne.dos.utils.files.JsonManager;

public class Core extends Game {
	
	/**Entry function for game.*/
	public static void main(String[] args) {
		/**Stores the attributes of the created window within its fields.*/
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		/**Sets the desired attributes of the created window.*/
		cfg.width = 1440;
		cfg.height = 900;
		cfg.title = "Devourer of Skills";
		cfg.resizable = true;
		cfg.foregroundFPS = 0;
		cfg.backgroundFPS = 0;
		cfg.vSyncEnabled = false;

		/**Creates a new window using these defined parameters as well as a new instance of this class.*/
		new LwjglApplication(new Core(), cfg);
	}
	
	/**Stores the JSON manager for the game.*/
	public static JsonManager json;
	/**Stores the graphics manager for the game.*/
	public static GraphicsManager graphics;
	/**Stores the audio helper for the game.*/
	public static AudioHelper audio;
	/**Stores the input helper for the game.*/
	public static InputHelper input;
	/**Stores the physics shape loader for the game.*/
	public static PhysicsShapeLoader physicsShapes;
	/**Stores the map loader for the game.*/
	public static MapLoader maps;
	/**Stores the scene manager for the game.*/
	public static SceneManager scenes;
	/**Stores whether the debug overlay is active or not.*/
	public static DebugOverlay debug;

	/**Run when window is opened, before render()*/
	@Override
	public void create() {
		/**Disables logging to speed up the game.*/
		LogManager.getLogManager().reset();
		/**Allows for native code to be loaded.*/
		GdxNativesLoader.load();
		/**Loads Box2D in to the game.*/
		Box2D.init();
		
		json = new JsonManager();
		graphics = new GraphicsManager();
		audio = new AudioHelper();
		input = new InputHelper();
		physicsShapes = new PhysicsShapeLoader();
		scenes = new SceneManager();
		maps = new MapLoader();
		debug = new DebugOverlay();
		
		scenes.pushScene(new LoadingScene());
	}
	
	/**Run when window is closed.*/
	@Override
	public void dispose() {
		super.dispose();
		json.dispose();
		graphics.dispose();
		audio.dispose();
		input.dispose();
		physicsShapes.dispose();
		scenes.dispose();
		maps.dispose();
		debug.dispose();
	}
	
	/**Main render loop for game, super-function renders current scene.*/
	@Override
	public void render() {
		try {
			debug.act();
			graphics.update();
			input.update();
			scenes.update();
			debug.draw();
			
			/**Takes a screenshot if the bind is pressed.*/
			if(input.bindJustPressed("take-screenshot"))
				graphics.screenshot();
		} catch(Exception e) {
			showErrorDialog(e.getMessage(), e);
		}
	}
	
	/**Run when window is resized.*/
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		scenes.resize(width, height);
		debug.resize(width, height);
	}
	
	/**Shows an error dialog box to the user, shows a Scene2D dialog box.*/
	public static void showErrorDialog(String message, Throwable exception) {
		System.err.println(message);
		exception.printStackTrace();
		Dialogs.showErrorDialog(scenes.getErrorScene(), "[RED]" + message, exception);
	}
}