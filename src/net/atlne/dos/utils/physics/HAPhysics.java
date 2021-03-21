package net.atlne.dos.utils.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class HAPhysics {
	
	/**Creates a rectangular PolygonShape based on the size vector given and rotated to the appropriate angle.*/
	public static PolygonShape createRectangleShape(Vector2 size, float angle) {
		PolygonShape rect = new PolygonShape();
		rect.setAsBox(size.x, size.y, new Vector2(0, 0), angle);
		return rect;
	}
	
	/**Creates a rectangular PolygonShape based on the size vector given.*/
	public static PolygonShape createRectangleShape(Vector2 size) {
		return createRectangleShape(size, 0);
	}
	
	/**Creates a rectangular PolygonShape based on the sizes given.*/
	public static PolygonShape createRectangleShape(float x, float y, float angle) {
		return createRectangleShape(new Vector2(x, y), angle);
	}
	
	/**Creates a rectangular PolygonShape based on the sizes given.*/
	public static PolygonShape createRectangleShape(float x, float y) {
		return createRectangleShape(x, y, 0);
	}
	
	/**Creates a CircleShape based on the radius given.*/
	public static CircleShape createCircleShape(float radius) {
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		return circle;
	}
}