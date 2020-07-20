package me.Allogeneous.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import me.Allogeneous.collision.CollisionData;
import me.Allogeneous.math.Point;
import me.Allogeneous.math.Vector;

public class ComplexShape extends Shape {

	private ArrayList<ConvexShape> convexShapes;
	
	public ComplexShape(ConvexShape... convexShapes) {
		this.convexShapes = new ArrayList<ConvexShape>(Arrays.asList(convexShapes));
		this.setColor(DEFAULT);
	}
	
	public ComplexShape(Color color, ConvexShape... convexShapes) {
		this.convexShapes = new ArrayList<ConvexShape>(Arrays.asList(convexShapes));
		this.setColor(color);
		for(ConvexShape shape : this.convexShapes) {
			shape.setColor(color);
		}
	}
	
	@Override
	public LineSegment[] getSides() {
		
		ArrayList<LineSegment> allSides = new ArrayList<>();
		
		for(ConvexShape shape : convexShapes) {
			LineSegment[] sides = shape.getSides();
			Collections.addAll(allSides, sides);
		}
		
		return allSides.toArray(new LineSegment[0]);
	}
	
	@Override
	public Point getCentroid() {
		double centerOfCentersX = 0, centerOfCentersY = 0;
		
		for(ConvexShape shape : convexShapes) {
			Point shapeCenter = shape.getCentroid();
			centerOfCentersX += shapeCenter.x;
			centerOfCentersY += shapeCenter.y;
		}
		
		centerOfCentersX /= convexShapes.size();
		centerOfCentersY /= convexShapes.size();
		
		return new Point(centerOfCentersX, centerOfCentersY);
	}
	
	@Override
	public void draw(Graphics2D g2) {
		for(ConvexShape shape : convexShapes) {
			shape.draw(g2);
		}
	}
	
	@Override
	public Vector[] getAxes() {
		ArrayList<Vector> axes = new ArrayList<>();
		for(ConvexShape shape : convexShapes) {
			Collections.addAll(axes, shape.getAxes());
		}
		return axes.toArray(new Vector[0]);
	}
	
	@Override
	public double[] projectOntoAxis(Vector axis) {	
		return convexShapes.get(0).projectOntoAxis(axis);
	}

	@Override
	public ArrayList<Point> getVertices() {
		ArrayList<Point> vertices = new ArrayList<>();
		for(ConvexShape shape : convexShapes) {
			vertices.addAll(shape.getVertices());
		}
		return vertices;
	}


	@Override
	public void moveBy(Point point) {
		for(ConvexShape shape : convexShapes) {
			shape.moveBy(point);
		}
	}


	@Override
	public void rotate(double theta) {
		Point center = this.getCentroid();
		for(ConvexShape shape : convexShapes) {
			shape.rotate(center, theta);
		}
	}
	
	@Override
	public void moveTo(Point center) {
		Point oldCenter = getCentroid();
		for(ConvexShape shape : convexShapes) {
			Point diff = shape.getCentroid().subtract(oldCenter);
			Point moveTo = center.add(diff);
			shape.moveTo(moveTo);
		}
	}
	
	public ArrayList<ConvexShape> getConvexShapes(){
		return this.convexShapes;
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
		for(ConvexShape shape : getConvexShapes()) {
			for(ConvexShape otherShape : other.getConvexShapes()) {
				CollisionData cd = shape.isIntersectingConvexShape(otherShape);
				if(cd.collision) {
					return cd;
				}
			}
		}
		return new CollisionData(null, false, Double.MAX_VALUE);
	}
	
	public CollisionData isIntersectingConvexShape(ConvexShape other) {
		for(ConvexShape shape : getConvexShapes()) {
			CollisionData cd = shape.isIntersectingConvexShape(other);
			if(cd.collision) {
				return cd;
			}
		}
		return new CollisionData(null, false, Double.MAX_VALUE);
	}

}
