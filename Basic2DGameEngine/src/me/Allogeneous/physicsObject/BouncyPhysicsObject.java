package me.Allogeneous.physicsObject;

import me.Allogeneous.math.Physics;
import me.Allogeneous.math.Vector;
import me.Allogeneous.physicsEvents.PhysicsObjectCollideEvent;
import me.Allogeneous.physicsEvents.PhysicsObjectMoveEvent;
import me.Allogeneous.shape.Shape;

public class BouncyPhysicsObject extends PhysicsObject{

	public BouncyPhysicsObject(Shape shape, Vector initialVelocity, double mass) {
		super(shape, initialVelocity, mass);
	}

	@Override
	public final void onCollide(PhysicsObjectCollideEvent event) {
		Physics.collisionCodeBounce(this, event);
	}

	@Override
	public final void onMove(PhysicsObjectMoveEvent event) {
		Physics.movementCode(this, event);
	}
}
