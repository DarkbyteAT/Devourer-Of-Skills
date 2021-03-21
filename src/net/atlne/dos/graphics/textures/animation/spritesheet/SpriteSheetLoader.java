package net.atlne.dos.graphics.textures.animation.spritesheet;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import net.atlne.dos.Core;

public class SpriteSheetLoader {
	
	/**Stores an instance of the Core class.*/
	private Core core;
	/**Stores a map of all spritesheet's text representations with their paths as keys.*/
	private ConcurrentHashMap<String, String> spriteSheets = new ConcurrentHashMap<String, String>();
	
	public SpriteSheetLoader(Core core) {
		this.core = core;
	}
	
	/**Creates a spritesheet from a file.*/
	public SpriteSheet get(String fileName) {
		if(!(fileName == null || spriteSheets.containsKey(fileName))) {
			/**Sets the file-name to lower-case.*/
			fileName = fileName.toLowerCase();
			/**Creates the file representing the location of the spritesheet.*/
			FileHandle file = Gdx.files.local(fileName);
			/**Sets the file-name equal to the file's path.*/
			/**Uses the path to ensure consistency.*/
			fileName = file.path();
			/**Checks if the file-name is not stored in the map and the file exists.*/
			if(!spriteSheets.containsKey(fileName) && file.exists())
				/**Adds the spritesheet to the map.*/
				spriteSheets.put(fileName, file.readString());
		}
		
		return fileName == null ? null : new SpriteSheet(core, spriteSheets.get(fileName));
	}
	
	/**Creates a spritesheet from a file, using the given replacements.*/
	public SpriteSheet get(String fileName, HashMap<String, String> replacements) {
		if(!(fileName == null || spriteSheets.containsKey(fileName))) {
			/**Sets the file-name to lower-case.*/
			fileName = fileName.toLowerCase();
			/**Creates the file representing the location of the spritesheet.*/
			FileHandle file = Gdx.files.local(fileName);
			/**Sets the file-name equal to the file's path.*/
			/**Uses the path to ensure consistency.*/
			fileName = file.path();
			/**Checks if the file-name is not stored in the map and the file exists.*/
			if(!spriteSheets.containsKey(fileName) && file.exists())
				/**Adds the spritesheet to the map.*/
				spriteSheets.put(fileName, file.readString());
		}
		
		return fileName == null ? null : new SpriteSheet(core, spriteSheets.get(fileName), replacements);
	}
}