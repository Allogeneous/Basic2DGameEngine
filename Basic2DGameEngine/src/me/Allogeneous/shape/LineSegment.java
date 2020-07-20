package me.Allogeneous.shape;

import java.awt.Color;
import java.awt.Graphics2D;

import me.Allogeneous.math.Point;
import me.Allogeneous.math.Vector;
import me.Allogeneous.render.Drawable;

public class LineSegment implements Cloneable, Drawable {
	
	public static Color DEFAULT = Color.RED;
	
	private static enum Side{COUNTERCLOCKWISE, CLOCKWISE};
	
	public Color color;
	public Point one, two;
	public Vector line;
	
	public LineSegment(Point one, Point two) {
		this.one = one;
		this.two = two;
		this.line = new Vector(one, two);
		this.color = DEFAULT;
	}
	
	public LineSegment(Color color, Point one, Point two) {
		this.one = one;
		this.two = two;
		this.line = new Vector(one, two);
		this.color = color;
	}
	
	public Vector getCollisionVector(Point collidingCenter) {
		if(getCollisionSide(collidingCenter) == Side.COUNTERCLOCKWISE) {
			return line.perpendicularCounterClockwise();
		}
		return line.perpendicularClockwise();
	}
	
	
	public Side getCollisionSide(Point collidingCenter) {
		double test = ((two.x - one.x)*(collidingCenter.y - one.y)) - ((collidingCenter.x - one.x)*(two.y - one.y));
		if(test >= 0) {
			return Side.COUNTERCLOCKWISE;
		}
		return Side.CLOCKWISE;
	}
	
	public Point getMidPoint(){
		return Point.midPoint(one, two);
	}
	
	public double getSlope() {
		return (this.two.y - this.one.y) / (this.two.x - this.two.y);
	}
	
	public boolean isParellelTo(LineSegment segment, double tolerance) {
		return this.getSlope() - segment.getSlope() <= tolerance;
	}
	
	public boolean isParellelTo(LineSegment segment) {
		return this.getSlope() == segment.getSlope();
	}
	
	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(this.color);
		graphics.drawLine((int) one.x, (int) one.y, (int) two.x, (int) two.y); 
	}
	
	public boolean intersectsLineSegment(LineSegment line) {
		Vector CmP = new Vector(this.one, line.one);
		Vector r = this.line;
		Vector s = line.line;
 
		double CmPxr = CmP.cross(r);
		double CmPxs = CmP.cross(s);
		double rxs = r.cross(s);
 
		if (CmPxr == 0) {
			return ((line.one.x - this.one.x < 0f) != (line.one.x - this.two.x < 0f))
				|| ((line.one.y - this.one.y < 0f) != (line.one.y - this.two.y < 0f));
		}
 
		if (rxs == 0) {
			return false; 
		}
 
		double rxsr = 1 / rxs;
		double t = CmPxs * rxsr;
		double u = CmPxr * rxsr;
		
		
		return (t >= 0) && (t <= 1) && (u >= 0) && (u <= 1);
	}
	

	public boolean intersectsCircle(Circle circle) {
		double oneX = this.one.x - circle.getCentroid().x;
		double oneY = this.one.y - circle.getCentroid().y;
		double twoX = this.two.x - circle.getCentroid().x;
		double twoY = this.two.y - circle.getCentroid().y;
		
		double a = Math.pow((twoX - oneX), 2) + Math.pow((twoY -oneY), 2);
		double b = 2*(oneX*(twoX - oneX) + oneY*(twoY - oneY));
		double c = Math.pow(oneX, 2) + Math.pow(oneY, 2) - Math.pow(circle.radius, 2);
		double disc = Math.pow(b, 2) - 4*a*c;
		if(disc <= 0) {
			return false;
		}
		double sqrtdisc = Math.sqrt(disc);
		double t1 = (-b + sqrtdisc)/(2*a);
		double t2 = (-b - sqrtdisc)/(2*a);
		if((0 < t1 && t1 < 1) || (0 < t2 && t2 < 1)) {
			return true;
		}
		return false;
	}

	
	@Override
	public String toString() {
		return "Point 1: (" + one.x + ", " + one.y + ") Point 2: (" + two.x + ", " + two.y + ")"; 
	}
	
	@Override
	public LineSegment clone() {
		return new LineSegment(this.color, this.one, this.two);
	}
	
	


}
