package me.Allogeneous.render;

import java.awt.Color;
import java.awt.Graphics2D;

import me.Allogeneous.math.Point;

public class Tint implements Drawable{
	
	private Color color;
	private Point start;
	private Point end;
	
	public Tint(Color color, Point start, Point end) {
		this.color = color;
		this.start = start;
		this.end = end;
	}

	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(color);
		graphics.fillRect(start.getIntX(), start.getIntY(), end.getIntX() - start.getIntX(), end.getIntY() - start.getIntY());
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

}
