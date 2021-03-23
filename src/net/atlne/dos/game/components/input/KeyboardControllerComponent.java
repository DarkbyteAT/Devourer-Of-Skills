package net.atlne.dos.game.components.input;

import com.badlogic.gdx.math.MathUtils;

import net.atlne.dos.Core;
import net.atlne.dos.game.components.Component;
import net.atlne.dos.game.entities.Direction;
import net.atlne.dos.game.entities.Entity;
import net.atlne.dos.physics.PhysicsObject;

public class KeyboardControllerComponent implements Component {
	
	/**Stores the expected idle state name.*/
	public static final String IDLE_STATE = "idle";
	/**Stores the expected moving state name.*/
	public static final String MOVE_STATE = "move";

	/**Stores the Entity to move.*/
	protected Entity entity;
	/**Stores the force to move the entity with.*/
	protected float force;
	/**Stores whether the Entity is moving this tick.*/
	protected boolean moving;
	
	public KeyboardControllerComponent(Entity entity, float force) {
		this.entity = entity;
		this.force = force;
	}

	@Override
	public void run() {
		PhysicsObject body = entity.getBody();
		moving = false;
		
		if(Core.getInput().bindPressed("up")) {
			entity.setDirection(Direction.UP);
			body.applyForce(force, Direction.UP.toAngle());
			entity.setState(MOVE_STATE);
			moving = true;
		}
		
		if(Core.getInput().bindPressed("down")) {
			entity.setDirection(Direction.DOWN);
			body.applyForce(force, Direction.DOWN.toAngle());
			entity.setState(MOVE_STATE);
			moving = true;
		}
		
		if(Core.getInput().bindPressed("left")) {
			entity.setDirection(Direction.LEFT);
			body.applyForce(force, Direction.LEFT.toAngle());
			entity.setState(MOVE_STATE);
			moving = true;
		}
		
		if(Core.getInput().bindPressed("right")) {
			entity.setDirection(Direction.RIGHT);
			body.applyForce(force, Direction.RIGHT.toAngle());
			entity.setState(MOVE_STATE);
			moving = true;
		}
		
		/**When not moving, increases the linear damping and applies a resistive force to slow movement.*/
		if(!moving) {
			if(entity.getState().equals(MOVE_STATE)) {
				entity.setState(IDLE_STATE);
				body.applyForce(body.getMass() * body.getLinearVelocity().len2(),
						body.getLinearVelocity().angleRad() - MathUtils.PI);
			}
			
			body.getBody().setLinearDamping(20f);
		} else body.getBody().setLinearDamping(10f);
	}

	public Entity getEntity() {
		return entity;
	}
	
	public float getForce() {
		return force;
	}
	
	public boolean isMoving() {
		return moving;
	}
	
	public void setForce(float force) {
		this.force = force;
	}
}