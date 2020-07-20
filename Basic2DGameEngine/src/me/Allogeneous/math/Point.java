package me.Allogeneous.math;

import java.awt.Color;
import java.awt.Graphics2D;

import me.Allogeneous.render.Drawable;
import me.Allogeneous.render.Focuseable;

public class Point implements Drawable, Focuseable{
	
	public volatile static Color POINT_COLOR = Color.GREEN;
	
	public double x, y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point add(double x, double y) {
		return new Point(this.x + x, this.y + y);
	}
	
	public Point subtract(double x, double y) {
		return new Point(this.x - x, this.y - y);
	}
	
	public Point add(Point point) {
		return new Point(this.x + point.x, this.y + point.y);
	}
	
	public Point subtract(Point point) {
		return new Point(this.x - point.x, this.y - point.y);
	}
	
	public double distance(Point to) {
		return new Vector(this, to).magnitude();
    }
	
	public Vector between(Point point) {
		return new Vector(this, point);
	}
	
	public int getIntX() {
		return (int) this.x;
	}
	
	public int getIntY() {
		return (int) this.y;
	}
	
	public Vector toVector() {
		return new Vector(this.x, this.y);
	}
	
	public boolean intEquals(Point point) {
		return this.getIntX() == point.getIntX() && this.getIntY() == point.getIntY();
	}
	
	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(POINT_COLOR);
		graphics.fillOval(this.getIntX() - (int) 2, this.getIntY() - (int) 2, 4, 4);
	}
	
	public static Point rotate(double theta, Point center, Point point) {
		double x = center.x + (point.x - center.x) * Math.cos(theta) - (point.y - center.y) * Math.sin(theta);
		double y = center.y + (point.x - center.x) * Math.sin(theta) + (point.y - center.y) * Math.cos(theta);
		return new Point(x, y);
	}
	
	public static Point midPoint(Point one, Point two) {
		return new Point((one.x + two.x) / 2, (one.y + two.y) / 2);
	}
	
	public static Point fromAwtPoint(java.awt.Point awtPoint, int height) {
		return new Point(awtPoint.getX(), ((double) height) - awtPoint.getY());
	}
	
	
	@Override
	public Point clone() {
		return new Point(this.x, this.y);
	}
	
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

	@Override
	public Point getFocusePoint() {
		return this;
	}

}
