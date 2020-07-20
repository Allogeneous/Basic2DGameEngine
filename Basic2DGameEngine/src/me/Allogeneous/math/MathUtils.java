package me.Allogeneous.math;

public class MathUtils {
	
	/**
	 * Squares whatever number is passed to it
	 * 
	 * @param n
	 * @return n^2
	 */
	public static double squared(double n) {
		return n * n;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return The largest of the two numbers
	 */
	public static double max(double x, double y) {
		return x <= y ? y : x;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return The smallest of the two numbers
	 */
	public static double min(double x, double y) {
		return x <= y ? x : y;
	}
	
	/**
	 * @param test - number to test
	 * @param min - low end of range
	 * @param max - high end of range
	 * @return true if test is greater than or equal to min and less than or equal to max, else false
	 */
	public static boolean fallsWithinRangeof(double test, double min, double max) {
		return(test >= min && test <= max);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param range - potential distance between x and y
	 * @return true if x and y are less than range away from each other
	 */
	public static boolean areClose(double x, double y, double range) {
		return x == y || Math.abs(x - y) < range;
	}
	

}
