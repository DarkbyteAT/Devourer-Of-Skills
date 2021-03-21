package net.atlne.dos.graphics.textures.animation.spritesheet;

import java.util.Arrays;

import net.atlne.dos.graphics.textures.animation.AnimatedSprite;
import net.atlne.dos.graphics.textures.animation.Animation;
import net.atlne.dos.utils.collections.HACollections;

public class StateAnimatedSprite extends AnimatedSprite {
	
	/**Stores the SpriteSheet to get the animation state from.*/
	protected SpriteSheet spriteSheet;
	/**Stores the current state of the sprite.*/
	protected String state;

	public StateAnimatedSprite(String defaultState, SpriteSheet spriteSheet) {
		super(spriteSheet.getAnimations().get(defaultState).getDuration());
		this.spriteSheet = spriteSheet;
		this.state = defaultState;
		updateTextures();
	}
	
	public void updateTextures() {
		Animation animation = spriteSheet.getAnimations().get(state);
		textures = Arrays.copyOfRange(spriteSheet.getSprites()[animation.getRow()], animation.getStart(), animation.getEnd() + 1);
		textures = animation.isReversed() ? HACollections.reverse(textures) : textures;
	}
	
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}
	
	public String getState() {
		return state;
	}
	
	public void setSpriteSheet(SpriteSheet spriteSheet) {
		if(!this.spriteSheet.equals(spriteSheet)) {
			this.spriteSheet = spriteSheet;
			time = 0;
			updateTextures();
		}
	}
	
	public void setState(String state) {
		if(!this.state.equals(state)) {
			this.state = state;
			time = 0;
			updateTextures();
		}
	}
}