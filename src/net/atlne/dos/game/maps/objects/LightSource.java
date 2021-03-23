package net.atlne.dos.game.maps.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;

import box2dLight.PointLight;
import net.atlne.dos.game.maps.rooms.Room;
import net.atlne.dos.graphics.GraphicsManager;
import net.atlne.dos.utils.maths.HAMaths;

public class LightSource {
	
	/**Stores the default light colour.*/
	public static final Color DEFAULT_COLOUR = Color.WHITE.cpy().lerp(Color.YELLOW.cpy(), 0.05f).lerp(Color.RED.cpy(), 0.05f);
	/**Stores the number of rays to cast per light.*/
	public static final int RAY_COUNT = 120;
	
	/**Stores the position of the light.*/
	protected Vector2 position;
	/**Stores the size of the light source object.*/
	protected Vector2 size;
	/**Stores the colour of the light.*/
	protected Color colour = DEFAULT_COLOUR.cpy();
	/**Stores the brightness of the light with 0 being none emitted. <br>
	 * The brightness of the light determines the distance to which the light shines.*/
	protected float brightness = 1;
	/**Stores the max deviation in brightness due to flickering (0 = no flicker, 1 = max flicker).*/
	protected float flicker = 0;
	/**Stores the flicker frequency multiplier.*/
	protected float flickerSpeed = 1;
	/**Stores whether the light should cast shadows or not.*/
	protected boolean shadows = false;
	/**Stores whether the light source should dim in bright ambient light.*/
	protected boolean dimming = true;

	/**Extracts the light properties from the MapObject's stored properties.*/
	public LightSource(RectangleMapObject mapObject) {
		MapProperties properties = mapObject.getProperties();
		this.position = new Vector2(mapObject.getRectangle().x, mapObject.getRectangle().y);
		this.size = new Vector2(mapObject.getRectangle().width, mapObject.getRectangle().height);
		this.colour = properties.containsKey("colour") ? properties.get("colour", Color.class) : colour;
		this.brightness = properties.containsKey("brightness") ? properties.get("brightness", float.class) : brightness;
		this.flicker = properties.containsKey("flicker") ? properties.get("flicker", float.class) : flicker;
		this.flickerSpeed = properties.containsKey("flicker-speed") ? properties.get("flicker-speed", float.class) : flickerSpeed;
		this.shadows = properties.containsKey("shadows") ? properties.get("shadows", boolean.class) : shadows;
		this.dimming = properties.containsKey("dimming") ? properties.get("dimming", boolean.class) : dimming;
	}
	
	/**Generates a corresponding Light object for the given room.*/
	public PointLight generate(Room room) {
		PointLight light = new PointLight(room.getRayHandler(), RAY_COUNT, colour,
				getRealBrightness(), position.x + (size.x / 2), position.y + (size.y / 2));
		light.setSoft(true);
		light.setXray(!shadows);
		return light;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Color getColour() {
		return colour;
	}

	public float getBrightness() {
		return brightness;
	}
	
	public float getRealBrightness() {
		return brightness * GraphicsManager.PPM * HAMaths.E_INVERSE;
	}
	
	public float getFlicker() {
		return flicker;
	}
	
	public float getRealFlicker() {
		return flicker * GraphicsManager.PPM * HAMaths.E_INVERSE;
	}
	
	public float getFlickerSpeed() {
		return flickerSpeed;
	}

	public boolean hasShadows() {
		return shadows;
	}
	
	public boolean isDimming() {
		return dimming;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
	
	public void setFlicker(float flicker) {
		this.flicker = flicker;
	}
	
	public void setFlickerSpeed(float flickerSpeed) {
		this.flickerSpeed = flickerSpeed;
	}

	public void setShadows(boolean shadows) {
		this.shadows = shadows;
	}
	
	public void setDimming(boolean dimming) {
		this.dimming = dimming;
	}
}