package net.atlne.dos.graphics.textures;

import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.utils.Disposable;

import net.atlne.dos.Core;
import net.atlne.dos.graphics.textures.animation.spritesheet.SpriteSheetLoader;

public class TextureLoader implements Disposable {
	
	/**Stores the SpriteSheet loader for the game.*/
	private SpriteSheetLoader spriteSheets;
	
	/**Stores the textures loaded within this map, mapping them to their file names.*/
	private ConcurrentHashMap<String, Texture> textures;
	/**Stores the loaded cursors within this map.*/
	private ConcurrentHashMap<String, Cursor> cursors;
	/**Stores the 9-patches loaded within this map, mapping them to their file names.*/
	private ConcurrentHashMap<String, NinePatch> ninePatches;
	
	/**Constructor for the texture loader, initialises the maps.*/
	public TextureLoader(Core core) {
		spriteSheets = new SpriteSheetLoader(core);
		textures = new ConcurrentHashMap<String, Texture>();
		cursors = new ConcurrentHashMap<String, Cursor>();
		ninePatches = new ConcurrentHashMap<String, NinePatch>();
	}
	
	/**Disposes of any loaded assets.*/
	@Override
	public void dispose() {
		for(String key : textures.keySet()) {
			textures.get(key).dispose();
			textures.remove(key);
		}
		
		for(String key : cursors.keySet()) {
			cursors.get(key).dispose();
			cursors.remove(key);
		}
	}
	
	/**Function to load textures into the game. Loads them from the map if already loaded. <br>
	 * If not loaded, attempts to load the texture from local files.*/
	public Texture get(String fileName) {
		if(!(fileName == null || textures.containsKey(fileName))) {
			/**Sets the file-name to lower-case.*/
			fileName = fileName.toLowerCase();
			/**Creates the file representing the location of the texture.*/
			FileHandle file = Gdx.files.local(fileName);
			/**Sets the file-name equal to the file's path.*/
			/**Uses the path to ensure consistency.*/
			fileName = file.path();
			/**Checks if the file-name is not stored in the map and the file exists.*/
			if(!textures.containsKey(fileName) && file.exists())
				/**Adds the texture to the map.*/
				textures.put(fileName, new Texture(file));
		}
		
		/**Returns the texture stored at the file name given.*/
		return fileName == null ? null : textures.get(fileName);
	}
	
	/**Function to load cursors into the game. Loads them from the map if already loaded. <br>
	 * If not loaded, attempts to load the cursor from local files.*/
	public Cursor getCursor(String fileName, int centerX, int centerY) {
		if(!cursors.containsKey(fileName)) {
			/**Adds the cursor to the map, loading the texture into the map, then the resulting cursor respectively.*/
			Texture cursorTexture = get(fileName);
			if(!cursorTexture.getTextureData().isPrepared())
				cursorTexture.getTextureData().prepare();
			cursors.put(fileName, Gdx.graphics.newCursor(cursorTexture.getTextureData().consumePixmap(), centerX, centerY));
			/**Reloads the used texture.*/
			cursorTexture.dispose();
			textures.remove(fileName);
			get(fileName);
		}
		
		/**Returns the cursor stored at the file name given.*/
		return cursors.get(fileName);
	}
	
	/**Function to load 9-patches into the game. Loads them from the map if already loaded. <br>
	 * If not loaded, attempts to load the 9-patch from local files.*/
	public NinePatch getNinePatch(String fileName, int leftX, int rightX, int topY, int bottomY) {
		if(!ninePatches.containsKey(fileName)) {
			/**Adds the 9-patch to the map, loading the texture into the map, then the resulting 9-patch respectively.*/
			ninePatches.put(fileName, new NinePatch(get(fileName), leftX, rightX, topY, bottomY));
		}
		
		/**Returns the 9-patch stored at the file name given.*/
		return ninePatches.get(fileName);
	}
	
	/**Generates a rectangle texture using the given parameters.*/
	public static Texture generateRectangle(float width, float height, Color colour) {
		/**Creates the pixel map for the rectangle.*/
		Pixmap rectMap = new Pixmap(Math.round(width), Math.round(height), Format.RGBA8888);
		/**Sets the colour of the pixmap to the desired colour and fills the rectangle.*/
		rectMap.setColor(colour);
		rectMap.fill();
		/**Encapsulates the map in a texture and returns the texture.*/
		return new Texture(rectMap);
	}
	
	public SpriteSheetLoader getSpriteSheets() {
		return spriteSheets;
	}
	
	public ConcurrentHashMap<String, Texture> getTextures() {
		return textures;
	}
	
	public ConcurrentHashMap<String, Cursor> getCursors() {
		return cursors;
	}
	
	public ConcurrentHashMap<String, NinePatch> getNinePatches() {
		return ninePatches;
	}
}