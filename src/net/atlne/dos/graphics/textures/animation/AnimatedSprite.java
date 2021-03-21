package net.atlne.dos.graphics.textures.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedSprite extends Sprite {
	
	/**Stores the time taken to complete one cycle of the animation.*/
	protected float animationTime;
	/**Stores the textures for the animated sprite.*/
	protected TextureRegion[] textures;
	/**Stores the time for the animation.*/
	protected float time;
	
	public AnimatedSprite(float animationTime, TextureRegion... textures) {
		this.animationTime = animationTime;
		this.textures = textures;
	}
	
	/**Draws the sprite's current animation and updates the animation time.*/
	@Override
	public void draw(Batch batch) {
		time = (time + Gdx.graphics.getDeltaTime()) % animationTime;
		this.setRegion(textures[(int) ((time / animationTime) * textures.length)]);
		super.draw(batch);
	}

	public float getAnimationTime() {
		return animationTime;
	}

	public float getTime() {
		return time;
	}

	public void setAnimationTime(float animationTime) {
		this.animationTime = animationTime;
	}

	public void setTime(float time) {
		this.time = time;
	}
}