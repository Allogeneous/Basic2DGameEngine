package me.Allogeneous.physicsEvents;

import me.Allogeneous.collision.CollisionData;
import me.Allogeneous.physicsObject.PhysicsObject;

public class PhysicsObjectCollideEvent {
	
	private PhysicsObject physicsObject;
	private long time;
	private CollisionData collisionData;
	
	public PhysicsObjectCollideEvent(PhysicsObject physicsObject, CollisionData collisionData, long time) {
		this.physicsObject = physicsObject;
		this.setCollisionData(collisionData);
		this.time = time;
	}

	public PhysicsObject getPhysicsObject() {
		return physicsObject;
	}

	public void setPhysicsObject(PhysicsObject physicsObject) {
		this.physicsObject = physicsObject;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public CollisionData getCollisionData() {
		return collisionData;
	}

	public void setCollisionData(CollisionData collisionData) {
		this.collisionData = collisionData;
	}

	

}
