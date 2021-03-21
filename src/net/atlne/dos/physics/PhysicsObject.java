package net.atlne.dos.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import net.atlne.dos.utils.maths.HAMaths.RoundingUtils;
import net.atlne.dos.utils.maths.HAMaths.TrigonometryUtils;

public class PhysicsObject {
	
	/**Stores the category bits for collision objects.*/
	public static final short COLLISION_CATEGORY = 0x1;
	/**Stores the category bits for collidable terrain.*/
	public static final short COLLISION_TERRAIN_CATEGORY = 0x2;
	/**Stores the category bits for players*/
	public static final short PLAYER_CATEGORY = 0x4;
	/**Stores the category bits for NPCs, friendly and hostile.*/
	public static final short NPC_CATEGORY = 0x8;
	/**Stores the category bits for projectiles.*/
	public static final short PROJECTILE_CATEGORY = 0x10;
	/**Stores the category bits for physics-enabled non-character objects.*/
	public static final short COLLIDABLE_SCENERY_CATEGORY = 0x20;
	/**Stores the category bits for non-physics-enabled non-character objects.*/
	public static final short META_CATEGORY = 0x40;
	
	/**Stores the mask for collision objects.
	 * Set to be everything but collision objects and terrain.*/
	public static final short COLLISION_MASK = ~(COLLISION_CATEGORY | COLLISION_TERRAIN_CATEGORY);
	/**Stores the mask for collision terrain.
	 * Set to be everything but collision objects and terrain.*/
	public static final short COLLISION_TERRAIN_MASK = ~(COLLISION_CATEGORY | COLLISION_TERRAIN_CATEGORY);
	/**Stores the mask for player objects.
	 * Set to be everything but other players.*/
	public static final short PLAYER_MASK = ~PLAYER_CATEGORY;
	/**Stores the mask for NPCs, friendly and hostile.
	 * Set to be everything.*/
	public static final short NPC_MASK = -1;
	/**Stores the mask for projectiles.
	 * Set to be everything but other projectiles and collision terrain.*/
	public static final short PROJECTILE_MASK = ~(PROJECTILE_CATEGORY | COLLISION_TERRAIN_CATEGORY);
	/**Stores the mask for physics-enabled non-character objects.
	 * Set to be everything.*/
	public static final short COLLIDABLE_SCENERY_MASK = -1;
	/**Stores the mask for non-physics-enabled non-character objects.
	 * Set to be nothing.*/
	public static final short META_MASK = ~-1;

	/**Stores the StringBuilder for outputting debug text.*/
	protected static final StringBuilder DEBUG_BUILDER = new StringBuilder();
	
	/**Stores the world this object is in.*/
	protected World world;
	/**Stores the PhysicsObjectType of the object.*/
	protected PhysicsObjectType type;
	/**Stores the mass of the object.*/
	protected float mass;
	/**Stores the body definition for the object.*/
	protected BodyDef bodyDef;
	/**Stores the body for the object.*/
	protected Body body;
	/**Stores the fixture definition for the object.*/
	protected FixtureDef fixtureDef;
	/**Stores the fixture for the object.*/
	protected Fixture fixture;
	
	/**Stores the object's current position.*/
	protected Vector2 position;
	/**Stores the local rotation origin of the physics object.*/
	protected Vector2 origin;
	/**Stores the object's current angle in radians.
	 * 0 degrees implies facing towards the positive x-axis whilst centered at the origin.*/
	protected float angle;
	/**Stores the object's current linear velocity.*/
	protected Vector2 linearVelocity;
	/**Stores the object's current angular velocity.*/
	protected float angularVelocity;
	
	/**Constructor for the PhysicsObject, takes in the parameters of the body.*/
	public PhysicsObject(PhysicsObjectType type, Vector2 position, float mass) {
		this.type = type;
		this.position = position.cpy();
		this.mass = mass;
		
		/**Sets the body definition parameters.*/
		bodyDef = new BodyDef();
		bodyDef.awake = true;
		/**Determines the type of the object based on the physics object type.*/
		switch(type) {
		/**Sets the type to static if it is a collision object.*/
		case COLLISION:
		case COLLISION_TERRAIN:
			bodyDef.type = BodyType.StaticBody;
			break;
		/**Sets the type to kinematic if it is a non-collision object.*/
		case META:
			bodyDef.type = BodyType.KinematicBody;
			break;
		/**Sets the type to dynamic if not.*/
		default:
			bodyDef.type = BodyType.DynamicBody;
			break;
		}
		
		/**Sets the fixture definition parameters and creates the fixture.*/
		fixtureDef = new FixtureDef();
		fixtureDef.restitution = 0.5f;
		fixtureDef.density = mass;
		fixtureDef.filter.categoryBits = type.getCategoryBits();
		fixtureDef.filter.maskBits = type.getMaskBits();
	}
	
	/**Constructor for the PhysicsObject, takes in the parameters of the body and creates the body in the world.*/
	public PhysicsObject(World world, PhysicsObjectType type, Vector2 position, float mass) {
		this(type, position, mass);
		createBody(world);
	}
	
	/**Outputs debug information for the object. <br>
	 * \\u03C0 = pi*/
	@Override
	public String toString() {
		DEBUG_BUILDER.setLength(0);
		DEBUG_BUILDER.append("Type: " + type + "\n")
				.append("Mass: " + mass + "kg\n")
				.append("Position: (" + RoundingUtils.roundTo(position.x, 3) + "m, " + RoundingUtils.roundTo(position.y, 3) + "m)\n")
				.append("Angle: " + RoundingUtils.roundTo(angle / MathUtils.PI, 3) + "\u03C0rad");
		if(linearVelocity != null)
			DEBUG_BUILDER.append("\nLinear Velocity: (" + RoundingUtils.roundTo(linearVelocity.x, 3) + "m/s, "
													+ RoundingUtils.roundTo(linearVelocity.y, 3) + "m/s)");
		if(body != null && !body.isFixedRotation())
			DEBUG_BUILDER.append("\nAngular Velocity: " + RoundingUtils.roundTo(angularVelocity / MathUtils.PI, 3) + "\u03C0rad/s");
		
		return DEBUG_BUILDER.toString();
	}
	
	/**Creates the body in the given world.*/
	public void createBody(World world) {
		this.world = world;
		body = world.createBody(bodyDef);
		body.setTransform(position, body.getAngle());
		this.origin = body.getLocalCenter();
	}
	
	/**Creates the fixture for the body.*/
	public void createFixture() {
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(this);
	}
	
	/**Updates the PhysicsObject by updating the stored details about position, angle, and both linear and angular velocity.*/
	public void update() {
		this.position = body.getPosition().cpy();
		this.angle = body.getAngle();
		this.linearVelocity = body.getLinearVelocity().cpy();
		this.angularVelocity = body.getAngularVelocity();
	}
	
	/**Applies an angular impulse to the object.*/
	public void applyAngularImpulse(float amount) {
		body.applyAngularImpulse(amount, true);
	}
	
	/**Applies a torque to the object.*/
	public void applyTorque(float amount) {
		body.applyTorque(amount, true);
	}
	
	/**Applies a force to the object in the direction of travel.*/
	public void applyForce(float amount) {
		body.applyForceToCenter(new Vector2(TrigonometryUtils.cos(body.getAngle()) * amount, TrigonometryUtils.sin(body.getAngle()) * amount), true);
	}
	
	/**Applies a force to the object in the given angle.*/
	public void applyForce(float amount, float angle) {
		body.applyForceToCenter(new Vector2(TrigonometryUtils.cos(angle) * amount, TrigonometryUtils.sin(angle) * amount), true);
	}
	
	public World getWorld() {
		return world;
	}

	public BodyDef getBodyDef() {
		return bodyDef;
	}

	public Body getBody() {
		return body;
	}

	public FixtureDef getFixtureDef() {
		return fixtureDef;
	}

	public Fixture getFixture() {
		return fixture;
	}

	public float getMass() {
		return mass;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public Vector2 getOrigin() {
		return origin;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public Vector2 getLinearVelocity() {
		return linearVelocity;
	}
	
	public float getAngularVelocity() {
		return angularVelocity;
	}
	
	public void setPosition(Vector2 position) {
		Vector2 temp = position.cpy();
		this.position = temp;
		body.setTransform(position, body.getAngle());
	}
	
	public void setOrigin(Vector2 origin) {
		this.origin = origin;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
		body.setTransform(body.getPosition(), angle);
	}
	
	public void setLinearVelocity(Vector2 linearVelocity) {
		Vector2 temp = linearVelocity.cpy();
		this.linearVelocity = temp;
		body.setLinearVelocity(linearVelocity);
	}
	
	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
		body.setAngularVelocity(angularVelocity);
	}
}