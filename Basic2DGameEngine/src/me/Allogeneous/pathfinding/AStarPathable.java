package me.Allogeneous.pathfinding;

import java.util.List;


public interface AStarPathable {
	
	public abstract void onPathRequestComplete(List<AStarNode> path);
	
}
