package me.Allogeneous.render;

import java.awt.Color;
import java.awt.Graphics2D;

public class CameraTint extends Tint{

	private Camera camera;
	
	public CameraTint(Camera camera, Color color) {
		super(color, null, null);
		this.setCamera(camera);
		this.setStart(camera.getBottomLeftCorner());
		this.setEnd(camera.getTopRightCorner());
	}

	@Override
	public void draw(Graphics2D graphics) {
		this.setStart(camera.getBottomLeftCorner());
		this.setEnd(camera.getTopRightCorner());
		super.draw(graphics);
	}
	
	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

}
