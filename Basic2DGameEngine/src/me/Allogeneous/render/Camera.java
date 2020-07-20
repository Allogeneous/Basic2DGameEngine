package me.Allogeneous.render;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;

import me.Allogeneous.math.Point;

public class Camera {
	
	public static final double DEFAULT_ZOOM_FACTOR = 1;
	public static final double DEFAULT_ZOOM_INCREMENT = 1.5;
	public static final double DEFAULT_MAX_ZOOM = Math.pow(DEFAULT_ZOOM_INCREMENT, 5);
	public static final double DEFAULT_MIN_ZOOM = Math.pow(DEFAULT_ZOOM_INCREMENT, -5);
	
	public static double defaultXResolution = 1920;
	public static double defaultYResolution = 1080;
	
	private Focuseable focuse;
	
	private Point focusePoint;
	private Point specificPoint;
	
	private Point minFocusePoint;
	private Point maxFocusePoint;
	
	private boolean freeMove;
	
	private final double origionalZoomFactor;
	
	private double zoomFactor;
	private double zoomIncrement;
	
	private double maxZoom;
	private double minZoom;
	
	private int screenWidth;
	private int screenHeight;
	
	
	
	
	public Camera(Focuseable focuse, Point minFocusePoint, Point maxFocusePoint, double zoomFactor, double zoomIncrement, double minZoom, double maxZoom) {
		this.focuse = focuse;
		this.minFocusePoint = minFocusePoint;
		this.maxFocusePoint = maxFocusePoint;
		this.focusePoint = focuse.getFocusePoint();
		this.zoomFactor = zoomFactor;
		this.origionalZoomFactor = zoomFactor;
		this.zoomIncrement = zoomIncrement;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}
	
	public Camera(Focuseable focuse, double zoomFactor, double zoomIncrement, double minZoom, double maxZoom) {
		this.focuse = focuse;
		this.focusePoint = focuse.getFocusePoint();
		this.zoomFactor = zoomFactor;
		this.origionalZoomFactor = zoomFactor;
		this.zoomIncrement = zoomIncrement;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.freeMove = true;
	}
	
	public Camera(Focuseable focuse, Point minFocusePoint, Point maxFocusePoint) {
		this.focuse = focuse;
		this.minFocusePoint = minFocusePoint;
		this.maxFocusePoint = maxFocusePoint;
		this.focusePoint = focuse.getFocusePoint();
		this.zoomFactor = DEFAULT_ZOOM_FACTOR;
		this.origionalZoomFactor = zoomFactor;
		this.zoomIncrement = DEFAULT_ZOOM_INCREMENT;
		this.minZoom = DEFAULT_MIN_ZOOM;
		this.maxZoom = DEFAULT_MAX_ZOOM;
	}
	
	public Camera(Focuseable focuse) {
		this.focuse = focuse;
		this.focusePoint = focuse.getFocusePoint();
		this.zoomFactor = DEFAULT_ZOOM_FACTOR;
		this.origionalZoomFactor = zoomFactor;
		this.zoomIncrement = DEFAULT_ZOOM_INCREMENT;
		this.minZoom = DEFAULT_MIN_ZOOM;
		this.maxZoom = DEFAULT_MAX_ZOOM;
		this.freeMove = true;
	}
	
	
	public void zoomIn() {
		if(zoomFactor * zoomIncrement <= maxZoom) {
			zoomFactor *= zoomIncrement;
		}
	}
	
	public void zoomOut() {
		if(zoomFactor * zoomIncrement >= minZoom) {
			zoomFactor /= zoomIncrement;
		}
	}
	
	public void resetZoom() {
		zoomFactor = origionalZoomFactor;
	}
	
	public void updateFocusePoint() {
		if(freeMove) {
			this.focusePoint = this.focuse.getFocusePoint();
			return;
		}
		
		Point centroid = this.focuse.getFocusePoint();
		if(centroid.getIntX() > this.minFocusePoint.getIntX()  && centroid.getIntY() > this.minFocusePoint.getIntY() && centroid.getIntX() < this.maxFocusePoint.getIntX()  && centroid.getIntY() < this.maxFocusePoint.getIntY()) {
			this.focusePoint = centroid;
		}else if(centroid.getIntX() > this.minFocusePoint.getIntX() && centroid.getIntX() < this.maxFocusePoint.getIntX()) {
			this.focusePoint = new Point(centroid.x, focusePoint.y);
		}else if(centroid.getIntY() > this.minFocusePoint.getIntY() && centroid.getIntY() < this.maxFocusePoint.getIntY()) {
			this.focusePoint = new Point(focusePoint.x, centroid.y);
		}
	}
	
	public AffineTransform getAfflineTransformation() {
		AffineTransform transformation = AffineTransform.getTranslateInstance(0, screenHeight);
		
		transformation.scale(1, -1);
		transformation.translate(screenWidth / 2, screenHeight / 2);
		transformation.scale(zoomFactor, zoomFactor);
		transformation.translate(-(focusePoint.getIntX()), -(focusePoint.getIntY()));
		
		return transformation;
	}
	
	public Point convertAwtPointOnScreenToPointRelativeToFocuse(java.awt.Point point) {
		Point center =  new Point(this.getScreenWidth() / 2, this.getScreenHeight() / 2);
		Point newPoint = new Point(point.x, this.getScreenHeight() - point.y);
		Point sub = newPoint.subtract(center);
		double x = (sub.x / zoomFactor);
		double y = (sub.y / zoomFactor);
		return focusePoint.add(new Point(x, y));
	}
	
	public Point convertPointOnScreenToPointRelativeToFocuse(Point point) {
		Point center =  new Point(this.getScreenWidth() / 2, this.getScreenHeight() / 2);
		Point sub = point.subtract(center);
		double x = (sub.x / zoomFactor);
		double y = (sub.y / zoomFactor);
		return focusePoint.add(new Point(x, y));
	}
	
	public Point getBottomLeftCorner() {
		double x = (this.getScreenWidth() / zoomFactor / 2);
		double y = (this.getScreenHeight() / zoomFactor / 2);
		return focusePoint.subtract(new Point(x, y));
	}
	
	public Point getTopRightCorner() {
		double x = (this.getScreenWidth() / zoomFactor  / 2);
		double y = (this.getScreenHeight() / zoomFactor / 2);
		return focusePoint.add(new Point(x, y));
	}
	
	public Focuseable getFocuse() {
		return focuse;
	}

	public void setFocuse(Focuseable focuse) {
		this.focusePoint = focuse.getFocusePoint();
		this.focuse = focuse;
	}
	
	public Point getFocusePoint() {
		return this.focusePoint;
	}

	public double getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(double maxZoom) {
		this.maxZoom = maxZoom;
	}

	public double getMinZoom() {
		return minZoom;
	}

	public void setMinZoom(double minZoom) {
		this.minZoom = minZoom;
	}

	public double getOrigionalZoomFactor() {
		return origionalZoomFactor;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public double getZoomIncrement() {
		return zoomIncrement;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public Point getSpecificPoint() {
		return specificPoint;
	}

	public void setSpecificPoint(Point specificPoint) {
		this.specificPoint = specificPoint;
	}

	public Point getMinFocusePoint() {
		return minFocusePoint;
	}

	public void setMinFocusePoint(Point minFocusePoint) {
		this.minFocusePoint = minFocusePoint;
	}

	public Point getMaxFocusePoint() {
		return maxFocusePoint;
	}

	public void setMaxFocusePoint(Point maxFocusePoint) {
		this.maxFocusePoint = maxFocusePoint;
	}

	public boolean isFreeMove() {
		return freeMove;
	}

	public void setFreeMove(boolean freeMove) {
		this.freeMove = freeMove;
	}

	public Point getScreenCenter() {
		return new Point(this.getScreenWidth() / 2, this.getScreenHeight() / 2);
	}
	
	public static Point getScreenResolution() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		return new Point(width, height);
	}
	
	public static Point getScreenResolutionRatio() {
		double rX = Camera.getScreenResolution().x / Camera.defaultXResolution;
		double rY = Camera.getScreenResolution().y / Camera.defaultYResolution;
		return new Point(rX, rY);
	}
	
	/*public void focuseOnOrigionalWindowCenterPoint() {
		this.focuseOnOrigionalWindowCenterPoint = true;
	}
	
	public boolean isFocusedOnOrigionalWindowCenterPoint() {
		return this.focuseOnOrigionalWindowCenterPoint;
	}*/
	

}
