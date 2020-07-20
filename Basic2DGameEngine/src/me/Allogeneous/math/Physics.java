package me.Allogeneous.math;

import me.Allogeneous.physicsEvents.PhysicsObjectCollideEvent;
import me.Allogeneous.physicsEvents.PhysicsObjectMoveEvent;
import me.Allogeneous.physicsObject.PhysicsObject;
import me.Allogeneous.physicsObject.StaticPhysicsObject;

/**
 * This class contains a bunch of static methods for physics calculations. These methods can be called to 
 * implement some basic physics code in classes that extend the PhysicsObject class.
 */
public class Physics {

	/**
	 * A Kinematics equation
	 * 
	 * @param initialVelocity
	 * @param acceleration
	 * @param time
	 * @return the velocity vector
	 */
	public static Vector getVelocity(Vector initialVelocity, Vector acceleration, double time) {
		return initialVelocity.add(acceleration.multiply(time));
	}
	
	/**
	 *  A Kinematics equation
	 * 
	 * @param currentVelocity
	 * @param initialVelocity
	 * @param time
	 * @return the displacement vector
	 */
	public static Vector getDisplacement(Vector currentVelocity, Vector initialVelocity, double time) {
		return currentVelocity.add(initialVelocity).divide(2d).multiply(time);
	}
	
	/**
	 *  A Kinematics equation
	 * 
	 * @param initialVelocity
	 * @param time
	 * @param acceleration
	 * @return the displacement vector
	 */
	public static Vector getDisplacement(Vector initialVelocity, double time, Vector acceleration) {
		return initialVelocity.multiply(time).add(acceleration.multiply(time * time).divide(2d));
	}
	
	/**
	 *  A Kinematics equation
	 * 
	 * @param initialVelocity
	 * @param acceleration
	 * @param displacement
	 * @return the velocity vector squared
	 */
	public static Vector getVelocitySquared(Vector initialVelocity, Vector acceleration, Vector displacement) {
		return initialVelocity.multiply(initialVelocity).add(acceleration.multiply(displacement).multiply(2d));
	}
	
	
	/**
	 * This method will make it so that the PhysicsObject will completely stop when it collides with something.
	 * 
	 * @param physicsObject - forwarded from the onCollide() method
	 * @param event - forwarded from the onCollide() method
	 */
	public static void collisionCodeStop(PhysicsObject physicsObject, PhysicsObjectCollideEvent event) {
		physicsObject.setFutureVelocity(new Vector(0,0));	
		Vector mtv = event.getCollisionData().mtv;
		physicsObject.getShape().moveBy(mtv.normalize().toPoint());
	}
	
	/**
	 * This method will make it so that the PhysicsObject will bounce off of one another object and the angle of reflection
	 * 
	 * @param physicsObject - forwarded from the onCollide() method
	 * @param event - forwarded from the onCollide() method
	 */
	public static void collisionCodeBounceLight(PhysicsObject physicsObject, PhysicsObjectCollideEvent event) {
		Vector proj = Vector.projectAontoB(physicsObject.getInitialVelocity(), event.getCollisionData().mtv);
		proj = proj.multiply(2);
		
		physicsObject.setFutureVelocity(physicsObject.getInitialVelocity().subtract(proj));
		
		Vector mtv = event.getCollisionData().mtv;
		physicsObject.getShape().moveBy(mtv.normalize().toPoint());
	}
	
	/**
	 * This method will make it so that the PhysicsObject will bounce off of one another and consider the objects masses
	 * 
	 * @param physicsObject - forwarded from the onCollide() method
	 * @param event - forwarded from the onCollide() method
	 */
	public static void collisionCodeBounce(PhysicsObject physicsObject, PhysicsObjectCollideEvent event) {
		if(!(event.getPhysicsObject() instanceof StaticPhysicsObject)) {
			double massCalc = (2 * event.getPhysicsObject().getMass()) / (physicsObject.getMass() + event.getPhysicsObject().getMass()); 
			Vector between = physicsObject.getShape().getCentroid().between(event.getPhysicsObject().getShape().getCentroid());
			Vector initialVelocitySub = physicsObject.getInitialVelocity().subtract(event.getPhysicsObject().getInitialVelocity());
			Vector result = Vector.projectAontoB(initialVelocitySub, between).multiply(massCalc);
			
			physicsObject.setFutureVelocity(physicsObject.getInitialVelocity().subtract(result));
			
			Vector mtv = event.getCollisionData().mtv;
			physicsObject.getShape().moveBy(mtv.normalize().toPoint());
			
			return;
		}
		collisionCodeBounceLight(physicsObject, event);
	}
	
	
	/**
	 * Some generic code that will allow a PhysicsObject to move around accurately on the screen
	 * 
	 * @param physicsObject - forwarded from the onCollide() method
	 * @param event - forwarded from the onCollide() method
	 */
	public static void movementCode(PhysicsObject physicsObject, PhysicsObjectMoveEvent event) {
		double deltaT = (event.getTime() - physicsObject.getLastTimeMoved()) / 1000d;
		
		Point displacement = Physics.getDisplacement(physicsObject.getFutureVelocity(), deltaT, new Vector(0, 0)).toPoint();
		physicsObject.getShape().moveBy(displacement);
	
		physicsObject.setInitialVelocity(physicsObject.getFutureVelocity().clone());
		physicsObject.setLastTimeMoved(event.getTime());
	}
}
