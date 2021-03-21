package net.atlne.dos.scenes.game.pause;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import net.atlne.dos.Core;
import net.atlne.dos.graphics.textures.TextureLoader;
import net.atlne.dos.graphics.ui.SceneTitle;
import net.atlne.dos.scenes.Scene;
import net.atlne.dos.utils.maths.HAMaths.InterpolationUtils;

public class PauseScene extends Scene {
	
	/**Stores the pause scene title text.*/
	public static final String TITLE = "Paused";
	/**Stores the target alpha value for the pause screen.*/
	public static final float TARGET_ALPHA = (float) (Math.sqrt(3) - 1);
	
	/**Stores the pause scene title.*/
	private SceneTitle title;
	
	/**Stores the pause screen overlay texture.*/
	private Texture overlay;
	/**Stores the pause screen alpha value.*/
	private float alpha;
	/**Flag storing whether we're currently exiting the pause menu or not.*/
	private boolean exiting;
	
	public PauseScene(Core core) {
		super(core, true);
		Gdx.graphics.setCursor(core.getGraphics().getTextures().getCursor("assets/textures/cursors/standard.png", 0, 0));
		title = new SceneTitle(core, TITLE);
		overlay = TextureLoader.generateRectangle(1, 1, Color.BLACK.cpy());
		addActor(title);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		overlay.dispose();
	}
	
	/**Draws the pause screen.*/
	@Override
	public void draw() {
		Color colour = batch.getColor().cpy();
		batch.setColor(1, 1, 1, alpha);
		batch.begin();
		batch.draw(overlay, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		batch.setColor(colour.cpy());
		
		title.setColor(1, 1, 1, alpha);
		super.draw();
	}
	
	/**Escapes back to the title screen if the ESC key is pressed.*/
	@Override
	public void act() {
		super.act();
		alpha = InterpolationUtils.lirp(exiting ? TARGET_ALPHA : 0, alpha, exiting ? 0 : TARGET_ALPHA, 0.15f);
		
		if(core.getInput().keyJustPressed(Keys.ESCAPE))
			exiting = true;
		
		if(exiting && alpha <= 0) {
			Gdx.graphics.setCursor(core.getGraphics().getTextures().getCursor("assets/textures/cursors/crosshair.png", 16, 16));
			exiting = false;
			core.getScenes().popScene();
			dispose();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		title.resize(width, height);
	}
}