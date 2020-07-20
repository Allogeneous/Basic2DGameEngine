package me.Allogeneous.pathfinding;

import me.Allogeneous.shape.ConvexShape;

public class AStarNode {
	
	private final int gridX;
	private final int gridY;
	private final ConvexShape square;
	
	private int valid;
	private int gCost;
	private int hCost;
	private int movementPenalty;
	
	private AStarNode parent;
	
	public AStarNode(int gridX, int gridY, ConvexShape square, int movementPenalty, int valid) {
		this.gridX = gridX;
		this.gridY = gridY;
		this.square = square;
		
		this.movementPenalty = movementPenalty;
		this.valid = valid;
		this.gCost = 0;
		this.hCost = 0;
	}

	public ConvexShape getSquare() {
		return square;
	}

	public boolean isValid() {
		return valid == 0;
	}
	
	public void releaseInvalidClaim(){
		if(this.valid != 0) {
			valid -= 1;
		}
	}

	public void addInvalidClaim(){
		valid += 1;
	}
	
	public void setValid() {
		this.valid = 0;
	}

	public int getfCost() {
		return gCost + hCost;
	}

	public int getgCost() {
		return gCost;
	}

	public void setgCost(int gCost) {
		this.gCost = gCost;
	}

	public int gethCost() {
		return hCost;
	}

	public void sethCost(int hCost) {
		this.hCost = hCost;
	}

	public int getGridY() {
		return gridY;
	}

	public int getGridX() {
		return gridX;
	}

	public AStarNode getParent() {
		return parent;
	}

	public void setParent(AStarNode parent) {
		this.parent = parent;
	}

	public int getMovementPenalty() {
		return movementPenalty;
	}

	public void setMovementPenalty(int movementPenalty) {
		this.movementPenalty = movementPenalty;
	}
}
