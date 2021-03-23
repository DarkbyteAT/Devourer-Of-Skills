package net.atlne.dos.graphics.textures.animation.spritesheet;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.atlne.dos.Core;
import net.atlne.dos.graphics.textures.animation.AnimatedSprite;
import net.atlne.dos.graphics.textures.animation.Animation;
import net.atlne.dos.utils.collections.HACollections;
import net.atlne.dos.utils.files.FileHandler;
import net.atlne.dos.utils.text.HAStrings;

public class SpriteSheet {

	/**Stores the location of the sprite-sheet texture.*/
	protected String textureLocation;
	/**Stores the sprite-sheet texture.*/
	protected transient Texture texture;
	/**Stores a 2D array of all contained sprites.*/
	protected transient TextureRegion[][] sprites;
	/**Stores the width and height of each sprite within the sheet in pixels.*/
	protected int spriteWidth, spriteHeight;
	/**Stores the animations mapped to their names created from the sheet.*/
	protected HashMap<String, Animation> animations = new HashMap<String, Animation>();
	
	/**Constructor for the sprite-sheet, takes in the texture, width and height of the sprites.*/
	public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight) {
		this.texture = texture;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		/**Splits the texture into the 2D array.*/
		sprites = new TextureRegion(texture).split(spriteWidth, spriteHeight);
	}
	
	/**Constructor for the sprite-sheet, takes in the texture location, width and height of the sprites.*/
	public SpriteSheet(String textureLocation, int spriteWidth, int spriteHeight) {
		this.textureLocation = textureLocation;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		this.texture = Core.getGraphics().getTextures().get(textureLocation);
		/**Splits the texture into the 2D array.*/
		sprites = new TextureRegion(texture).split(spriteWidth, spriteHeight);
	}
	
	/**Constructor for the sprite-sheet, reads in the parameters from the given file.*/
	public SpriteSheet(FileHandler spriteSheetFile) {
		/**Passes the contents of the file to the below constructor.*/
		this(spriteSheetFile.getContents());
	}
	
	/**Constructor for the sprite-sheet, reads in the parameters from the given file.*/
	public SpriteSheet(String spriteSheet) {
		/**First reads in the contents of the file as a string and splits it by lines.*/
		String[] lines = spriteSheet.trim().split("\n");
		/**Gets the texture location from the first line.*/
		this.textureLocation = lines[0].trim();
		/**Loads the texture from the texture location.*/
		this.texture = Core.getGraphics().getTextures().get(textureLocation);
			
		/**Takes the width and height to be stored in the second and third lines respectively.*/
		/**Parses both back into integers.*/
		/**Catches an error where the lines may contain other characters.*/
		try {
			this.spriteWidth = Integer.parseInt(lines[1].trim());
			this.spriteHeight = Integer.parseInt(lines[2].trim());
			/**Gets the list of animations from the animations file.*/
			String[] animations = new FileHandler(lines[3].trim()).getContents().trim().split("\n");
				
			/**Iterates over each line of the animations file and creates animations.*/
			for(int i = 0; i < animations.length; i++) {
				/**Splits the line into its constituent components.*/
				String[] parts = animations[i].split(", ");
				/**The first part is taken as the animation name.*/
				/**The second part is taken as the animation's row in the sheet.*/
				/**The third part is taken as the starting index and the fourth as the ending index.*/
				String animationName = parts[0].trim();
				float duration = Float.parseFloat(parts[1].trim());
				int row = Integer.parseInt(parts[2].trim()),
							start = Integer.parseInt(parts[3].trim()),
							end = Integer.parseInt(parts[4].trim());
				boolean reversed = Boolean.parseBoolean(parts[5].trim());
				/**Adds the animation to the map.*/
				this.animations.put(animationName, new Animation(duration, row, start, end, reversed));
			}
		} catch(NumberFormatException e) {
			Core.showErrorDialog("An error has occurred whilst loading a spritesheet!\nSpritesheet:\n" + spriteSheet, e);
		}
		
		/**Creates the 2D array to store the sprites.*/
		this.sprites = new TextureRegion(texture).split(spriteWidth, spriteHeight);
	}
	
	/**Constructor for the sprite-sheet, reads in the parameters from the given file
	 * Also takes in a HashMap of String-String mappings of replacements for the animation file.*/
	public SpriteSheet(String spriteSheet, HashMap<String, String> replacements) {
		/**Handles any replacements for the sprite-sheet.*/
		spriteSheet = HAStrings.replaceAll(spriteSheet, replacements);
		/**Splits the sprite-sheet string by lines.*/
		String[] lines = spriteSheet.trim().split("\n");
		/**Gets the texture location from the first line.*/
		this.textureLocation = lines[0].trim();
		/**Loads the texture from the texture location.*/
		this.texture = Core.getGraphics().getTextures().get(textureLocation);
			
		/**Takes the width and height to be stored in the second and third lines respectively.*/
		/**Parses both back into integers.*/
		/**Catches an error where the lines may contain other characters.*/
		try {
			this.spriteWidth = Integer.parseInt(lines[1].trim());
			this.spriteHeight = Integer.parseInt(lines[2].trim());
			/**Gets the entire list of animations from the animations file.*/
			String animationSheet = new FileHandler(lines[3].trim()).getContents().trim();
			/**Splits the animation sheet into a list of animation strings.*/
			String[] animations = animationSheet.trim().split("\n");
			
			/**Iterates over each line of the animations file and creates animations.*/
			for(int i = 0; i < animations.length; i++) {
				/**Splits the line into its constituent components.*/
				String[] parts = animations[i].split(", ");
				/**The first part is taken as the animation name.*/
				/**The second part is taken as the animation's row in the sheet.*/
				/**The third part is taken as the starting index and the fourth as the ending index.*/
				String animationName = parts[0].trim();
				float duration = Float.parseFloat(parts[1].trim());
				int row = Integer.parseInt(parts[2].trim()),
							start = Integer.parseInt(parts[3].trim()),
							end = Integer.parseInt(parts[4].trim());
				boolean reversed = Boolean.parseBoolean(parts[5].trim());
				/**Adds the animation to the map.*/
				this.animations.put(animationName, new Animation(duration, row, start, end, reversed));
			}
		} catch(NumberFormatException e) {
			Core.showErrorDialog("An error has occurred whilst loading a spritesheet!\nSpritesheet:\n" + spriteSheet, e);
		}
		
		/**Creates the 2D array to store the sprites.*/
		this.sprites = new TextureRegion(texture).split(spriteWidth, spriteHeight);
	}
	
	/**Creates an AnimatedSprite for a specific animation from the spritesheet.*/
	public AnimatedSprite getAnimatedSprite(String animationName) {
		Animation animation = animations.get(animationName);
		TextureRegion[] textures = HACollections.subset(animation.getStart(), animation.getEnd(), sprites[animation.getRow()]);
		return new AnimatedSprite(animation.getDuration(), animation.isReversed() ? HACollections.reverse(textures) : textures);
	}
	
	/**Gets the frame an animation is currently on using the current time.*/
	public TextureRegion getFrame(String animationName, float stateTime) {
		/**Returns the index in the row equal to the elapsed frames plus the start frame.*/
		/**This gets the frame that the animation is currently on.*/
		return sprites[animations.get(animationName).getRow()][animations.get(animationName).getCurrentFrame(stateTime)];
	}
	public Texture getTexture() {
		return texture;
	}

	public TextureRegion[][] getSprites() {
		return sprites;
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public HashMap<String, Animation> getAnimations() {
		return animations;
	}
}