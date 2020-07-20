package me.Allogeneous.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.util.Pair;

public class LayerSet {
	
	private static enum Action{CREATE_LAYER, DELETE_LAYER, ADD_TO_LAYER, REMOVE_FROM_LAYER, REMOVE_FROM_LAYER_INDEX, REMOVE_FROM_EVERY_LAYER}
	
	private ArrayList<ArrayList<Drawable>> layers;
	private CopyOnWriteArrayList<Pair<Action, Object>> actionSyncMap;
	
	public LayerSet() {
		this.layers = new ArrayList<>();
		actionSyncMap = new CopyOnWriteArrayList<>();
	}
	
	public void createLayer() {
		actionSyncMap.add(new Pair<Action, Object>(Action.CREATE_LAYER, null));
	}
	
	public void removeLayer() {
		actionSyncMap.add(new Pair<Action, Object>(Action.DELETE_LAYER, null));
	}
	
	public void add(int layer, Drawable drawable) {
		actionSyncMap.add(new Pair<Action, Object>(Action.ADD_TO_LAYER, new Pair<Integer, Drawable>(layer, drawable)));
	}
	
	public void remove(int layer, Drawable drawable) {
		actionSyncMap.add(new Pair<Action, Object>(Action.REMOVE_FROM_LAYER, new Pair<Integer, Drawable>(layer, drawable)));
	}
	
	public void remove(int layer, int index) {
		actionSyncMap.add(new Pair<Action, Object>(Action.REMOVE_FROM_LAYER_INDEX, new Pair<Integer, Integer>(layer, index)));
	}
	
	public void remove(Drawable drawable) {
		actionSyncMap.add(new Pair<Action, Object>(Action.REMOVE_FROM_EVERY_LAYER, drawable));
	}
	
	
	
	@SuppressWarnings("unchecked")
	protected void update() {
		if(!actionSyncMap.isEmpty()) {
			if(!actionSyncMap.isEmpty()) {
				List<Pair<Action, Object>> rmv = new ArrayList<>();
				for (int i  = 0; i < actionSyncMap.size(); i++) {
					Pair<Action, Object> pair = actionSyncMap.get(i);
				    switch(pair.getKey()) {
					case ADD_TO_LAYER:
						Pair<Integer, Drawable> atl = (Pair<Integer, Drawable>) pair.getValue();
						layers.get(atl.getKey()).add(atl.getValue());
						break;
					case CREATE_LAYER:
						layers.add(new ArrayList<Drawable>());
						break;
					case DELETE_LAYER:
						layers.remove(layers.size() - 1);
						break;
					case REMOVE_FROM_EVERY_LAYER:
						for(ArrayList<Drawable> layer : layers) {
							layer.remove(pair.getValue());
						}
						break;
					case REMOVE_FROM_LAYER:
						Pair<Integer, Drawable> rfl = (Pair<Integer, Drawable>) pair.getValue();
						layers.get(rfl.getKey()).remove(rfl.getValue());
						break;
					case REMOVE_FROM_LAYER_INDEX:
						Pair<Integer, Integer> rfli = (Pair<Integer, Integer>) pair.getValue();
						layers.get(rfli.getKey()).remove(rfli.getValue().intValue());
						break;
					default:
						break;
				    }
				    rmv.add(pair);
				}
				actionSyncMap.removeAll(rmv);
			}
		}
	}

	public List<ArrayList<Drawable>> viewLayers() {
		return Collections.unmodifiableList(new CopyOnWriteArrayList<ArrayList<Drawable>>(layers));
	}
	
	protected ArrayList<ArrayList<Drawable>> getLayers() {
		return layers;
	}
	
}
