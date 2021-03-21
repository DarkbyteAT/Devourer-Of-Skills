package net.atlne.dos.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import net.atlne.dos.Core;
import net.atlne.dos.graphics.text.FontLoader;
import net.atlne.dos.scenes.menu.MenuScene;

public class LoadingScene extends Scene {
	
	/**Stores the title to draw when loading.*/
	public static final String LOADING_TEXT = "Loading";
	
	/**Stores the max font size loaded.*/
	private int maxLoadedSize;
	/**Flag storing whether the physics shapes have been loaded or not.*/
	private boolean physicsShapesLoaded;
	
	public LoadingScene(Core core) {
		super(core, false);
		Gdx.graphics.setCursor(core.getGraphics().getTextures().getCursor("assets/textures/cursors/standard.png", 0, 0));
	}
	
	/**Draws the loading screen.*/
	@Override
	public void draw() {
		batch.begin();
		
		/**Only renders the logo if the max font size is greater than 0.*/
		for(int size = 1; size <= maxLoadedSize && maxLoadedSize >= 1; size++) {
			BitmapFont font = core.getGraphics().getFonts().get(size);
			/**Renders the logo text in the centre of the scene with the current largest loaded font size.*/
			font.draw(batch, LOADING_TEXT,
					(getWidth() - FontLoader.getWidth(font, LOADING_TEXT)) / 2,
					(getHeight() - FontLoader.getHeight(font, LOADING_TEXT)) / 2);
		}
		
		/**Renders the appropriate loading flag if the text has finished loading.*/
		if(maxLoadedSize == FontLoader.MAX_SIZE) {
			BitmapFont infoFont = core.getGraphics().getFonts().get(32);
			
			if(!physicsShapesLoaded) {
				infoFont.draw(batch, "Loading physics engine...",
						(getWidth() - FontLoader.getWidth(infoFont, "Loading physics shapes...")) / 2,
						(getHeight() / 4));
			}
		}
		
		batch.end();
	}
	
	/**Loads in assets.*/
	@Override
	public void act() {
		/**Loads fonts if necessary, else switches scene to the menu scene.*/
		if(maxLoadedSize < FontLoader.MAX_SIZE) {
			core.getGraphics().getFonts().generate(maxLoadedSize++);
		} else {
			if(!physicsShapesLoaded) {
				physicsShapesLoaded = core.getPhysicsShapes().generate();
			} else {
				core.getAudio().playSoundEffect("load");
				core.getScenes().popScene();
				core.getScenes().pushScene(new MenuScene(core));
				dispose();
			}
		}
	}
}