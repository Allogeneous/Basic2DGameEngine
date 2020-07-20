package me.Allogeneous.pathfinding;

import java.util.Comparator;

public class AStarNodeComparator implements Comparator<AStarNode>{

	@Override
	public int compare(AStarNode one, AStarNode two) {
		int compare = Integer.compare(one.getfCost(), two.getfCost());
		if(compare == 0) {
			compare = Integer.compare(one.gethCost(), two.gethCost());
		}
		return compare;
	}

}
