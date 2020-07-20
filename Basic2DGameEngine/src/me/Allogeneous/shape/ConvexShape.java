package me.Allogeneous.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import me.Allogeneous.collision.CollisionData;
import me.Allogeneous.math.Point;
import me.Allogeneous.math.Vector;

public class ConvexShape extends Shape {
	
	private ArrayList<Point> vertices;
	
	public ConvexShape(Point... vertices) {
		this.vertices = new ArrayList<Point>(Arrays.asList(vertices));
		this.setColor(DEFAULT);
	}
	
	public ConvexShape(Color color, Point... vertices) {
		this.vertices = new ArrayList<Point>(Arrays.asList(vertices));
		this.setColor(color);
	}
	
	@Override
	public LineSegment[] getSides() {
		LineSegment[] sides = new LineSegment[vertices.size()];
		
		for(int i = 1; i < vertices.size(); i ++) {
			sides[i - 1] = new LineSegment(getColor(), vertices.get(i - 1), vertices.get(i));
		}
		sides[sides.length - 1] = new LineSegment(getColor(), vertices.get(vertices.size() - 1), vertices.get(0));
		
		return sides;
	}
	
	public LineSegment getFirstIntersectingLineSegmentOf(Shape shape) {
		if(shape instanceof ConvexShape && this instanceof Circle) {
			for(LineSegment otherSide : shape.getSides()) {
				if(otherSide.intersectsCircle((Circle) this)) {
					return otherSide;
				}
			}
		}else if(shape instanceof ConvexShape && this instanceof ConvexShape) {
				for(LineSegment side : getSides()) {
					for(LineSegment otherSide : shape.getSides()) {
						if(side.intersectsLineSegment(otherSide)) {
							return otherSide;
						}
					}
				}
			}
		
		return null;
	}
	
	@Override
	public Point getCentroid() {
		
		double centerX = 0, centerY = 0;
		
		for(Point vertice : vertices) {
			centerX += vertice.x;
			centerY += vertice.y;
		}
		
		centerX = centerX / vertices.size();
		centerY = centerY / vertices.size();
		
		
		return new Point(centerX, centerY);
	}
	
	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(this.getColor());
		for(LineSegment side : getSides()) {
			g2.drawLine(side.one.getIntX(), side.one.getIntY(), side.two.getIntX(), side.two.getIntY()); 
		}
	}
	
	@Override
	public Vector[] getAxes() {
		Vector[] axes = new Vector[this.vertices.size()];
		int i = 0;
		for(LineSegment segment : getSides()) {
			axes[i] = segment.line.perpendicularClockwise();
			i++;
		}
		return axes;
	}
	
	@Override
	public double[] projectOntoAxis(Vector axis) {
		double min = axis.normalize().dot(vertices.get(0).toVector());
		double max = min;
		
		if(this instanceof Circle) {
			double rd = this.getCentroid().toVector().dot(axis.normalize());
	        return new double[] {rd - ((Circle) this).radius, rd + ((Circle) this).radius};
		}else {			
			for (int i = 1; i < vertices.size(); i++) {
				double p = axis.normalize().dot(vertices.get(i).toVector());
					if (p < min) {
						min = p;
					} else if (p > max) {
						max = p;
					}
			}
		}
		
		return new double[] {min, max};
	}
	
	@Override
	public ArrayList<Point> getVertices() {
		return this.vertices;
	}

	@Override
	public void moveBy(Point point) {
		for(int i = 0; i < vertices.size(); i++) {
			vertices.set(i, vertices.get(i).add(point));
		}
	}

	@Override
	public void rotate(double theta) {
		Point center = getCentroid();
		for(int i = 0; i < vertices.size(); i++) {
			vertices.set(i, Point.rotate(theta, center, vertices.get(i)));
		}
	}
	
	@Override
	public void moveTo(Point center) {
		Point oldCenter = getCentroid();
		for(int i = 0; i < vertices.size(); i++) {
			Point diff = vertices.get(i).subtract(oldCenter);
			vertices.set(i, center.add(diff));
		}
		
	}
	
	public void rotate(Point center, double theta) {
		for(int i = 0; i < vertices.size(); i++) {
			vertices.set(i, Point.rotate(theta, center, vertices.get(i)));
		}
	}
	
	/**
	 * Checks to see if the shape is intersecting another shape
	 * 
	 * @param shape - The shape that intersection or "collision" is being tested with
	 * @return - a CollisionData object that contains details about the collision between the two shapes
	 * 
	 * @see
	 * <br>
	 * This implementation is heavily based on the guide found here: http://www.dyn4j.org/2010/01/sat/
	 */
	@Override
	public CollisionData isIntersectingShape(Shape shape) {
		if(shape instanceof ConvexShape) {
			return isIntersectingConvexShape((ConvexShape) shape);
		}else {
			return isIntersectingComplexShape((ComplexShape) shape);
		}
	}
	
	public CollisionData isIntersectingComplexShape(ComplexShape other) {
		for(ConvexShape shape : other.getConvexShapes()) {
			CollisionData cd = this.isIntersectingConvexShape(shape);
			if(cd.collision == true) {
				return cd;
			}
		}
		return new CollisionData(null, false, Double.MAX_VALUE);
	}
	

	public CollisionData isIntersectingConvexShape(ConvexShape other) {
		Vector mtv = new Vector(0, 0);
		
		Vector[] ourAxes = null;
		if(this instanceof Circle) {
			ourAxes = ((Circle) this).getAxes(other);
		}else {
			ourAxes = this.getAxes();
		}
		
		Vector[] otherAxes = null;
		if(other instanceof Circle) {
			otherAxes = ((Circle) other).getAxes(this);
		}else {
			otherAxes = other.getAxes();
		}
		
		double minOverlap = Double.MAX_VALUE;
		
		for (int i = 0; i < ourAxes.length; i++) {
		  
		  double[] p1 = projectOntoAxis(ourAxes[i]);
		  double[] p2 = other.projectOntoAxis(ourAxes[i]);
		  
		  if(!(p1[0] <= p2[1] && p2[0] <= p1[1])) {
			  return new CollisionData(null, false, minOverlap);
		  }else {
			  double ol = Math.max(p1[0],p2[0]);
			  double oh = Math.min(p1[1],p2[1]);
			  
			  double overlap = oh - ol;
			  
			  if(overlap < minOverlap) {
				  mtv = ourAxes[i];
				  minOverlap = overlap;
			  }
		  }
		}

		for (int i = 0; i < otherAxes.length; i++) {
		  double[] p1 = projectOntoAxis(otherAxes[i]);
		  double[] p2 = other.projectOntoAxis(otherAxes[i]);
		  
		  if(!(p1[0] <= p2[1] && p2[0] <= p1[1])) {
			  return new CollisionData(null, false, minOverlap);
		  }else {
			  double ol = Math.max(p1[0],p2[0]);
			  double oh = Math.min(p1[1],p2[1]);
			  
			  double overlap = oh - ol;
			  
			  if(overlap < minOverlap) {
				  mtv = otherAxes[i];
				  minOverlap = overlap;
			  }
		  }
		}
		
		Vector between = getCentroid().between(other.getCentroid());
		
		if(mtv.dot(between) > 0){
			mtv = mtv.flipSigns();
		}
		
		return new CollisionData(mtv, true, minOverlap);
	}

	public static ConvexShape makeSquare(Point center, double sideLength) {
		Point[] points = new Point[4];
		
		double sideLength2 = sideLength / 2;
		
		points[0] = center.subtract(sideLength2, sideLength2);
		points[1] = center.subtract(sideLength2, -sideLength2);
		points[2] = center.add(sideLength2, sideLength2);
		points[3] = center.add(sideLength2, -sideLength2);
		
		return new ConvexShape(points);
	}
	
	public static ConvexShape makeRectangle(Point center, double width, double height) {
		Point[] points = new Point[4];
		
		double width2 = width / 2;
		double height2 = height / 2;
		
		points[0] = center.subtract(width2, height2);
		points[1] = center.subtract(width2, -height2);
		points[2] = center.add(width2, height2);
		points[3] = center.add(width2, -height2);
		
		return new ConvexShape(points);
	}
	
}
