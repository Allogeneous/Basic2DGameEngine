package me.Allogeneous.render;

import java.awt.Graphics2D;
import java.util.ArrayList;

import me.Allogeneous.core.Universe;

public class Renderer {
	
	private Universe universe;
	private Camera camera;
	
	private LayerSet background;
	private LayerSet foreground;
	
	private int maxFps;
	private int currentFps;
	private int framesRenderedThisSecond;
	private long lastRenderTime;
	
	public Renderer(Universe universe, Camera camera, int maxFps) {
		this.universe = universe;
		this.camera = camera;
		
		this.background = new LayerSet();
		this.foreground = new LayerSet();
		
		this.maxFps = maxFps;
		this.currentFps = 0;
		this.framesRenderedThisSecond = 0;
		this.lastRenderTime = 0;
	}
	
	public void render(Graphics2D graphics) {
		camera.updateFocusePoint();
		graphics.transform(camera.getAfflineTransformation());
		paint(graphics);
	}
	
	private void paint(Graphics2D graphics) {
		background.update();
		for(ArrayList<Drawable> layer : background.getLayers()) {
			for(Drawable drawable : layer) {
				drawable.draw(graphics);
			}
		}

		for(Drawable physicsObjects : universe.viewPhysicsObjects()) {
			physicsObjects.draw(graphics);
		}

		foreground.update();
		for(ArrayList<Drawable> layer : foreground.getLayers()) {
			for(Drawable drawable : layer) {
				drawable.draw(graphics);
			}
		}
	}
	
	public Universe getUniverse() {
		return this.universe;
	}
	
	public int getMaxFps() {
		return maxFps;
	}
	
	public void setMaxFps(int maxFps) {
		this.maxFps = maxFps;
	}

	public LayerSet getBackground() {
		return this.background;
	}
	
	public LayerSet getForeground() {
		return this.foreground;
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public Camera getCamera() {
		return this.camera;
	}

	public int getCurrentFps() {
		return currentFps;
	}

	public void setCurrentFps(int currentFps) {
		this.currentFps = currentFps;
	}

	public long getLastRenderTime() {
		return lastRenderTime;
	}

	public void setLastRenderTime(long lastRenderTime) {
		this.lastRenderTime = lastRenderTime;
	}

	public int getFramesRenderedThisSecond() {
		return framesRenderedThisSecond;
	}

	public void setFramesRenderedThisSecond(int framesRenderedThisSecond) {
		this.framesRenderedThisSecond = framesRenderedThisSecond;
	}

	
	

}
