package me.Allogeneous.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import me.Allogeneous.math.Point;

public class GraphicsObject implements Drawable{
	
	private Point center;
	private Point spriteSheetCord;
	
	private SpriteSheet spriteSheet;
	private BufferedImage image;
	
	public GraphicsObject(SpriteSheet spriteSheet) {
		this.center = new Point(0, 0);
		this.spriteSheet = spriteSheet;
		this.spriteSheetCord = new Point(0, 0);
		this.image = this.spriteSheet.getSpite(this.spriteSheetCord);
	}
	
	public GraphicsObject(Point center, SpriteSheet spriteSheet) {
		this.center = center;
		this.spriteSheet = spriteSheet;
		this.spriteSheetCord = new Point(0, 0);
		this.image = this.spriteSheet.getSpite(this.spriteSheetCord);
	}
	
	public GraphicsObject(Point center, SpriteSheet spriteSheet, Point spriteSheetCord, double theta) {
		this.center = center;
		this.spriteSheet = spriteSheet;
		this.spriteSheetCord = spriteSheetCord;
		this.image = this.spriteSheet.getSpite(this.spriteSheetCord);	
	}
	
	@Override
	public void draw(Graphics2D graphics) {
		graphics.drawImage(image, GraphicsObject.getGenericTransform(this.center, image), null);
	}
	
	public void drawRelative(Point center, Graphics2D graphics, Point spriteSheetCord, double theta) {
		BufferedImage image = rotateRelative(spriteSheet.getSpite(spriteSheetCord), theta);
		graphics.drawImage(image, GraphicsObject.getGenericTransform(center, image), null);
	}
	
	public static AffineTransform getGenericTransform(Point center, BufferedImage bufferedImage) {
		AffineTransform at = new AffineTransform();
		at.scale(1, -1);
		at.translate(center.getIntX() - (bufferedImage.getWidth() / 2), -(center.getIntY() + (bufferedImage.getHeight() / 2)));
		return at;
	}
	
	public void rotate(double theta) {
        double sin = Math.abs(Math.sin(theta)), cos = Math.abs(Math.cos(theta));
        int w = image.getWidth();
        int h = image.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(theta, x, y);
        g2d.setTransform(at);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        
        this.image = rotated;
    }
	
	public BufferedImage rotateRelative(BufferedImage image, double theta) {
        double sin = Math.abs(Math.sin(theta)), cos = Math.abs(Math.cos(theta));
        int w = image.getWidth();
        int h = image.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(theta, x, y);
        g2d.setTransform(at);
        g2d.drawImage(image, 0, 0, null);
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();
        
        return rotated;
    }
	
	public SpriteSheet getSpriteSheet() {
		return this.spriteSheet;
	}
	
	public Point getSpriteSheetCord() {
		return this.spriteSheetCord;
	}
	
	public void setSpriteSheetCord(Point spriteSheetCord) {
		this.spriteSheetCord = spriteSheetCord;
		this.image = spriteSheet.getSpite(spriteSheetCord);
	}
	
	public Point getCenter() {
		return this.center;
	}
	
	public void setCenter(Point center) {
		this.center = center;
	}

	

}
