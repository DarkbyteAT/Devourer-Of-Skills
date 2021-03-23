package net.atlne.dos.graphics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.atlne.dos.Core;
import net.atlne.dos.graphics.text.FontLoader;

public class SceneTitle extends Actor {
	
	/**Stores the text for the title.*/
	protected String text;
	/**Stores the font for rendering the title.*/
	private BitmapFont font;
	/**Stores the font size of the title font.*/
	private int fontSize;

	public SceneTitle(String text) {
		this.text = text;
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		Color original = font.getColor().cpy();
		font.setColor(getColor());
		font.draw(batch, text, (getStage().getWidth() - FontLoader.getWidth(font, text)) / 2, getStage().getHeight() - (FontLoader.getHeight(font, text) / 4));
		font.setColor(original);
		super.draw(batch, parentAlpha);
	}
	
	/**Updates the title font size to reflect the change in screen size.*/
	public void resize(float width, float height) {
		fontSize = FontLoader.MAX_SIZE;
		while((FontLoader.getWidth(Core.getGraphics().getFonts().get(fontSize), text) > width
				|| FontLoader.getHeight(Core.getGraphics().getFonts().get(fontSize), text) > height)
				&& fontSize > 1)
			fontSize--;
		font = Core.getGraphics().getFonts().get(fontSize);
	}
}