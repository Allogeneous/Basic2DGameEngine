package me.Allogeneous.physicsObject;

import me.Allogeneous.physicsEvents.PhysicsObjectCollideEvent;
import me.Allogeneous.physicsEvents.PhysicsObjectMoveEvent;
import me.Allogeneous.shape.Shape;

public class StaticPhysicsObject extends PhysicsObject{

	public StaticPhysicsObject(Shape shape, double mass) {
		super(shape, mass);
	}

	@Override
	public final void onCollide(PhysicsObjectCollideEvent event) {}

	@Override
	public final void onMove(PhysicsObjectMoveEvent event) {}

}
