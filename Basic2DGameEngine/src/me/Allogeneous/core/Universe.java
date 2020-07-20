package me.Allogeneous.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.util.Pair;
import me.Allogeneous.collision.CollisionData;
import me.Allogeneous.physicsEvents.PhysicsObjectCollideEvent;
import me.Allogeneous.physicsEvents.PhysicsObjectMoveEvent;
import me.Allogeneous.physicsObject.PhysicsObject;



/**
 * This class dispatches all events for PhysicsObjects and classes that implement the Updateable 
 * interface when added to an instance of this class. This class serves as the "physics loop" for the game.
 *
 */
public class Universe implements Runnable{
	
	//Enum so that internal operations can be done in a thread safe way
	private static enum Action{ADD_PO, REMOVE_PO, CLEAR_PO, ADD_UPU, REMOVE_UPU, CLEAR_UPU, ADD_PU, REMOVE_PU, CLEAR_PU}
	
	//This list allows for opjects to be added and removed from the physics object and updateable lists in a thread safe way
	private volatile CopyOnWriteArrayList<Pair<Action, Object>> actionSyncMap;
	//List of physics physicsObjects that get updated
	private volatile List<PhysicsObject> physicsObjects;
	//Copy for thread safe viewing
	private volatile List<PhysicsObject> viewablePhysicsObjects;
	//There are things that will have their update() method called every tick
	private volatile List<Updateable> unpauseableUpdateables;
	//Copy for thread safe viewing
	private volatile List<Updateable> viewableUnpauseableUpdateables;
	//There are things that will have their update() method called every tick if physics are not paused
	private volatile List<Updateable> pauseableUpdateables;
	//Copy for thread safe viewing
	private volatile List<Updateable> viewablePauseableUpdateablesUpdateables;
	//Updates per second
	private volatile int tickSpeed;
	
	//Pauses the physics events and pauseableUpdateables
	private volatile boolean interupt = false;
	//Extra pause check variable
	private volatile boolean checkPause = false;
	//Ends the thread this class is running on
	private volatile boolean end = false;
	
	
	/**
	 * Creates a Universe with a given tick speed.
	 * 
	 * @param tickSpeed - How many times the Universe updates per second 
	 */
	public Universe(int tickSpeed) {
		physicsObjects = new ArrayList<>();
		viewablePhysicsObjects = Collections.unmodifiableList(new ArrayList<>());
		unpauseableUpdateables = new ArrayList<>();
		viewableUnpauseableUpdateables = Collections.unmodifiableList(new ArrayList<>());
		pauseableUpdateables = new ArrayList<>();
		viewablePauseableUpdateablesUpdateables = Collections.unmodifiableList(new ArrayList<>());
		actionSyncMap = new CopyOnWriteArrayList<>();
		this.tickSpeed = tickSpeed;
	}
	
	/**
	 * @return An unmodifiable list of PhysicsObjects being managed by this Universe 
	 */
	public List<PhysicsObject> viewPhysicsObjects(){
		return viewablePhysicsObjects;
	}
	
	/**
	 * @return An unmodifiable list of Updateables that can be paused and are being managed by this Universe 
	 */
	public List<Updateable> viewPauseableUpdateables(){
		return viewablePauseableUpdateablesUpdateables;
	}
	
	/**
	 * @return An unmodifiable list of Updateables that cannot be paused and are being managed by this Universe 
	 */
	public List<Updateable> viewUnpauseableUpdateables(){
		return viewableUnpauseableUpdateables;
	}
	
	/**
	 * Adds a PhysicsObject to the Universe
	 * 
	 * @param physicsObject - the PhysicsObject to be added
	 */
	public void addPhysicsObject(PhysicsObject physicsObject) {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.ADD_PO, physicsObject));
	}
	
	/**
	 * Adds an Updateable that can be paused to the Universe
	 * 
	 * @param pauseableUpdateable - the Updateable to be added
	 */
	public void addPauseableUpdateable(Updateable pauseableUpdateable) {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.ADD_PU, pauseableUpdateable));
	}
	
	/**
	 * Adds an Updateable that cannot be paused to the Universe
	 * 
	 * @param unpauseableUpdateable - the Updateable to be added
	 */
	public void addUnpauseableUpdateable(Updateable unpauseableUpdateable) {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.ADD_UPU, unpauseableUpdateable));
	}
	
	/**
	 * Removes the given PhysicsObject from the Universe 
	 * 
	 * @param physicsObject - PhysicsObject to be removed
	 */
	public void removePhysicsObject(PhysicsObject physicsObject) {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.REMOVE_PO, physicsObject));
	}
	
	/**
	 * Removes the given (pauseable) Updateable from the Universe 
	 * 
	 * @param pauseableUpdateable - Updateable to be removed
	 */
	public void removePauseableUpdateable(Updateable pauseableUpdateable) {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.REMOVE_PU, pauseableUpdateable));
	}
	
	/**
	 * Removes the given (unpauseable) Updateable from the Universe 
	 * 
	 * @param unpauseableUpdateable - Updateable to be removed
	 */
	public void removeUnpauseableUpdateable(Updateable unpauseableUpdateable) {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.REMOVE_UPU, unpauseableUpdateable));
	}
	
	/**
	 * Removes all PhysicsObjects from the Universe
	 */
	public void clearPhysicsObjects() {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.CLEAR_PO, null));
	}
	
	/**
	 * Removes all (pauseable) Updateables from the Universe
	 */
	public void clearPauseableUpdateable() {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.CLEAR_PU, null));
	}
	
	/**
	 * Removes all (unpauseable) Updateables from the Universe
	 */
	public void clearUnpauseableUpdateable() {
		this.actionSyncMap.add(new Pair<Action, Object>(Action.CLEAR_UPU, null));
	}
	
	/**
	 * Pauses the Universe. The Universe will stop dispatching events for PhysicsObjects and (pauseable) Updateables.
	 */
	public void pause() {
		interupt = true;
	}
	
	/**
	 * @return true, if the Universe is paused, false if not
	 */
	public boolean isPaused() {
		return interupt;
	}
	
	/**
	 * Resumes event dispatches for everything in the Universe
	 */
	public void play() {
		if(!checkPause) {
			checkPause = true;
		}
	}
	
	
	/**
	 * Changes the Universe's tick speed
	 * 
	 * @param tickSpeed - Number of times the universe will update per second
	 */
	public void setTickSpeed(int tickSpeed) {
		this.tickSpeed = tickSpeed;
	}
	
	/**
	 * @return The amount of milliseconds the Universe sleeps between each tick
	 */
	public int tps() {
		return 1000 / tickSpeed;
	}
	
	/**
	 * @return The state of the thread the Universe is running on, specifically, if it has ended or not
	 */
	public boolean isEnd() {
		return end;
	}

	/**
	 * All Updateables and PhysicsObjects in this Universe will no longer have their update and event methods called. 
	 * Note that once set to true, this flag cannot back to false to resume the Universe.
	 * 
	 * @param end - If set to true, the thread running this Universe will end
	 * 
	 */
	public void setEnd(boolean end) {
		this.end = end;
	}
	
	
	//Dispatches updates for pauseable Updateables
	private void dispatchPauseableUpdates(long time) {
		for(Updateable u : pauseableUpdateables) {
			u.update(time);
		}
	}
	
	//Dispatches updates from unpauseable Updateables
	private void dispatchUpdates(long time) {
		for(Updateable u : unpauseableUpdateables) {
			u.update(time);
		}
	}
	
	
	//Checks for collisions and dispatches PhysicsObjectCollideEvents for PhysicsObjects
	private void dispatchCollideEvents(long time) {
		for(PhysicsObject object : physicsObjects) {
			for(PhysicsObject object2 : physicsObjects) {
				if(object != object2) {
					CollisionData cd = object.isColliding(object2);
					if(cd.collision){
						if(object != null && object2 != null) {
							object.onCollide(new PhysicsObjectCollideEvent(object2, cd, time));
						}
					}
				}
			}
		}
	}
	
	//Dispatches move events for PhysicsObjects
	private void dispatchMoveEvents(long time) {
		for(PhysicsObject object : physicsObjects) {
			object.onMove(new PhysicsObjectMoveEvent(time));
		}
	}
	
	//Checks if the Universe is paused
	private void checkPause(long time) {
		if(checkPause) {
			if(interupt) {
				for(PhysicsObject po : physicsObjects) {
					po.setLastTimeMoved(time);
				}
			}
			interupt = false;
			checkPause = false;
		}
	}
	
	//Runs actions in the action sync map in a thread safe way
	private void checkActionSyncMap() {
		if(!actionSyncMap.isEmpty()) {
			List<Pair<Action, Object>> rmv = new ArrayList<>();
			for (Pair<Action, Object> pair : actionSyncMap) {
			    switch(pair.getKey()) {
					case ADD_PO:
						physicsObjects.add((PhysicsObject) pair.getValue());
						break;
					case ADD_PU:
						pauseableUpdateables.add((Updateable) pair.getValue());
						break;
					case ADD_UPU:
						unpauseableUpdateables.add((Updateable) pair.getValue());
						break;
					case CLEAR_PO:
						physicsObjects.clear();
						break;
					case CLEAR_PU:
						pauseableUpdateables.clear();
						break;
					case CLEAR_UPU:
						unpauseableUpdateables.clear();
						break;
					case REMOVE_PO:
						physicsObjects.remove((PhysicsObject) pair.getValue());
						break;
					case REMOVE_PU:
						pauseableUpdateables.remove((Updateable) pair.getValue());
						break;
					case REMOVE_UPU:
						unpauseableUpdateables.remove((Updateable) pair.getValue());
						break;
					default:
						break;
			    }
			    rmv.add(pair);
			}
			actionSyncMap.removeAll(rmv);
		}
	}
	
	//Synchronizes viewable lists with current live lists
	private void updateViewableLists() {
		viewablePhysicsObjects = Collections.unmodifiableList(new ArrayList<PhysicsObject>(physicsObjects));
		viewableUnpauseableUpdateables = Collections.unmodifiableList(new ArrayList<>(viewableUnpauseableUpdateables));
		viewablePauseableUpdateablesUpdateables = Collections.unmodifiableList(new ArrayList<>(viewablePauseableUpdateablesUpdateables));
	}
	

	/**
	 *This is the physics loop, this loop is called every tick.
	 */
	@Override
	public void run() {
		while(!end) {
			long currentTimeMillis = System.currentTimeMillis();
			this.checkPause(currentTimeMillis);
			this.checkActionSyncMap();
			this.dispatchUpdates(currentTimeMillis);
			if(!interupt) {
				this.dispatchCollideEvents(currentTimeMillis);
				this.dispatchMoveEvents(currentTimeMillis);
				this.dispatchPauseableUpdates(currentTimeMillis);
				
			}
			this.updateViewableLists();
			try {
				Thread.sleep(tps());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
}
