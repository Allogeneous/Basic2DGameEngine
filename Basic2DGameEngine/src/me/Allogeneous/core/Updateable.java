package me.Allogeneous.core;

public interface Updateable {
	/**
	 * The implementation of this method can do any task, dispatched by the Universe every tick
	 * 
	 * @param time - Time in milliseconds that the method was called
	 */
	public abstract void update(long time);
}
