package net.atlne.dos.game.entities;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.atlne.dos.game.components.Component;
import net.atlne.dos.graphics.GraphicsManager;
import net.atlne.dos.graphics.textures.animation.AnimatedSprite;
import net.atlne.dos.graphics.textures.animation.spritesheet.StateAnimatedSprite;
import net.atlne.dos.physics.PhysicsObject;
import net.atlne.dos.utils.Pair;

public class Entity {
	
	/**Stores the StringBuilder for outputting debug text.*/
	protected static final StringBuilder DEBUG_BUILDER = new StringBuilder();
	
	/**Stores the ID of the entity.*/
	protected String ID;
	/**Stores the state of the entity.*/
	protected String state;
	/**Stores the internal timer for the entity.*/
	protected float stateTime;
	/**Stores the physics body associated with the entity.*/
	protected PhysicsObject body;
	/**Stores the body's direction.*/
	protected Direction direction;
	/**Stores the sprites for the body in rendering order.*/
	protected LinkedHashMap<String, Sprite> sprites = new LinkedHashMap<>();
	/**Stores the components for the entity.*/
	protected Vector<Component> components = new Vector<>();

	@SafeVarargs
	public Entity(String ID, String state, PhysicsObject body, Direction direction, Pair<String, Sprite>... sprites) {
		this.ID = ID;
		this.state = state;
		this.body = body;
		this.body.getFixture().setUserData(this);
		this.direction = direction;
		this.sprites = new LinkedHashMap<>();
		for(Pair<String, Sprite> pair : sprites)
			this.sprites.put(pair.getKey(), pair.getValue());
	}
	
	/**Outputs debug information for the object.*/
	@Override
	public String toString() {
		DEBUG_BUILDER.setLength(0);
		DEBUG_BUILDER.append(body.toString());
		DEBUG_BUILDER.append("\nID: " + ID + "\n")
						.append("State: " + state + "\n")
						.append("Direction: " + direction + "\n")
						.append("Sprite Names: " + sprites.keySet());
		return DEBUG_BUILDER.toString();
	}
	
	/**Draws the sprites for the entity, the position of the sprites acting as a relative origin for drawing.*/
	public void draw(SpriteBatch batch) {
		for(Sprite sprite : sprites.values()) {
			float originX = sprite.getX(), originY = sprite.getY();
			sprite.setPosition(originX + (GraphicsManager.PPM * body.getPosition().x),
							   originY + (GraphicsManager.PPM * body.getPosition().y));
			sprite.draw(batch);
			sprite.setPosition(originX, originY);
		}
	}
	
	/**Updates the entity's internal clock and the body, as well as the sprites.*/
	public void update() {
		this.stateTime += Gdx.graphics.getDeltaTime();
		components.forEach(Component::run);
		syncSprites();
		body.update();
	}
	
	/**Gets the drawing position of the entity, by default, set to the y-position.*/
	public float drawOrder() {
		return body.getPosition().y;
	}
	
	/**Syncs the time of the sprites for the entity.
	 * Also syncs states for state-animated sprites.*/
	protected void syncSprites() {
		float maxTime = ((AnimatedSprite) sprites.values().stream()
							.filter(e -> e instanceof AnimatedSprite)
							.max(Comparator.comparing(a -> ((AnimatedSprite) a).getTime()))
							.get())
						.getTime();
		
		for(Sprite sprite : sprites.values()) {
			if(sprite instanceof AnimatedSprite) {
				((AnimatedSprite) sprite).setTime(maxTime);
				if(sprite instanceof StateAnimatedSprite)
					((StateAnimatedSprite) sprite).setState(state + "-" + direction.toString().toLowerCase());
			}
		}
	}

	public String getID() {
		return ID;
	}
	
	public String getState() {
		return state;
	}
	
	public float getStateTime() {
		return stateTime;
	}
	
	public PhysicsObject getBody() {
		return body;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public LinkedHashMap<String, Sprite> getSprites() {
		return sprites;
	}
	
	public Vector<Component> getComponents() {
		return components;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public void setSprite(String name, Sprite sprite) {
		sprites.put(name, sprite);
	}
}