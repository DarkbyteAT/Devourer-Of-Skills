package net.atlne.dos.game.entities;

import com.badlogic.gdx.math.MathUtils;

public enum Direction {
	UP(MathUtils.PI / 2), LEFT(MathUtils.PI), DOWN(MathUtils.PI * 1.5f), RIGHT(0);
	
	/**Stores the angle.*/
	private float angle;
	
	/**Constructor for the Direction enum, stores the angle.*/
	Direction(float angle) {
		this.angle = angle;
	}
	
	/**Determines a direction based on the angle given in radians.*/
	public static Direction fromAngle(float angle) {
		angle = (float) (Math.toDegrees(angle) % 360) / 90f;
		return (new Direction[] {RIGHT, UP, LEFT, DOWN})[Math.round(angle) % 4];
	}
	
	/**Returns the angle associated with the direction.*/
	public float toAngle() {
		return angle;
	}
	
	/**Returns the opposite direction.*/
	public Direction opposite() {
		return this == UP 	? DOWN  :
			   this == DOWN ? UP    :
			   this == LEFT ? RIGHT :
							  LEFT;
	}
	
	/**Returns the perpendicular directions to the given direction.*/
	public Direction[] perpendicular() {
		return this == UP || this == DOWN ?
				new Direction[] {LEFT, RIGHT} :
					new Direction[] {UP, DOWN};
	}
	
	/**Returns whether a given direction is perpendicular to the current direction.*/
	public boolean isPerpendicular(Direction d) {
		return (this == UP || this == DOWN) ? d == LEFT || d == RIGHT :
				(this == LEFT || this == RIGHT) ? d == UP || d == DOWN :
					false;
	}
}