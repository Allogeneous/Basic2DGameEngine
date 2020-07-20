package me.Allogeneous.physicsEvents;

public class PhysicsObjectMoveEvent {

	private long time;
	
	public PhysicsObjectMoveEvent(long time) {

		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
