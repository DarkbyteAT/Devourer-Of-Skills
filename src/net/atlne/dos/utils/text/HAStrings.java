package net.atlne.dos.utils.text;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;

import net.atlne.dos.graphics.text.FontLoader;

public class HAStrings {
	
	/**Stores the maximum feasible font size displayable on the screen.*/
	public static int MAX_FONT_SIZE = FontLoader.MAX_SIZE;
	
	/**Converts a string in the format "this-format-here" to "This format here".*/
	public static String dashToDisplay(String dash) {
		String result = "";
		String[] words = dash.split("-");
		words[0] = Character.toUpperCase(words[0].charAt(0)) + words[0].substring(1, words[0].length());
		for(String word : words)
			result += word + " ";
		return result.trim();
	}
	
	/**Converts a string in the format "this-format-here" to "This Format Here".*/
	public static String dashToCapitalisedDisplay(String dash) {
		String result = "";
		String[] words = dash.split("-");
		for(String word : words)
			result += Character.toUpperCase(word.charAt(0)) + word.substring(1, word.length()) + " ";
		return result.trim();
	}
	
	/**Replaces all occurrences of the keys in the map with their associated values, in the given string.*/
	public static String replaceAll(String str, HashMap<String, String> replacements) {
		for(String key : replacements.keySet())
			str = str.replaceAll(key, replacements.get(key));
		return str;
	}
	
	/**Gets the colour prefix for formatting text.*/
	public static String getColourPrefix(Color color) {
		return "[#" + color.toString().toUpperCase() + "]";
	}
}