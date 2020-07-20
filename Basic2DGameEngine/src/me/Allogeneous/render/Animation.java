package me.Allogeneous.render;

import java.awt.Graphics2D;

import me.Allogeneous.math.Point;

public class Animation implements Drawable{
	
	private final GraphicsObject graphicsObject;
	
	private long frameDisplayTime;
	private long currentDrawTime;
	private long lastFrameChangeTime;
	
	private boolean cycle;
	
	private Point currentPoint;
	
	private int endingFrame;
	private int startingFrame;
	
	public Animation(GraphicsObject graphicsObject, long frameDisplayTime, boolean cycle) {
		this.graphicsObject = graphicsObject;
		this.cycle = cycle;
		this.frameDisplayTime = Math.abs(frameDisplayTime);
		this.lastFrameChangeTime = 0;
		this.startingFrame = 0;
		this.endingFrame = getMaxFrame();
		setCurrentFrame(startingFrame);
	}
	
	public Animation(GraphicsObject graphicsObject, int startingFrame, long frameDisplayTime, boolean cycle) {
		this.graphicsObject = graphicsObject;
		this.cycle = cycle;
		this.frameDisplayTime = Math.abs(frameDisplayTime);
		this.lastFrameChangeTime = 0;
		this.startingFrame = startingFrame;
		this.endingFrame = getMaxFrame();
		if(this.startingFrame > endingFrame || this.startingFrame < 0) {
			this.startingFrame = 0;
		}
		setCurrentFrame(this.startingFrame);
	}
	
	public Animation(GraphicsObject graphicsObject, int startingFrame, int endingFrame, long frameDisplayTime, boolean cycle) {
		this.graphicsObject = graphicsObject;
		this.cycle = cycle;
		this.frameDisplayTime = Math.abs(frameDisplayTime);
		this.lastFrameChangeTime = 0;
		this.startingFrame = startingFrame;
		this.endingFrame = endingFrame;
		if(this.startingFrame > endingFrame || this.startingFrame < 0) {
			this.startingFrame = 0;
		}
		if(this.endingFrame > getMaxFrame() || this.endingFrame < 0) {
			this.endingFrame = getMaxFrame();
		}
		setCurrentFrame(this.startingFrame);
	}
	
	public void drawAt(Point center, Graphics2D graphics) {
		currentDrawTime = System.currentTimeMillis();
		if(this.lastFrameChangeTime == 0) {
			this.lastFrameChangeTime = currentDrawTime;
		}
		if(currentDrawTime - lastFrameChangeTime > frameDisplayTime) {
			setCurrentFrame(getNextFrame());
		}
		
		graphicsObject.setCenter(center);
		graphicsObject.setSpriteSheetCord(currentPoint);
		graphicsObject.draw(graphics);
	}
	
	@Override
	public void draw(Graphics2D graphics) {
		currentDrawTime = System.currentTimeMillis();
		if(this.lastFrameChangeTime == 0) {
			this.lastFrameChangeTime = currentDrawTime;
		}
		if(currentDrawTime - lastFrameChangeTime > frameDisplayTime) {
			setCurrentFrame(getNextFrame());
		}
		graphicsObject.draw(graphics);
	}
	
	public void setCurrentFrame(int frame) {
		if(frame >= 0 && frame <= getMaxFrame()) {
			int x = frame / (graphicsObject.getSpriteSheet().getMaxY() + 1);
			int y = frame / (graphicsObject.getSpriteSheet().getMaxX() + 1);
			
			this.currentPoint = new Point(x, y);
			this.lastFrameChangeTime = currentDrawTime;
		}
	}
	
	public void setStaringFrame(int startingFrame) {
		if(startingFrame <= endingFrame && this.startingFrame >= 0) {
			this.startingFrame = startingFrame;
		}
	}
	
	public void setEndingFrame(int endingFrame) {
		if(endingFrame <= getMaxFrame() && endingFrame >= 0) {
			this.endingFrame = endingFrame;
		}
	}
	
	public void setFrameDisplayTime(long frameDisplayTime) {
		this.frameDisplayTime = Math.abs(frameDisplayTime);
	}
	
	public void setCycle(boolean cycle) {
		this.cycle = cycle;
	}
	
	public int getMaxFrame() {
		return ((graphicsObject.getSpriteSheet().getMaxX() + 1) * (graphicsObject.getSpriteSheet().getMaxY() + 1)) - 1;
	}
	
	public int getCurrentFrame() {
		return ((currentPoint.getIntX() + 1) * (currentPoint.getIntY() + 1)) - 1;
	}
	
	public int getNextFrame() {
		int nextFrame = getCurrentFrame() + 1;
		if(cycle) {
			if(nextFrame > endingFrame) {
				return startingFrame;
			}
		}else {
			if(nextFrame > endingFrame) {
				return endingFrame;
			}
		}
		return nextFrame;
	}
	
	
	public Point getCurrentSpriteSheetPoint() {
		return this.currentPoint.clone();
	}
	
	public long getFrameDisplayTime() {
		return this.frameDisplayTime;
	}
	
	public int getStaringFrame() {
		return this.startingFrame;
	}
	
	public int getEndingFrame() {
		return this.endingFrame;
	}
	
	public boolean isCycle() {
		return this.cycle;
	}
	
	public GraphicsObject getGraphicsObject() {
		return this.graphicsObject;
	}
	
}
