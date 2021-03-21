package net.atlne.dos.graphics.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Disposable;

public class FontLoader implements Disposable {
	
	/**Stores the max font size and the width of the border around the rendered text.*/
	public static final int MAX_SIZE = 128, BORDER_WIDTH = 1;
	/**Stores an array of all the fonts generated.*/
    private transient BitmapFont[] text = new BitmapFont[MAX_SIZE];
    /**Stores the generator object used to generate the fonts for the array.*/
    private transient FreeTypeFontGenerator generator;
    /**Stores the parameters for the font generator.*/
    private transient FreeTypeFontParameter params;
    
    /**Takes in the location of the font file as a parameter. <br>
     * Also takes in an value determining whether the fonts should simply scale from an existing font. <br>
     * The value refers to the font-size to scale from.*/
    public FontLoader(String fontLocation) {
    	FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
    	/**Initialises the generator using the file location given.*/
    	generator = new FreeTypeFontGenerator(Gdx.files.local(fontLocation));
    	params = new FreeTypeFontParameter();
    	
    	/**Sets the parameters of the object constant for each font, regardless of size.*/
    	params.borderWidth = BORDER_WIDTH;
        params.borderColor = Color.BLACK;
        params.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        params.magFilter = TextureFilter.MipMapNearestLinear;
        params.minFilter = TextureFilter.MipMapNearestLinear;
        params.genMipMaps = true;
        params.kerning = true;
        params.genMipMaps = true;
    }
    
    /**Disposes of any loaded fonts.*/
    @Override
    public void dispose() {
    	/**Iterates over every font.*/
    	for(BitmapFont font : text) {
    		/**Checks if the font is loaded before disposing of it.*/
    		if(font != null) {
    			font.dispose();
    		}
    	}
    }
    
    /**Loads a font of a certain size given the generator and parameters.*/
    public void generate(int size) {
    	/**Sets the size of the font, scaling for the pixels-per-inch for the device the game is being executed on.*/
        params.size = Math.round(((size + 1) * Gdx.graphics.getDensity()));
        /**Generates the font given the parameter.*/
        text[size] = generator.generateFont(params);
        /**Sets the flag allowing colour mark-up tags to be used in text rendered.*/
        text[size].getData().markupEnabled = true;
    }
    
    /**Gets a font of a given size, returns the smallest size if the parameter is too small. <br>
     * Alternatively, if the size given is too large, returns the largest size.*/
    public BitmapFont get(int size) {
    	/**Uses a ternary conditional operator to condense the conditional statements required on to one line.*/
    	return text[size > 0 ? (size <= text.length ? size - 1 : text.length - 1) : 1];
    }
    
    /**Gets the width in pixels of a given string with a given font.*/
    public static float getWidth(BitmapFont font, String text) {
    	return new GlyphLayout(font, text).width;
    }
    
    /**Gets the height in pixels of a given string with a given font.*/
    public static float getHeight(BitmapFont font, String text) {
    	return new GlyphLayout(font, text).height;
    }
}