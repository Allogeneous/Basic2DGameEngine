package me.Allogeneous.pathfinding;

import me.Allogeneous.math.Point;
import me.Allogeneous.pathfinding.AStar.Mode;

public class AStarPathRequest {
	
	private final Point start;
	private final Point end;
	private final Mode mode;
	private final AStarPathable callback;
	
	public AStarPathRequest(Point start, Point end, Mode mode, AStarPathable callback) {
		this.start = start;
		this.end = end;
		this.mode = mode;
		this.callback = callback;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}

	public Mode getMode() {
		return mode;
	}

	public AStarPathable getCallback() {
		return callback;
	}
	
	

}
