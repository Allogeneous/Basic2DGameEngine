package me.Allogeneous.shape;

import java.awt.Color;
import java.awt.Graphics2D;

import me.Allogeneous.math.Point;
import me.Allogeneous.math.Vector;

public class Circle extends ConvexShape {
	
	public int radius;
	public Point rotationPoint;
	
	public Circle(Color color, Point center, int radius) {
		super(center, new Point(center.x + radius, center.y), new Point(center.x - radius, center.y), new Point(center.x, center.y + radius), new Point(center.x, center.y - radius));
		this.radius = radius;
		this.setColor(color);
		this.rotationPoint = new Point(center.x + radius, center.y);
	}
	
	public Circle(Point center, int radius) {
		super(center, new Point(center.x + radius, center.y), new Point(center.x - radius, center.y), new Point(center.x, center.y + radius), new Point(center.x, center.y - radius));
		this.radius = radius;
		this.setColor(DEFAULT);
		this.rotationPoint = new Point(center.x + radius, center.y);
	}
	
	@Override
	public void rotate(double theta) {
		rotationPoint = Point.rotate(theta, this.getCentroid(), this.rotationPoint);
	}
	
	
	public int getDiameter() {
		return radius * 2;
	}
	
	@Override
	public LineSegment[] getSides() {
		return null;
	}
	
	@Override
	public Point getCentroid() {
		return this.getVertices().get(0);
	}
	
	
	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(this.getColor());
		g2.drawOval(this.getCentroid().getIntX() - (int) radius, this.getCentroid().getIntY() - (int) radius, this.getDiameter(), this.getDiameter());
	}
	
	@Override
	public Vector[] getAxes() {
		return getAxes(this);
	}
	
	public Vector[] getAxes(ConvexShape shape) {
		Point center = getCentroid();
		Point closestPoint = shape.getVertices().get(0);
		double cdist = center.distance(closestPoint);
		
		for(int i = 1; i < shape.getVertices().size(); i++) {
			double dist = center.distance(shape.getVertices().get(i));
			if(dist < cdist) {
				cdist = dist;
				closestPoint = shape.getVertices().get(i);
			}
		}
		
		return new Vector[] {center.between(closestPoint)};
	}
	
}
