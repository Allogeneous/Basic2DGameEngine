package me.Allogeneous.pathfinding;

public class AStarWorker implements Runnable{

	private final AStarWorkerPool pool;

	
	private volatile boolean run = true;
	
	public AStarWorker(AStarWorkerPool pool) {
		this.pool = pool;
	}
	
	@Override
	public void run() {
		while(run) {
			AStarPathRequest request = pool.getPathRequests().poll();
			if(request != null) {
				AStar.findPath(pool.getGrid(), request);
			}
			synchronized(pool) {
				try {
					pool.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void startWorker() {
		new Thread(this).start();
	}
	
	public void terminateWorker() {
		run = false;
	}
}
