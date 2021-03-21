package net.atlne.dos.physics;

public enum PhysicsObjectType {
	COLLISION(PhysicsObject.COLLISION_CATEGORY, PhysicsObject.COLLISION_MASK, 0, 1),
	COLLISION_TERRAIN(PhysicsObject.COLLISION_TERRAIN_CATEGORY, PhysicsObject.COLLISION_TERRAIN_MASK, 0, 1),
	PLAYER(PhysicsObject.PLAYER_CATEGORY, PhysicsObject.PLAYER_MASK, 2),
	NPC(PhysicsObject.NPC_CATEGORY, PhysicsObject.NPC_MASK),
	PROJECTILE(PhysicsObject.PROJECTILE_CATEGORY, PhysicsObject.PROJECTILE_MASK, 1, 4),
	COLLIDABLE_SCENERY(PhysicsObject.COLLIDABLE_SCENERY_CATEGORY, PhysicsObject.COLLIDABLE_SCENERY_MASK),
	META(PhysicsObject.META_CATEGORY, PhysicsObject.META_MASK, 0, 1, 2, 3, 4, 5, 6);
	
	/**Stores the category bits for the object type.*/
	private final short categoryBits;
	/**Stores the mask bits for the object type.*/
	private final short maskBits;
	/**Stores the types to ignore collisions with.*/
	private final int[] ignore;
	
	/**Constructor for the object types, takes in their category and mask bits.*/
	PhysicsObjectType(short categoryBits, short maskBits, int... ignore) {
		this.categoryBits = categoryBits;
		this.maskBits = maskBits;
		this.ignore = ignore;
	}
	
	/**Returns false if this PhysicsObjectType ignores the type in the parameters with respect to collisions.
	 * Also returns false if that is satisfied with respect to the other type.*/
	public boolean collides(PhysicsObjectType type) {
		/**Checks if this type ignores the type from the parameters.*/
		for(int i = 0; i < this.ignore.length; i++)
			if(this.ignore[i] == type.ordinal())
				return false;
		/**Checks if the type from the parameters ignores this type.*/
		for(int i = 0; i < type.ignore.length; i++)
			if(type.ignore[i] == this.ordinal())
				return false;
		/**Returns true otherwise.*/
		return true;
	}
	
	/**Returns the PhysicsObjectTypes to ignore collisions with for this type.*/
	public PhysicsObjectType[] getIgnored() {
		PhysicsObjectType[] ignore = new PhysicsObjectType[this.ignore.length];
		for(int i = 0; i < this.ignore.length; i++)
			ignore[i] = PhysicsObjectType.values()[this.ignore[i]];
		return ignore;
	}

	public short getCategoryBits() {
		return categoryBits;
	}

	public short getMaskBits() {
		return maskBits;
	}
}