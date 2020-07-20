package me.Allogeneous.pathfinding;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AStarWorkerPool {
	
	private final AStarGrid grid;
	private final ConcurrentLinkedQueue<AStarPathRequest> pathRequests;
	private final AStarWorker[] pool;
	
	public AStarWorkerPool(int amount, AStarGrid grid, ConcurrentLinkedQueue<AStarPathRequest> pathRequests) {
		this.grid = grid;
		this.pathRequests = pathRequests;
		pool = new AStarWorker[amount];
		for(int i = 0; i < pool.length; i++) {
			pool[i] = new AStarWorker(this);
		}
	}

	public AStarGrid getGrid() {
		return grid;
	}

	public ConcurrentLinkedQueue<AStarPathRequest> getPathRequests() {
		return pathRequests;
	}
	
	public void start() {
		for(int i = 0; i < pool.length; i++) {
			pool[i].startWorker();
		}
	}
	
	public void end() {
		for(int i = 0; i < pool.length; i++) {
			pool[i].terminateWorker();
		}
		
		synchronized(this) {
			this.notifyAll();
		}
		
	}
	
	public void wakeWorker() {
		synchronized(this) {
			this.notify();
		}
	}
	
	public void wakeAllWorkers() {
		synchronized(this) {
			this.notifyAll();
		}
	}
	
}
