package net.atlne.dos.graphics;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;

import net.atlne.dos.Core;
import net.atlne.dos.Manager;
import net.atlne.dos.graphics.text.FontLoader;
import net.atlne.dos.graphics.textures.TextureLoader;

public class GraphicsManager extends Manager {
	
	/**Stores the pixels-per-meter conversion for rendering.*/
	public static final float PPM = 32;
	
	/**Stores the font loader for the game.*/
	public FontLoader fonts;
	/**Stores the texture loader for the game.*/
	public TextureLoader textures;

	/**Constructor for the GraphicsManager, initialises the VisUI library. <br>
	 * Also initialises the camera  for the game's scaling.*/
	public GraphicsManager(Core core) {
		super(core);
		fonts = new FontLoader("assets/fonts/standard.ttf");
		textures = new TextureLoader(core);
		
		/**Loads the VisUI library.
		 * Sets the title alignment for windows to the centre.
		 * Sets the fonts to allow colouring via the LibGDX mark-up language.*/
		VisUI.load(Gdx.files.local("assets/ui/x2/tinted.json"));
		VisUI.setDefaultTitleAlign(Align.center);
		VisUI.getSkin().getFont("default-font").getData().markupEnabled = true;
		VisUI.getSkin().getFont("small-font").getData().markupEnabled = true;
	}

	/**Disposes of the VisUI library.*/
	@Override
	public void dispose() {
		VisUI.dispose(true);
		fonts.dispose();
		textures.dispose();
	}
	
	/**Clears the screen.*/
	public void update() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	/**Takes a screenshot of the screen.
	 * @throws IOException */
	public void screenshot() {
		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0,
				Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
		/**This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing.*/
		for(int i = 4; i < pixels.length; i += 4)
		    pixels[i - 1] = (byte) 255;
		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
		/**Creates the folder if non-existent.*/
		if(!new File("screenshots").exists())
			new File("screenshots").mkdir();
		PixmapIO.writePNG(Gdx.files.local("screenshots/screenshot "
				+ new SimpleDateFormat("yyyy-MM-dd HH mm ss").format(new Date()) + ".png"), pixmap);
		pixmap.dispose();
	}
	
	/**Returns a TextureRegion of the screen.*/
	public TextureRegion screen() {
		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0,
				Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
		/**This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing.*/
		for(int i = 4; i < pixels.length; i += 4)
		    pixels[i - 1] = (byte) 255;
		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
		return new TextureRegion(new Texture(pixmap));
	}
	
	public FontLoader getFonts() {
		return fonts;
	}
	
	public TextureLoader getTextures() {
		return textures;
	}
}