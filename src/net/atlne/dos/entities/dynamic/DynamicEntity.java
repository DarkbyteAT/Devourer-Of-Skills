package net.atlne.dos.entities.dynamic;

import com.badlogic.gdx.graphics.g2d.Sprite;

import net.atlne.dos.entities.Direction;
import net.atlne.dos.entities.Entity;
import net.atlne.dos.physics.PhysicsObject;
import net.atlne.dos.utils.Pair;

public class DynamicEntity extends Entity {

	@SafeVarargs
	public DynamicEntity(String ID, String state, PhysicsObject body, Direction direction, Pair<String, Sprite>... sprites) {
		super(ID, state, body, direction, sprites);
	}
}