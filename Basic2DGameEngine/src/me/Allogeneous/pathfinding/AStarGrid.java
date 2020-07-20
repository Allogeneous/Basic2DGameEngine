package me.Allogeneous.pathfinding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.Allogeneous.math.Point;
import me.Allogeneous.pathfinding.AStar.Mode;
import me.Allogeneous.physicsObject.PhysicsObject;
import me.Allogeneous.physicsObject.StaticPhysicsObject;
import me.Allogeneous.render.Drawable;
import me.Allogeneous.shape.Circle;
import me.Allogeneous.shape.ConvexShape;
import me.Allogeneous.shape.LineSegment;

/**
 * This class is a grid used for path finding.
 * 
 * @see 
 * <br> This class uses an implementation of Bresenham's line algorithm found here:
 * <br> https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
 * <br>
 * <br> The implementation of this class is also loosely based on Sebastian Lague's guide here: 
 * <br>https://www.youtube.com/watch?v=-L-WgKMFuhE&list=PLFt_AvWsXl0cq5Umv3pMC9SPnKjfp9eGW
 *
 */
public class AStarGrid implements Drawable{
	
	private final Point bottomLeft;
	private final Point topRight;
	private final int xNodeCount, yNodeCount, nodeSize;
	private final ConcurrentLinkedQueue<AStarPathRequest> pathRequests;
	private final ArrayList<AStarUpdateable> movingParts;
	private final AStarWorkerPool workerPool;
	
	private volatile List<AStarNode> lastPath;
	
	private AStarNode[][] grid;
	
	public AStarGrid(int frequency, Point bottomLeft, int xNodeCount, int yNodeCount, int nodeSize, int workers) {
		this.bottomLeft = bottomLeft;
		this.topRight = bottomLeft.add(xNodeCount * nodeSize, yNodeCount * nodeSize);
		this.xNodeCount = xNodeCount;
		this.yNodeCount = yNodeCount;
		this.nodeSize = nodeSize;
		this.lastPath = new ArrayList<AStarNode>();
		this.movingParts = new ArrayList<AStarUpdateable>();
		this.pathRequests = new ConcurrentLinkedQueue<AStarPathRequest>();
		workerPool = new AStarWorkerPool(workers, this, pathRequests);
		workerPool.start();
		setUpGrid(frequency);
	}
	public AStarGrid(List<StaticPhysicsObject> mask, Point bottomLeft, int xNodeCount, int yNodeCount, int nodeSize, int workers) {
		this.bottomLeft = bottomLeft;
		this.topRight = bottomLeft.add(xNodeCount * nodeSize, yNodeCount * nodeSize);
		this.xNodeCount = xNodeCount;
		this.yNodeCount = yNodeCount;
		this.nodeSize = nodeSize;
		this.lastPath = new ArrayList<AStarNode>();
		this.movingParts = new ArrayList<AStarUpdateable>();
		this.pathRequests = new ConcurrentLinkedQueue<AStarPathRequest>();
		workerPool = new AStarWorkerPool(workers, this, pathRequests);
		workerPool.start();
		setUpGridFast(mask);
	}
	
	public AStarGrid(List<StaticPhysicsObject> mask, List<PhysicsObject> moveables, Point bottomLeft, int xNodeCount, int yNodeCount, int nodeSize, int workers, int gridUpdatesPerSecond) {
		this.bottomLeft = bottomLeft;
		this.topRight = bottomLeft.add(xNodeCount * nodeSize, yNodeCount * nodeSize);
		this.xNodeCount = xNodeCount;
		this.yNodeCount = yNodeCount;
		this.nodeSize = nodeSize;
		this.lastPath = new ArrayList<AStarNode>();
		this.movingParts = new ArrayList<AStarUpdateable>();
		this.pathRequests = new ConcurrentLinkedQueue<AStarPathRequest>();
		workerPool = new AStarWorkerPool(workers, this, pathRequests);
		workerPool.start();
		setUpGridFast(mask, moveables);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					AStarGrid.this.update();
					try {
						Thread.sleep(1000 / gridUpdatesPerSecond);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void offerPathRequest(AStarPathRequest request) {
		pathRequests.offer(request);
		workerPool.wakeWorker();
	}
	
	private void setUpGrid(int frequency) {
		Random r = new Random();
		grid = new AStarNode[xNodeCount][yNodeCount];
		for(int x = 0; x < xNodeCount; x++) {
			for(int y = 0; y < yNodeCount; y++) {
				Point bottomLeft = this.bottomLeft.add(x * nodeSize, y * nodeSize);
				Point bottomRight = bottomLeft.add(nodeSize, 0);
				Point topLeft = bottomLeft.add(0, nodeSize);
				Point topRight = topLeft.add(nodeSize, 0);
				int randomNum = r.nextInt(frequency);
				int valid = 0;
				if(randomNum == frequency - 1) {
					valid = 1;
				}
				grid[x][y] = new AStarNode(x ,y, new ConvexShape(topLeft, topRight, bottomRight, bottomLeft), 0, valid);
			}
		}
	}
	
	private void setUpGridFast(List<StaticPhysicsObject> mask) {
		grid = new AStarNode[xNodeCount][yNodeCount];
		for(int x = 0; x < xNodeCount; x++) {
			for(int y = 0; y < yNodeCount; y++) {
				Point bottomLeft = this.bottomLeft.add(x * nodeSize, y * nodeSize);
				Point bottomRight = bottomLeft.add(nodeSize, 0);
				Point topLeft = bottomLeft.add(0, nodeSize);
				Point topRight = topLeft.add(nodeSize, 0);
				grid[x][y] = new AStarNode(x ,y, new ConvexShape(topLeft, topRight, bottomRight, bottomLeft), 0, 0);
			}
		}
		for(StaticPhysicsObject po : mask) {
			if(po.getShape() instanceof Circle) {
				for(LineSegment ls : ConvexShape.makeSquare(po.getShape().getCentroid(), ((Circle)po.getShape()).radius * 2).getSides()) {
					plotLineSegment(ls);
				}
			}else {
				for(LineSegment ls : po.getShape().getSides()) {
					plotLineSegment(ls);
				}
			}
		}	
	}
	
	private void setUpGridFast(List<StaticPhysicsObject> mask, List<PhysicsObject> moveables) {
		setUpGridFast(mask);
		for(PhysicsObject po : moveables) {
			List<AStarNode> allNodes = new ArrayList<>();
			if(po.getShape() instanceof Circle) {
				for(LineSegment ls : ConvexShape.makeSquare(po.getShape().getCentroid(), ((Circle)po.getShape()).radius * 2).getSides()) {
					List<AStarNode> lsNodes = plotLineSegment(ls);
					if(lsNodes != null) {
						allNodes.addAll(lsNodes);
					}
				}
				if(!allNodes.isEmpty()) {
					movingParts.add(new AStarUpdateable(po, allNodes));
				}
			}else {
				for(LineSegment ls : po.getShape().getSides()) {
					List<AStarNode> lsNodes = plotLineSegment(ls);
					if(lsNodes != null) {
						allNodes.addAll(lsNodes);
					}
				}
				if(!allNodes.isEmpty()) {
					movingParts.add(new AStarUpdateable(po, allNodes));
				}
			}
		}
	}
	
	private void update() {
		for(AStarUpdateable asu : movingParts) {
			asu.validateAllTiles();
			asu.getCurrentTiles().clear();
			List<AStarNode> allNodes = new ArrayList<>();
			if(asu.getPhysicsObject().getShape() instanceof Circle) {
				for(LineSegment ls : ConvexShape.makeSquare(asu.getPhysicsObject().getShape().getCentroid(), ((Circle)asu.getPhysicsObject().getShape()).radius * 2).getSides()) {
					List<AStarNode> lsNodes = plotLineSegment(ls);
					if(lsNodes != null) {
						allNodes.addAll(lsNodes);
					}
				}
				if(!allNodes.isEmpty()) {
					asu.setCurrentTiles(allNodes);
				}
			}else {
				for(LineSegment ls : asu.getPhysicsObject().getShape().getSides()) {
					List<AStarNode> lsNodes = plotLineSegment(ls);
					if(lsNodes != null) {
						allNodes.addAll(lsNodes);
					}
				}
				if(!allNodes.isEmpty()) {
					asu.setCurrentTiles(allNodes);
				}
			}
		}
	}
	

	private List<AStarNode> plotLineGridLow(Point a, Point b) {
		List<AStarNode> nodes = new ArrayList<>();
		AStarNode one = fromPoint(a);
		AStarNode two = fromPoint(b);
		if(one == null || two == null) {
			return null;
		}

	    int dx = two.getGridX() - one.getGridX();
	    int dy = two.getGridY() - one.getGridY();
	    
	    int yi = 1;
	    
	    if(dy < 0) {
	    	yi = -1;
	    	dy = -dy;
	    }
	    
	    int D = 2*dy - dx;
	    int y = one.getGridY();
	    
	    for (int x = one.getGridX(); x <= two.getGridX(); x++) {
	    	if(x >= bottomLeft.x && y >= bottomLeft.y && x < topRight.x && y < topRight.y) {
	    		grid[x][y].addInvalidClaim();
	    		nodes.add(grid[x][y]);
	    	}
	    	if(D > 0) {
	               y = y + yi;
	               D = D - 2*dx;
	        }
	        D = D + 2*dy;
	    }
	
	    
	    return nodes;
	}
	
	private List<AStarNode> plotLineGridHigh(Point a, Point b) {
		List<AStarNode> nodes = new ArrayList<>();
		AStarNode one = fromPoint(a);
		AStarNode two = fromPoint(b);
		if(one == null || two == null) {
			return null;
		}
		
	    int dx = two.getGridX() - one.getGridX();
	    int dy = two.getGridY() - one.getGridY();
	    
	    int xi = 1;
	    
	    if(dx < 0) {
	    	xi = -1;
	    	dx = -dx;
	    }
	    
	    int D = 2*dx - dy;
	    int x = one.getGridX();
	    
	    for (int y = one.getGridY(); y <= two.getGridY(); y++) { 
	    	if(x >= bottomLeft.x && y >= bottomLeft.y && x < topRight.x && y < topRight.y) {
	    		grid[x][y].addInvalidClaim();
	    		nodes.add(grid[x][y]);
	    	}
	    	if(D > 0) {
	               x = x + xi;
	               D = D - 2*dy;
	        }
	        D = D + 2*dx;
	    }

	    
	    return nodes;
	}
	
	private List<AStarNode> plotLineSegment(LineSegment ls) {
		if(Math.abs(ls.two.y - ls.one.y) < Math.abs(ls.two.x - ls.one.x)){
			if(ls.one.x > ls.two.x) {
				return plotLineGridLow(ls.two, ls.one);
			}else {
				return plotLineGridLow(ls.one, ls.two);
			}
		}else {
			if(ls.one.y > ls.two.y){
	            return plotLineGridHigh(ls.two, ls.one);
			}else {
	        	return plotLineGridHigh(ls.one, ls.two);
			}
		}
	}
	
	public void requestPath(AStarPathable callback, Point start, Point end, Mode mode) {
		this.offerPathRequest(new AStarPathRequest(start, end, mode, callback));
	}
	
	public AStarNode fromPoint(Point point) {
		if(point.x < bottomLeft.x || point.y < bottomLeft.y || point.x > topRight.x || point.y > topRight.y) {
			return null;
		}
		
		Point offset = point.subtract(bottomLeft);
		int x = offset.getIntX() / nodeSize;
		int y = offset.getIntY() / nodeSize;
		try {
			return grid[x][y];
		}catch(Exception ex) {
			return null;
		}
	}
	
	public List<AStarNode> getNeighbors(AStarNode node){
		List<AStarNode> neighbors = new ArrayList<>();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				if(x != 0 || y != 0) {
					int cX = node.getGridX() + x;
					int cY = node.getGridY() + y;
					if(cX >= 0 && cX < grid.length && cY >= 0 && cY < grid[0].length) {
						neighbors.add(grid[cX][cY]);
					}
				}
			}
		}
		return neighbors;
	}
	
	public List<AStarNode> getNeighborsNoDiagonals(AStarNode node){
		List<AStarNode> neighbors = new ArrayList<>();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				if(x == 0 ^ y == 0) {
					int cX = node.getGridX() + x;
					int cY = node.getGridY() + y;
					if(cX >= 0 && cX < grid.length && cY >= 0 && cY < grid[0].length) {
						neighbors.add(grid[cX][cY]);
					}
				}
			}
		}
		return neighbors;
	}
	
	public List<AStarNode> getNeighborsNoClosedDiagonals(AStarNode node){
		List<AStarNode> neighbors = new ArrayList<>();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				if(x != 0 || y != 0) {
					int cX = node.getGridX() + x;
					int cY = node.getGridY() + y;
					if(cX >= 0 && cX < grid.length && cY >= 0 && cY < grid[0].length) {
						neighbors.add(grid[cX][cY]);
					}else {
						neighbors.add(null);
					}
				}
			}
		}
		
		List<AStarNode> checkedNeighbors = new ArrayList<>();
		
		for(int i = 0; i < neighbors.size(); i++) {
			if(neighbors.get(i) != null) {
				switch(i) {
					case 0:
						if(neighbors.get(1) != null && neighbors.get(3) != null) {
							if(neighbors.get(1).isValid() && neighbors.get(3).isValid()) {
								checkedNeighbors.add(neighbors.get(i));
							}
						}
						break;
					case 1:
						checkedNeighbors.add(neighbors.get(i));
						break;
					case 2:
						if(neighbors.get(1) != null && neighbors.get(4) != null) {
							if(neighbors.get(1).isValid() && neighbors.get(4).isValid()) {
								checkedNeighbors.add(neighbors.get(i));
							}
						}
						break;
					case 3:
						checkedNeighbors.add(neighbors.get(i));
						break;
					case 4:
						checkedNeighbors.add(neighbors.get(i));
						break;
					case 5:
						if(neighbors.get(3) != null && neighbors.get(6) != null) {
							if(neighbors.get(3).isValid() && neighbors.get(6).isValid()) {
								checkedNeighbors.add(neighbors.get(i));
							}
						}
						break;
					case 6:
						checkedNeighbors.add(neighbors.get(i));
						break;
					case 7:
						if(neighbors.get(4) != null && neighbors.get(6) != null) {
							if(neighbors.get(4).isValid() && neighbors.get(6).isValid()) {
								checkedNeighbors.add(neighbors.get(i));
							}
						}
						break;
					default:
						break;
				}
			}
		}
		
		return checkedNeighbors;
	}
	
	public List<AStarNode> getNeighborsPartlyClosedDiagonals(AStarNode node){
		List<AStarNode> neighbors = new ArrayList<>();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				if(x != 0 || y != 0) {
					int cX = node.getGridX() + x;
					int cY = node.getGridY() + y;
					if(cX >= 0 && cX < grid.length && cY >= 0 && cY < grid[0].length) {
						neighbors.add(grid[cX][cY]);
					}else {
						neighbors.add(null);
					}
				}
			}
		}
		
		List<AStarNode> checkedNeighbors = new ArrayList<>();
		
		for(int i = 0; i < neighbors.size(); i++) {
			if(neighbors.get(i) != null) {
				switch(i) {
					case 0:
						if(neighbors.get(1) != null && neighbors.get(3) != null) {
							if(neighbors.get(1).isValid() || neighbors.get(3).isValid()) {
								checkedNeighbors.add(neighbors.get(i));
							}
						}
						break;
					case 1:
						checkedNeighbors.add(neighbors.get(i));
						break;
					case 2:
						if(neighbors.get(1) != null && neighbors.get(4) != null) {
							if(neighbors.get(1).isValid() || neighbors.get(4).isValid()) {
								checkedNeighbors.add(neighbors.get(i));
							}
						}
						break;
					case 3:
						checkedNeighbors.add(neighbors.get(i));
						break;
					case 4:
						checkedNeighbors.add(neighbors.get(i));
						break;
					case 5:
						if(neighbors.get(3) != null && neighbors.get(6) != null) {
							if(neighbors.get(3).isValid() || neighbors.get(6).isValid()) {
								checkedNeighbors.add(neighbors.get(i));
							}
						}
						break;
					case 6:
						checkedNeighbors.add(neighbors.get(i));
						break;
					case 7:
						if(neighbors.get(4) != null || neighbors.get(6) != null) {
							if(neighbors.get(4).isValid() && neighbors.get(6).isValid()) {
								checkedNeighbors.add(neighbors.get(i));
							}
						}
						break;
					default:
						break;
				}
			}
		}
		
		return checkedNeighbors;
	}

	@Override
	public void draw(Graphics2D graphics) {
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[x].length; y++) {
				if(lastPath.contains(grid[x][y])) {
					graphics.setColor(Color.WHITE);
					graphics.fillRect(grid[x][y].getSquare().getVertices().get(3).getIntX(), grid[x][y].getSquare().getVertices().get(3).getIntY(), nodeSize, nodeSize);
				}else if(!grid[x][y].isValid()){
					graphics.setColor(Color.RED);
					graphics.fillRect(grid[x][y].getSquare().getVertices().get(3).getIntX(), grid[x][y].getSquare().getVertices().get(3).getIntY(), nodeSize, nodeSize);
				}else {
					grid[x][y].getSquare().draw(graphics);
				}
			}
		}
	}

	public List<AStarNode> getLastPath() {
		return lastPath;
	}

	public void setLastPath(List<AStarNode> lastPath) {
		this.lastPath = lastPath;
	}
	
	public int getGridSize() {
		return xNodeCount * yNodeCount;
	}

}
