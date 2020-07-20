package me.Allogeneous.math;

public class Vector {
	
	public static final Vector ZERO = new Vector(0, 0);
	
	public static final Vector UP = new Vector(0, 1);
	public static final Vector DOWN = new Vector(0, -1);
	public static final Vector LEFT = new Vector(-1, 0);
	public static final Vector RIGHT = new Vector(1, 0);
	
	public static final Vector UP_LEFT = new Vector(-1, 1);
	public static final Vector UP_RIGHT = new Vector(1, 1);
	public static final Vector DOWN_LEFT = new Vector(-1, -1);
	public static final Vector DOWN_RIGHT = new Vector(1, -1);
	
	public double i, j;
	
	public Vector() {
		this.i = 0;
		this.j = 0;
	}
	
	public Vector(double i, double j) {
		this.i = i;
		this.j = j;
	}
	
	public Vector(Point one, Point two) {
		this.i = two.x - one.x;
		this.j = two.y - one.y;
	}
	
	public Vector add(Vector vector) {
		return new Vector(this.i + vector.i, this.j + vector.j);
	}
	
	public Vector subtract(Vector vector) {
		return new Vector(this.i - vector.i, this.j - vector.j);
	}
	
	public double magnitude() {
		return Math.sqrt((i * i) + (j * j));
	}
	
	public double getDirection() {
		return Math.atan2(j, i);
	}
	
	public Vector normalize() {
		double magnitude = magnitude();
		return new Vector(this.i / magnitude, this.j / magnitude);
	}
	
	public Vector flipSigns() {
		return new Vector(-this.i, -this.j);
	}
	
	public double dot(Vector vector) {
		return (this.i * vector.i) + (this.j * vector.j);
	}
	
	public double cross(Vector vector) {
		return (this.i * vector.j) - (this.j * vector.i);
	}
	
	public Vector multiply(Vector vector) {
		return new Vector(this.i * vector.i, this.j * vector.j);
	}
	
	public Vector multiply(double scalar) {
		return new Vector(this.i * scalar, this.j * scalar);	
	}
	
	public Vector divide(double scalar) {
		return new Vector(this.i / scalar, this.j / scalar);
	}
	
	public Vector rotate(double theta) {
		double sin = Math.sin(theta);
		double cos = Math.cos(theta);
		return new Vector(this.i * cos + this.j * sin, this.j * cos + this.i * sin);
	}
	
	public Vector perpendicularClockwise() {
		return new Vector(this.j, -this.i);
	}
	
	public Vector perpendicularCounterClockwise() {
		return new Vector(-this.j, this.i);
	}
	
	public Vector abs() {
		return new Vector(Math.abs(this.i), Math.abs(this.j));
	}
	
	public Vector increaseMagnitude(Vector vector) {
		Vector nv = Vector.matchSigns(this, vector);
		return new Vector(this.i - nv.i, this.j - nv.j);
	}
	
	public Vector decreaseMagnitude(Vector vector) {
		Vector nv = Vector.matchSigns(this, vector);
		return new Vector(this.i - nv.i, this.j - nv.j);
	}
	
	public Point toPoint() {
		return new Point(this.i, this.j);
	}
	
	public static Vector movingTowards(Point start, Point destination, double speed) {
		return start.between(destination).normalize().multiply(speed);
	}
	
	public static Vector projectAontoB(Vector A, Vector B) {
		double scalar = A.dot(B) / B.dot(B);
		return B.multiply(scalar);
	}
	
	public static Vector matchSigns(Vector A, Vector B) {
		double ni = B.i;
		double nj = B.j;
		
		if((A.i < 0 && ni > 0) || (A.i > 0 && ni < 0)) {
			ni = -ni;
		}
		
		if((A.j < 0 && nj > 0) || (A.j > 0 && nj < 0)) {
			nj = -nj;
		}
		
		return new Vector(ni, nj);
	}
	
	@Override
	public String toString() {
		return "<" + this.i + ", " + this.j + ">";
	}
	
	@Override
	public Vector clone() {
		return new Vector(this.i, this.j);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object != null && object instanceof Vector) {
			Vector other = (Vector) object;
			if(this.i == other.i && this.j == other.j) {
				return true;
			}
		}
		return false;
	}
	
}
