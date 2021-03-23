package net.atlne.dos.game.maps.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import net.atlne.dos.physics.PhysicsObject;
import net.atlne.dos.physics.PhysicsObjectType;

public class CollisionTile extends PhysicsObject {
	
	/**Stores the tile rotation.*/
	protected int tileRotation;
	
	/**Creates the collision tile to be used in rooms.*/
	public CollisionTile(Vector2 position, int tileRotation, Shape shape, boolean impasse) {
		super(impasse ? PhysicsObjectType.COLLISION_TERRAIN : PhysicsObjectType.COLLISION, position, 1);
		this.tileRotation = tileRotation;
		this.fixtureDef.shape = shape;
	}
	
	/**Creates the fixture after creating the body.*/
	@Override
	public void createBody(World world) {
		super.createBody(world);
		createFixture();
		setAngle(tileRotation * 0.5f * MathUtils.PI);
		body.setFixedRotation(true);
	}
}