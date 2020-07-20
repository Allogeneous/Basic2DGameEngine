package me.Allogeneous.pathfinding;

import java.util.List;

import me.Allogeneous.physicsObject.PhysicsObject;

public class AStarUpdateable {
	
	private PhysicsObject physicsObject;
	private List<AStarNode> currentTiles;
	
	public AStarUpdateable(PhysicsObject physicsObject, List<AStarNode> currentTiles) {
		this.setPhysicsObject(physicsObject);
		this.setCurrentTiles(currentTiles);
	}

	public PhysicsObject getPhysicsObject() {
		return physicsObject;
	}

	public void setPhysicsObject(PhysicsObject physicsObject) {
		this.physicsObject = physicsObject;
	}

	public List<AStarNode> getCurrentTiles() {
		return currentTiles;
	}

	public void setCurrentTiles(List<AStarNode> currentTiles) {
		this.currentTiles = currentTiles;
	}
	
	public void validateAllTiles() {
		for(AStarNode node : currentTiles) {
			node.releaseInvalidClaim();
		}
	}

}
