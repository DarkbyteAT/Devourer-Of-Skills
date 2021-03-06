package net.atlne.dos.scenes.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.google.gson.reflect.TypeToken;

import net.atlne.dos.Core;
import net.atlne.dos.game.components.input.KeyboardControllerComponent;
import net.atlne.dos.game.entities.Direction;
import net.atlne.dos.game.entities.Entity;
import net.atlne.dos.game.maps.rooms.Room;
import net.atlne.dos.graphics.GraphicsManager;
import net.atlne.dos.graphics.textures.animation.spritesheet.StateAnimatedSprite;
import net.atlne.dos.physics.PhysicsObject;
import net.atlne.dos.physics.PhysicsObjectType;
import net.atlne.dos.scenes.Scene;
import net.atlne.dos.scenes.game.pause.PauseScene;
import net.atlne.dos.utils.Pair;
import net.atlne.dos.utils.physics.HAPhysics;

public class GameScene extends Scene {
	
	/**Stores the test room.*/
	private Room room;
	/**Stores the test physics object.*/
	private PhysicsObject[] obj;
	/**Stores the pause screen buffer.*/
	private TextureRegion pauseBuffer;
	
	public GameScene() {
		super(true);
		Gdx.graphics.setCursor(Core.getGraphics().getTextures().getCursor("assets/textures/cursors/crosshair.png", 16, 16));
		
		this.room = new Room("start-town");
		this.obj = new PhysicsObject[10];
		
		for(int i = 0; i < this.obj.length; i++) {
			this.obj[i] = new PhysicsObject(room.getWorld(), PhysicsObjectType.PLAYER, new Vector2(1, 1), 70);
			this.obj[i].createBody(room.getWorld());
			this.obj[i].getFixtureDef().shape = HAPhysics.createRectangleShape(0.265f, 0.375f);
			this.obj[i].createFixture();
			this.obj[i].getBody().setLinearDamping(10f);
			this.obj[i].getBody().setAngularDamping(0f);
			this.obj[i].getBody().setFixedRotation(true);
			this.obj[i].setPosition(new Vector2(9, 22));
			StateAnimatedSprite objSprite = new StateAnimatedSprite("idle-right",
					Core.getGraphics().getTextures().getSpriteSheets().get("assets/textures/characters/human/sheet.cfg",
							Core.getJson().get("test/replacement.json", new TypeToken<HashMap<String, String>>(){}.getType())));
			objSprite.setSize(GraphicsManager.PPM * 1.5f, GraphicsManager.PPM * (4f/3f));
			objSprite.setPosition(-GraphicsManager.PPM / 1.33f, (-GraphicsManager.PPM / 2) + 4);
			
			room.getEntities().put("test-" + i, new Entity("test-" + i, "idle", this.obj[i], Direction.RIGHT,
					new Pair<String, Sprite>("body", objSprite)));
			room.getEntities().get("test-" + i).getComponents()
				.add(new KeyboardControllerComponent(room.getEntities().get("test-" + i), 1000));
		}
		
		Box2DDebugRenderer.setAxis(new Vector2(GraphicsManager.PPM, GraphicsManager.PPM));
	}
	
	@Override
	public void dispose() {
		super.dispose();
		room.dispose();
		if(pauseBuffer != null)
			pauseBuffer.getTexture().dispose();
	}
	
	@Override
	public void draw() {
		super.draw();
		
		 if(Core.getScenes().getSceneStack().peek() == this) {
			room.getCameraPos().set(room.getEntities().get("test-0").getBody().getPosition());
			batch.begin();
			room.draw(batch);
			batch.end();
			
			if(Core.getInput().keyJustPressed(Keys.ESCAPE)) {
				if(pauseBuffer != null)
					pauseBuffer.getTexture().dispose();
				pauseBuffer = Core.getGraphics().screen();
			}
		} else {
			batch.begin();
			batch.draw(pauseBuffer, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.end();
		}
		
	}
	
	@Override
	public void act() {
		super.act();
		
		if(Core.getInput().keyJustPressed(Keys.ESCAPE)) {
			Core.getScenes().pushScene(new PauseScene());
		} else if(Core.getScenes().getSceneStack().peek() == this) {
			room.update();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		room.resize(width, height);
	}
	
	public Room getRoom() {
		return room;
	}
}