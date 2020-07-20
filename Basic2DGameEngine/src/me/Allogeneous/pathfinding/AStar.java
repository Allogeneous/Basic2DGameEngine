package me.Allogeneous.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import me.Allogeneous.math.Point;
import me.Allogeneous.math.Vector;

/**
 * Class with static methods to run the AStar path finding algorithm on a given AStaGrid.
 * 
 * @see 
 * <br> This implementation is based on Sebastian Lague's guide here: 
 * <br>https://www.youtube.com/watch?v=-L-WgKMFuhE&list=PLFt_AvWsXl0cq5Umv3pMC9SPnKjfp9eGW
 *
 */
public class AStar {
	
	public static enum Mode{USE_DIAGONAL_NEIGHBOR_NODES, NO_DIAGONAL_NEIGHBOR_NODES, NO_CLOSED_DIAGONAL_NEIGHBOR_NODES, USE_PARTLY_CLOSED_DIAGONAL_NEIGHBOR_NODES};
	
	public static void findPath(AStarGrid grid, AStarPathRequest request) {
		AStarNode startNode = grid.fromPoint(request.getStart());
		AStarNode endNode = grid.fromPoint(request.getEnd());
		
		if(startNode == null || endNode == null) {
			request.getCallback().onPathRequestComplete(null);
			return;
		}
		
		PriorityQueue<AStarNode> openNodes = new PriorityQueue<>(grid.getGridSize(), new AStarNodeComparator());
		List<AStarNode> closedNodes = new ArrayList<>();
		
		openNodes.add(startNode);
		
		while(!openNodes.isEmpty()) {
			AStarNode currentNode = openNodes.remove();
			
			closedNodes.add(currentNode);
			
			if(currentNode.equals(endNode)) {
				request.getCallback().onPathRequestComplete(generatePath(startNode, endNode));
				return;
			}
			
			List<AStarNode> neighbors;
			
			switch(request.getMode()) {
				case NO_CLOSED_DIAGONAL_NEIGHBOR_NODES:
					neighbors = grid.getNeighborsNoClosedDiagonals(currentNode);
					break;
				case NO_DIAGONAL_NEIGHBOR_NODES:
					neighbors = grid.getNeighborsNoDiagonals(currentNode);
					break;
				case USE_DIAGONAL_NEIGHBOR_NODES:
					neighbors = grid.getNeighbors(currentNode);
					break;
				case USE_PARTLY_CLOSED_DIAGONAL_NEIGHBOR_NODES:
					neighbors = grid.getNeighborsPartlyClosedDiagonals(currentNode);
					break;
				default:
					neighbors = grid.getNeighbors(currentNode);
					break;
			}
			
			for(AStarNode node : neighbors) {
				if(!node.isValid() || closedNodes.contains(node)) {
					continue;
				}
				
				int movementCost = currentNode.getgCost() + getAStarDistanceBetween(currentNode, node) + node.getMovementPenalty();
				if(movementCost < node.getgCost() || !openNodes.contains(node)) {
					node.setgCost(movementCost);
					node.sethCost(getAStarDistanceBetween(node, endNode));
					node.setParent(currentNode);
					
					if(!openNodes.contains(node)) {
						openNodes.add(node);
					}else {
						openNodes.remove(node);
						openNodes.add(node);
					}
				}
				
			}
		}
		request.getCallback().onPathRequestComplete(new ArrayList<AStarNode>());
	}
	
	private static List<AStarNode> generatePath(AStarNode startNode, AStarNode endNode) {
		List<AStarNode> path = new ArrayList<AStarNode>();
		
		AStarNode pathNode = endNode;
		while(pathNode != startNode) {
			path.add(pathNode);
			pathNode = pathNode.getParent();
		}
		path.add(pathNode);
		Collections.reverse(path);
		
		return path;
	}
	
	private static int getAStarDistanceBetween(AStarNode one, AStarNode two) {
		int distX = Math.abs(one.getGridX() - two.getGridX());
		int distY = Math.abs(one.getGridY() - two.getGridY());
		
		return 14 * Math.min(distX, distY) + 10 * Math.abs(distX - distY);
	}
	
	public static List<Point> movementPoints(List<AStarNode> path){
		List<Point> movementPoints = new ArrayList<>();
		Vector lastDir = new Vector(0, 0);
		for(int i = 1; i < path.size(); i++) {
			Vector direction = new Vector(path.get(i - 1).getSquare().getCentroid().getIntX() - path.get(i).getSquare().getCentroid().getIntX(), path.get(i - 1).getSquare().getCentroid().getIntY() - path.get(i).getSquare().getCentroid().getIntY());
			if(!direction.equals(lastDir)) {
				movementPoints.add(path.get(i - 1).getSquare().getCentroid());
			}
			lastDir = direction.clone();
		}
		movementPoints.add(path.get(path.size() - 1).getSquare().getCentroid());
		return movementPoints;
	}

}
