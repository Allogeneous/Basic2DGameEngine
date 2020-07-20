package me.Allogeneous.physicsObject;

import java.awt.Graphics2D;

import me.Allogeneous.collision.CollisionData;
import me.Allogeneous.math.Point;
import me.Allogeneous.math.Vector;
import me.Allogeneous.physicsEvents.PhysicsObjectCollideEvent;
import me.Allogeneous.physicsEvents.PhysicsObjectMoveEvent;
import me.Allogeneous.render.Drawable;
import me.Allogeneous.render.Focuseable;
import me.Allogeneous.shape.Shape;

public abstract class PhysicsObject implements Drawable, Focuseable{
	
	private Shape shape;
	
	private Vector initialVelocity;
	private Vector futureVelocity;
	
	private double mass;
	
	private long lastTimeMoved;
	
	private boolean visible = true;
	
	public PhysicsObject(Shape shape, Vector initialVelocity, double mass) {
		this.shape = shape;
		this.initialVelocity = initialVelocity.clone();
		this.mass = mass;
		
		this.futureVelocity = initialVelocity.clone();
		this.lastTimeMoved = System.currentTimeMillis();
	}
	
	public PhysicsObject(Shape shape, double mass) {
		this.shape = shape;
		this.initialVelocity = new Vector(0, 0);
		this.mass = mass;
		
		this.futureVelocity = new Vector(0, 0);
		this.lastTimeMoved = System.currentTimeMillis();
	}
	
	public abstract void onCollide(PhysicsObjectCollideEvent event);
	public abstract void onMove(PhysicsObjectMoveEvent event);
	
	@Override
	public void draw(Graphics2D graphics) {
		if(this.isVisible()) {
			shape.draw(graphics);
			shape.getCentroid().draw(graphics);
		}
	}
	
	public final CollisionData isColliding(PhysicsObject other) {
		return this.shape.isIntersectingShape(other.shape);
	}
	
	
	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Vector getInitialVelocity() {
		return initialVelocity;
	}

	public void setInitialVelocity(Vector initialVelocity) {
		this.initialVelocity = initialVelocity;
	}

	public Vector getFutureVelocity() {
		return futureVelocity;
	}

	public void setFutureVelocity(Vector futureVelocity) {
		this.futureVelocity = futureVelocity;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public long getLastTimeMoved() {
		return lastTimeMoved;
	}

	public void setLastTimeMoved(long lastTimeMoved) {
		this.lastTimeMoved = lastTimeMoved;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public Point getFocusePoint() {
		return this.getShape().getCentroid();
	}
}
