package net.atlne.dos.utils.maths;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class HAMaths {
	
	/**e^-1*/
	public static final float E_INVERSE = (float) (1 / Math.E);
	
	/**Credit to Riven: http://www.java-gaming.org/topics/extremely-fast-sine-cosine/36469/view.html*/
	public static class TrigonometryUtils {
		private static final int SIN_BITS, SIN_MASK, SIN_COUNT;
	    private static final float radFull, radToIndex;
	    private static final float degFull, degToIndex;
	    private static final float[] sin, cos;
	
	    static {
	        SIN_BITS = 12;
	        SIN_MASK = ~(-1 << SIN_BITS);
	        SIN_COUNT = SIN_MASK + 1;
	
	        radFull = (float) (Math.PI * 2.0);
	        degFull = (float) (360.0);
	        radToIndex = SIN_COUNT / radFull;
	        degToIndex = SIN_COUNT / degFull;
	
	        sin = new float[SIN_COUNT];
	        cos = new float[SIN_COUNT];
	
	        for (int i = 0; i < SIN_COUNT; i++) {
	            sin[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
	            cos[i] = (float) Math.cos((i + 0.5f) / SIN_COUNT * radFull);
	        }
	
	        // Four cardinal directions (credits: Nate)
	        for (int i = 0; i < 360; i += 90) {
	            sin[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * Math.PI / 180.0);
	            cos[(int) (i * degToIndex) & SIN_MASK] = (float) Math.cos(i * Math.PI / 180.0);
	        }
	    }
	
	    public static final float sin(float rad) {
	        return sin[(int) (rad * radToIndex) & SIN_MASK];
	    }
	
	    public static final float cos(float rad) {
	        return cos[(int) (rad * radToIndex) & SIN_MASK];
	    }
	}
	
	public static class RandomUtils {
		/**Outputs a random Gaussian distributed number.*/
		public static float randomGaussian() {
			return (float) new Random().nextGaussian();
		}
	}
	
	public static class RoundingUtils {
		/**Rounds a given number to a given number of decimal places.*/
		public static float roundTo(float number, int decimalPlaces) {
			/**Calculates the exponent to 10 of the decimal places.
			 * Shorthanded from here-on as N.*/
			float N = (float) Math.pow(10, decimalPlaces);
			/**Multiplies the number by N.
			 * Rounds the number.
			 * Divides the number by N.
			 * Returns the result.*/
			return Math.round(number * N) / N;
		}
	}
	
	public static class ProjectionUtils {
		/**Projects the world coordinates as they are relative to screen coordinates.*/
		public static Vector2 project(Vector2 worldCoords, Camera camera) {
			Vector3 coords = camera.project(new Vector3(worldCoords.cpy(), 0));
			return new Vector2(coords.x, coords.y);
		}
		
		/**Un-projects the screen coordinates as they are relative to world coordinates.*/
		public static Vector2 unproject(Vector2 screenCoords, Camera camera) {
			Vector3 coords = camera.unproject(new Vector3(screenCoords.cpy(), 0));
			return new Vector2(coords.x, coords.y);
		}
	}
	
	public static class InterpolationUtils {
		/**Linearly interpolates the input value to the desired target in the desired time.
		 * 
		 * @param start - The value to interpolate from
		 * @param current - The current value after <emph>n</emph> interpolations
		 * @param target - The target value to reach
		 * @param t - The time that should be taken to reach the target*/
		public static float lirp(float start, float current, float target, float t) {
			boolean increase = start < target;
			float lirp = current + (((target - start) / t) * Gdx.graphics.getDeltaTime());
			return increase ? Math.min(target, lirp) : Math.max(target, lirp);
		}
		
		/**Logarithmically interpolates the input value asymptotically to the desired target.
		 * 
		 * @param start - The value to interpolate
		 * @param target - The target value to reach
		 * @param a - The coefficient between 0 and 1 to advance the start value to the target by each frame.*/
		public static float lorp(float start, float target, float a) {
			return start + (a * (target - start));
		}
		
		/**"Over"-interpolates the input value logarithmically to the desired target by capping the min/max output value at the target.
		 * 
		 * @param start - The value to interpolate
		 * @param target - The target value to reach
		 * @param a - The coefficient between 0 and 1 to advance the start value to the target by each frame.
		 * @param b - The number to add to the target, allowing for the "over-lerp"ing process to occur. Recommended to be small.*/
		public static float overlorp(float start, float target, float a, float b) {
			boolean increase = start < target;
			float lorp = lorp(start, target + (increase ? b : -b), a);
			return increase ? Math.min(target, lorp) : Math.max(target, lorp);
		}
	}
}