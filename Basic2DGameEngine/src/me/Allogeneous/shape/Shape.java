package me.Allogeneous.shape;

import java.awt.Color;
import java.util.ArrayList;

import me.Allogeneous.collision.CollisionData;
import me.Allogeneous.math.Point;
import me.Allogeneous.math.Vector;
import me.Allogeneous.render.Drawable;

public abstract class Shape implements Drawable {
	
	public static Color DEFAULT = Color.RED;
	
	private Color color;
	
	public abstract Point getCentroid();
	public abstract LineSegment[] getSides();
	public abstract ArrayList<Point> getVertices();
	
	public abstract void moveTo(Point center);
	public abstract void moveBy(Point point);
	public abstract void rotate(double theta);
	
	public abstract Vector[] getAxes();
	public abstract double[] projectOntoAxis(Vector axis);
	
	public abstract CollisionData isIntersectingShape(Shape shape);
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
}

