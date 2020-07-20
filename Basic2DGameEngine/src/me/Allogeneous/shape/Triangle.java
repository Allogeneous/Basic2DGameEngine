package me.Allogeneous.shape;

import me.Allogeneous.math.Point;

public class Triangle extends ConvexShape{
	
	public Triangle(Point a, Point b, Point c) throws InvalidTriangleException {
		super(new Point[]{a, b, c});

		if(!Triangle.isValidTriangle(a, b, c)) {
			throw new InvalidTriangleException("Not a valid triangle!");
		}
	}
	
	public static boolean isValidTriangle(Point a, Point b, Point c) {
		double ab = a.distance(b);
		double bc = b.distance(c);
		double ca = c.distance(a);
		
		if(ab + bc > ca && ab + ca > bc && bc + ca > ab) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("serial")
	public class InvalidTriangleException extends Exception{
		public InvalidTriangleException(String errorMessage) {
			super(errorMessage);
		}
	}

	
}
