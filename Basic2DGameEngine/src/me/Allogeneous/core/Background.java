package me.Allogeneous.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;


/**
 * This is the main JComponent class that holds the whole game engine together.
 * This class is responsible for holding the scene and running the Renderer.
 * All add component listeners should be added to the instance of this class. 
 *
 */
@SuppressWarnings("serial")
public class Background extends JPanel implements Runnable{
	
	//Scene for the background
	private Scene scene;
	
	/** 
	 * Creates a background for a given Scene with a specified width and height.
	 * 
	 * @param scene - This is essentially the game or part of a game that you want to have running
	 * @param width - Screen width
	 * @param height - Screen height
	 */
	public Background(Scene scene, int width, int height) {
		this.setScene(scene);
		this.setLayout(null);
		this.setBackground(Color.BLACK);
		
		scene.getRenderer().getCamera().setScreenWidth(this.getWidth());
		scene.getRenderer().getCamera().setScreenHeight(this.getHeight());
		
		this.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent componentEvent) {
		    	scene.getRenderer().getCamera().setScreenWidth(Background.this.getWidth());
				scene.getRenderer().getCamera().setScreenHeight(Background.this.getHeight());
		    }
		});
	}

	
	/**
	 *Renders the next frame.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
	    Graphics2D g2 = (Graphics2D) g.create();
	    scene.getRenderer().render(g2);
	    
	    g2.dispose();
	    g.dispose();
	}
	
	/**
	 *This is the "graphics loop" of the game. It will run at the speed of the renderer's frame-rate.
	 */
	@Override
	public void run() {
		while(true) {	
			long now = System.currentTimeMillis();
			
			if(scene.getRenderer().getLastRenderTime() == 0) {
				scene.getRenderer().setLastRenderTime(now);
			}
			if(now - scene.getRenderer().getLastRenderTime() >= 1000) {
				scene.getRenderer().setLastRenderTime(now);
				scene.getRenderer().setCurrentFps(scene.getRenderer().getFramesRenderedThisSecond());
				scene.getRenderer().setFramesRenderedThisSecond(0);
			}
			this.repaint(0, 0, this.getWidth(), this.getHeight());
			scene.getRenderer().setFramesRenderedThisSecond(scene.getRenderer().getFramesRenderedThisSecond() + 1);
			if(scene.getRenderer().getMaxFps() > 0) {
				try {
					Thread.sleep(1000 / scene.getRenderer().getMaxFps());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * @return The background's current Scene.
	 */
	public Scene getScene() {
		return scene;
	}


	/**
	 * This method is experimental and relatively untested, it will be improved
	 * in later versions. It is supposed to change the background's scene to a new
	 * scene. 
	 * 
	 * @param scene - The new scene to be set to the background
	 */
	public void setScene(Scene scene) {
		this.scene = scene;
		
		scene.getRenderer().getCamera().setScreenWidth(this.getWidth());
		scene.getRenderer().getCamera().setScreenHeight(this.getHeight());
		
		this.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent componentEvent) {
		    	scene.getRenderer().getCamera().setScreenWidth(Background.this.getWidth());
				scene.getRenderer().getCamera().setScreenHeight(Background.this.getHeight());
		    }
		});
	}

	
}
