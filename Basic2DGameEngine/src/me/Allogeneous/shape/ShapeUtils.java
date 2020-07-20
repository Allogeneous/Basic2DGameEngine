package me.Allogeneous.shape;

import java.util.List;

import me.Allogeneous.math.Point;

public class ShapeUtils {
	
	public static Point getPointWithHighestXValue(List<Point> points) {
		
		Point hp = points.get(0);
		
		for(Point p : points) {
			if(p.x > hp.x) {
				hp = p;
			}
		}
		
		return hp;
		
	}
	
	public static Point getPointWithHighestYValue(List<Point> points) {
		
		Point hp = points.get(0);
		
		for(Point p : points) {
			if(p.y > hp.y) {
				hp = p;
			}
		}
		
		return hp;
		
	}
	
	public static Point getPointWithLowestXValue(List<Point> points) {
		
		Point hp = points.get(0);
		
		for(Point p : points) {
			if(p.x < hp.x) {
				hp = p;
			}
		}
		
		return hp;
		
	}
	
	public static Point getPointWithLowestYValue(List<Point> points) {
		
		Point hp = points.get(0);
		
		for(Point p : points) {
			if(p.y < hp.y) {
				hp = p;
			}
		}
		
		return hp;
		
	}

}
