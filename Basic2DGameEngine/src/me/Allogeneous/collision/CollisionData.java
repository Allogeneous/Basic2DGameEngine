package me.Allogeneous.collision;

import me.Allogeneous.math.Vector;

/**
 * This class is a data wrapper for the information that is returned
 * after a collision check.
 */
public class CollisionData {
	
	//Minimum translation vector from collision
	public final Vector mtv;
	//Will be true if there was a collision
	public final boolean collision;
	//Distance that the two shapes overlap
	public final double overlap;

	
	/**
	 * This is the data that is returned from a collision check.
	 * 
	 * @param mtv - Minimum translation vector from collision
	 * @param collision - Will be true if there was a collision
	 * @param overlap - Distance that the two shapes overlap
	 */
	public CollisionData(Vector mtv, boolean collision, double overlap) {
		this.mtv = mtv;
		this.collision = collision;
		this.overlap = overlap;
	}
}
