package net.atlne.dos.debug;

import java.util.LinkedHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.Tooltip;

import net.atlne.dos.Core;
import net.atlne.dos.game.maps.rooms.Room;
import net.atlne.dos.graphics.GraphicsManager;
import net.atlne.dos.scenes.Scene;
import net.atlne.dos.scenes.game.GameScene;
import net.atlne.dos.utils.debug.HADebug;
import net.atlne.dos.utils.text.HAStrings;

public class DebugOverlay extends Scene {
	
	/**Stores the interval in seconds at which the RAM usage and allocation are updated.*/
	public static final float RAM_CHECK_INTERVAL = 1;
	
	/**Stores the font used for the debug menu.*/
	private BitmapFont font;
	/**Stores a list of all of the text to render in the debug overlay.*/
	private LinkedHashMap<String, String> debugPanes;
	/**Stores whether the debug pane is active or not.*/
	private boolean active;
	
	/**Stores the StringBuilder for rendering text.*/
	private StringBuilder toRender;
	/**Stores the RAM usage string; recalculated lazily as it is a heavy operation.*/
	private String ramUsage;
	/**Stores the RAM allocation string; recalculated lazily as it is a heavy operation.*/
	private String ramAllocated;
	/**Stores the timer in seconds for updating the RAM usage and allocation strings.*/
	private float ramCheckTimer;
	
	/**Stores the Tooltip for displaying entity information.*/
	private Tooltip entityInfo;
	/**Flag determining if entity info should be displayed or not.*/
	private boolean displayEntityInfo;

	public DebugOverlay() {
		super(true);
		this.batch = new SpriteBatch();
		this.font = new BitmapFont();
		this.font.getData().markupEnabled = true;
		this.debugPanes = new LinkedHashMap<String, String>();
		this.toRender = new StringBuilder();
		this.entityInfo = new Tooltip();
		addActor(entityInfo);
	}
	
	/**Disposes of any assets.*/
	public void dispose() {
		super.dispose();
		font.dispose();
		toRender.setLength(0);
	}
	
	/**Renders the debug overlay to the screen.*/
	public void draw() {
		if(active) {
			toRender.setLength(0);
			for(String pane : debugPanes.keySet())
				toRender.append(pane + ": " + debugPanes.get(pane) + "\n");
			batch.begin();
			font.draw(batch, toRender.toString(), 4, getHeight() - 4);
			batch.end();
			debugPanes.clear();
			entityInfo.setVisible(displayEntityInfo);
			super.draw();
		}
	}
	
	/**Updates the base panes for the debug overlay.*/
	@Override
	public void act() {
		super.act();
		
		boolean oldActive = active;
		active = Core.getInput().bindJustPressed("open-debug-menu") ? !active : active;
		if(active != oldActive)
			recalculateRAMUsage();
		
		if(active) {
			if(ramCheckTimer >= RAM_CHECK_INTERVAL) {
				recalculateRAMUsage();
			} else ramCheckTimer += Gdx.graphics.getDeltaTime();
			
			debugPanes.put("FPS", Integer.toString(Gdx.graphics.getFramesPerSecond()));
			debugPanes.put("RAM Usage", ramUsage);
			debugPanes.put("RAM Allocated", ramAllocated);
			
			displayEntityInfo = false;
			GameScene gameScene;
			
			/**Adds a tool-tip to about entity being inspected describing its data, if game in GameScene.*/
			if(Core.getInput().bindPressed("inspect") &&
					(gameScene = (GameScene) Core.getScenes().peekScene(GameScene.class)) != null) {
				Room room = gameScene.getRoom();
				Vector2 mouseCoords = Core.getInput().getMouseCoords(room.getCamera()).scl(1 / GraphicsManager.PPM);
				Array<Fixture> fixtures = new Array<Fixture>(room.getWorld().getFixtureCount());
				room.getWorld().getFixtures(fixtures);
				
				for(Fixture fixture : fixtures) {
					if(fixture.testPoint(mouseCoords)) {
						entityInfo.setText("Entity Information:\n" + fixture.getUserData());
						displayEntityInfo = true;
						break;
					}
				}
			}
		}
	}
	
	/**Recalculates RAM statistics if needed.*/
	public void recalculateRAMUsage() {
		ramUsage = HAStrings.getColourPrefix(Color.GREEN.cpy().lerp(Color.RED, HADebug.getUsedMemoryProportion())) +
				Long.toString(HADebug.getUsedMemory() / 1000000L) + "[]MB";
		ramAllocated = Long.toString(HADebug.getAllocatedMemory() / 1000000L) + "MB";
		ramCheckTimer = 0;
	}
	
	public LinkedHashMap<String, String> getDebugPanes() {
		return debugPanes;
	}
	
	public boolean isActive() {
		return active;
	}
}