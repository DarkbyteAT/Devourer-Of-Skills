package net.atlne.dos.physics;

import java.util.Vector;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import net.atlne.dos.graphics.GraphicsManager;

public class TexturedPhysicsObject extends PhysicsObject {
	
	/**Stores the Sprite to attach to the physics object.*/
	protected Vector<Sprite> sprites;

	public TexturedPhysicsObject(PhysicsObjectType type, Vector2 position, float mass, TextureRegion... textures) {
		super(type, position, mass);
		this.sprites = new Vector<Sprite>();
		for(int i = 0; i < textures.length; i++)
			if(textures[i] != null)
				this.sprites.add(new Sprite(textures[i]));
	}

	public TexturedPhysicsObject(World world, PhysicsObjectType type, Vector2 position, float mass, TextureRegion... textures) {
		super(world, type, position, mass);
		this.sprites = new Vector<Sprite>();
		for(int i = 0; i < textures.length; i++)
			if(textures[i] != null)
				this.sprites.add(new Sprite(textures[i]));
	}

	/**Updates the sprite position and rotation to match the physics object.
	 * Scales the position by the PPM constant for rendering.*/
	@Override
	public void update() {
		super.update();
		Vector2 newPos = position.cpy().scl(GraphicsManager.PPM);
		
		for(Sprite sprite : sprites) {
			if(sprite != null) {
				sprite.setOrigin(0, 0);
				sprite.setRotation(angle * 180 / MathUtils.PI);
				sprite.setPosition(newPos.x, newPos.y);
			}
		}
	}
	
	/**Draws the sprite.*/
	public void draw(SpriteBatch batch) {
		for(Sprite sprite : sprites)
			if(sprite != null)
				sprite.draw(batch);
	}
	
	/**Gets the sprite for the physics object.*/
	public Vector<Sprite> getSprites() {
		return sprites;
	}
	
	/**Gets the sprite size of the sprites.*/
	public Vector2 getSpriteSize() {
		return sprites.size() > 0 ? new Vector2(sprites.get(0).getWidth(), sprites.get(0).getHeight()) : new Vector2();
	}
	
	/**Sets the size of all of the sprites.*/
	public void setSpriteSize(float width, float height) {
		for(Sprite sprite : sprites)
			sprite.setSize(width, height);
	}
}