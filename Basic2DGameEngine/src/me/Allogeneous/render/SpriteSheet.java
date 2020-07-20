package me.Allogeneous.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import me.Allogeneous.math.Point;

public class SpriteSheet {
	
	private BufferedImage image;
	
	private int spriteWidth;
	private int spriteHeight;
	
	private final int maxX;
	private final int maxY;

	public SpriteSheet(BufferedImage image, int spriteWidth, int spriteHeight) {
		this.spriteHeight = spriteHeight;
		this.spriteWidth = spriteWidth;
		
		this.image = image;
		
		maxX = (image.getWidth() / this.spriteWidth) - 1;
		maxY = (image.getHeight() / this.spriteHeight) - 1;
	}
	
	
	public SpriteSheet(URL resource, int spriteWidth, int spriteHeight) {
		try {
			image = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.spriteHeight = spriteHeight;
		this.spriteWidth = spriteWidth;
		
		maxX = (image.getWidth() / this.spriteWidth) - 1;
		maxY = (image.getHeight() / this.spriteHeight) - 1;
	}
	
	
	public SpriteSheet(BufferedImage image) {
		this.spriteHeight = image.getHeight();
		this.spriteWidth = image.getWidth();
		
		this.image = image;
		
		maxX = (image.getWidth() / this.spriteWidth) - 1;
		maxY = (image.getHeight() / this.spriteHeight) - 1;
	}
	
	
	public SpriteSheet(URL resource) {
		try {
			image = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.spriteHeight = image.getHeight();
		this.spriteWidth = image.getWidth();
		
		maxX = (image.getWidth() / this.spriteWidth) - 1;
		maxY = (image.getHeight() / this.spriteHeight) - 1;
	}
	
	
	public BufferedImage getSprite(int topLeftPixel, int topRightPixel) {
		return image.getSubimage(topLeftPixel, topRightPixel, spriteWidth, spriteHeight);
	}
	
	public BufferedImage getSpite(Point sheetCords) {
		return image.getSubimage(spriteWidth * sheetCords.getIntX(), spriteHeight * sheetCords.getIntY(), spriteWidth, spriteHeight);
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}
	
}
