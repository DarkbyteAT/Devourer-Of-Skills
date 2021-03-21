package net.atlne.dos.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.google.gson.reflect.TypeToken;

import net.atlne.dos.Core;
import net.atlne.dos.scenes.Scene;
import net.atlne.dos.utils.maths.HAMaths.ProjectionUtils;

public class InputHelper implements Disposable {
	
	/**Stores the max duration a double press or click can be delayed by.*/
	public static final long DOUBLE_DELAY = 100;
	
	private LinkedHashMap<String, Integer> binds;
	private HashMap<String, Integer> stringKeys;
	private HashMap<Integer, String> keyStrings;
	private int[] clickDuration;
	private int[] keyPressDuration;
	private long[] lastClickTime;
	private long[] lastPressTime;
	private boolean[] doubleClicked;
	private boolean[] doublePressed;
	
	/**Stores the currently bound scene for input polling.*/
	private Scene inputScene;
	
	public InputHelper() {
		if(!Gdx.files.local("config/binds.json").exists()) {
			if(!Gdx.files.local("config").exists()) Gdx.files.local("config").mkdirs();
			Gdx.files.local("config/binds.json")
				.writeString(Gdx.files.internal("net/atlne/dos/input/default-binds.json").readString(), false);
		}
		
		binds = Core.json.get("config/binds.json", new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());
		stringKeys = new HashMap<String, Integer>();
		keyStrings = new HashMap<Integer, String>();
		clickDuration = new int[16];
		keyPressDuration = new int[256];
		lastClickTime = new long[16];
		lastPressTime = new long[256];
		doubleClicked = new boolean[16];
		doublePressed = new boolean[256];
		
		for(int key = 0; key < 256; key++) {
			String value = Keys.toString(key);
			/**Checks if the character has a defined name.*/
			if(value != null && !value.equals("?")) {
				stringKeys.put(value, key);
				keyStrings.put(key, value);
			}
		}
	}
	
	/**Updates the stored binds in the file.*/
	@Override
	public void dispose() {
		if(!Gdx.files.local("config").exists())
			Gdx.files.local("config").mkdirs();
		Core.json.set("config/binds.json", binds);
	}
	
	public void update() {
		/**Tests mouse clicks for mouse buttons.*/
		for(int button = 0; button < 16; button++) {
			if(Gdx.input.isButtonPressed(button)) {
				clickDuration[button]++;
				
				/**Checks if button just clicked.*/
				if(clickDuration[button] == 1) {
					/**Gets current time in milliseconds.*/
					long now = System.currentTimeMillis();
					/**Checks if difference between last click times is less than the delay in milliseconds.
					 * If so, registers a double click.*/
					doubleClicked[button] = now - lastClickTime[button] <= DOUBLE_DELAY;
					lastClickTime[button] = now;
				} else {
					doubleClicked[button] = false;
				}
			} else {
				clickDuration[button] = 0;
			}
		}
		
		/**Tests key presses for all keys to check for.*/
		for(int key = 0; key < keyPressDuration.length; key++) {
			if(Gdx.input.isKeyPressed(key)) {
				keyPressDuration[key]++;
				
				/**Checks if key just pressed.*/
				if(keyPressDuration[key] == 1) {
					/**Gets current time in milliseconds.*/
					long now = System.currentTimeMillis();
					/**Checks if difference between last press times is less than the delay in milliseconds.
					 * If so, registers a double press.*/
					doublePressed[key] = now - lastPressTime[key] <= DOUBLE_DELAY;
					lastPressTime[key] = now;
				} else {
					doublePressed[key] = false;
				}
			} else {
				keyPressDuration[key] = 0;
			}
		}
	}
	
	/**Unbinds the input scene.*/
	public void resetInputScene() {
		inputScene = null;
	}
	
	/**Returns true if the bound scene is the input processor, false otherwise.*/
	public boolean shouldPoll() {
		return inputScene == null || inputScene == Gdx.input.getInputProcessor();
	}
	
	/**Returns true if a given bind is a key-bind, false if it is a mouse bind, or throws an exception if it doesn't exist.
	 * 
	 * @throws IllegalArgumentException Bind not found.*/
	public boolean isBindKey(String bind) {
		if(binds.containsKey(bind)) {
			return shouldPoll() && binds.get(bind) >= 0;
		} else throw new IllegalArgumentException("Bind \"" + bind + "\" was not found!");
	}
	
	/**Returns true if a given bind is pressed, or throws an exception if the bind doesn't exist.
	 * 
	 * @throws IllegalArgumentException Bind not found.*/
	public boolean bindPressed(String bind) {
		if(binds.containsKey(bind)) {
			int bindCode = binds.get(bind);
			return shouldPoll() && (bindCode < 0 ? clicked(-(bindCode + 1)) : keyPressed(bindCode));
		} else throw new IllegalArgumentException("Bind \"" + bind + "\" was not found!");
	}
	
	/**Returns true if a given bind was just pressed, or throws an exception if the bind doesn't exist.
	 * 
	 * @throws IllegalArgumentException Bind not found.*/
	public boolean bindJustPressed(String bind) {
		if(binds.containsKey(bind)) {
			int bindCode = binds.get(bind);
			return shouldPoll() && (bindCode < 0 ? justClicked(-(bindCode + 1)) : keyJustPressed(bindCode));
		} else throw new IllegalArgumentException("Bind \"" + bind + "\" was not found!");
	}
	
	/**Returns true if a given bind was double pressed, or throws an exception if the bind doesn't exist.
	 * 
	 * @throws IllegalArgumentException Bind not found.*/
	public boolean bindDoublePressed(String bind) {
		if(binds.containsKey(bind)) {
			int bindCode = binds.get(bind);
			return shouldPoll() && (bindCode < 0 ? doubleClicked(-(bindCode + 1)) : keyDoublePressed(bindCode));
		} else throw new IllegalArgumentException("Bind \"" + bind + "\" was not found!");
	}
	
	public boolean clicked(int button) {
		return shouldPoll() && clickDuration[button] > 0;
	}

	public boolean justClicked(int button) {
		return shouldPoll() && clickDuration[button] == 1;
	}
	public boolean doubleClicked(int button) {
		return shouldPoll() && doubleClicked[button];
	}
	
	public boolean keyPressed(int key) {
		return shouldPoll() && keyPressDuration[key] > 0;
	}
	
	public boolean keyJustPressed(int key) {
		return shouldPoll() && keyPressDuration[key] == 1;
	}
	
	public boolean keyDoublePressed(int key) {
		return shouldPoll() && doublePressed[key];
	}
	
	public int getKeyCode(String key) {
		return stringKeys.get(key);
	}
	
	public String getKeyName(int keyCode) {
		return keyStrings.get(keyCode);
	}
	
	public int getMouseX() {
		return Gdx.input.getX();
	}
	
	public int getMouseY() {
		return Gdx.graphics.getHeight() - Gdx.input.getY();
	}
	
	public Vector2 getMouseCoords() {
		return new Vector2(getMouseX(), getMouseY());
	}
	
	/**Un-projects the mouse coordinates to the world.*/
	public Vector2 getMouseCoords(Camera camera) {
		return ProjectionUtils.unproject(new Vector2(getMouseX(), Gdx.input.getY()), camera);
	}
	
	/**Gets a list of all key-codes of the keys pressed just this frame.*/
	public ArrayList<Integer> getKeysJustPressed() {
		ArrayList<Integer> keysPressed = new ArrayList<Integer>();
		for(String key : stringKeys.keySet())
			if(keyJustPressed(stringKeys.get(key)))
				keysPressed.add(stringKeys.get(key));
		return keysPressed;
	}
	
	public LinkedHashMap<String, Integer> getBinds() {
		return binds;
	}
	
	public int[] getClickDuration() {
		return clickDuration;
	}
	
	public int[] getKeyPressDuration() {
		return keyPressDuration;
	}
	
	/**Binds the given scene as the input scene.*/
	public void setInputScene(Scene inputScene) {
		this.inputScene = inputScene;
	}
}