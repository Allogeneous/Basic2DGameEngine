package me.Allogeneous.core;

import me.Allogeneous.render.Renderer;

public class Scene {
	
	//The scene's renderer
	private final Renderer renderer;
	
	/**
	 * Crates a new Scene with a given Renderer.
	 * @param renderer
	 */
	public Scene(Renderer renderer) {
		this.renderer = renderer;
		Thread t = new Thread(renderer.getUniverse());
		t.start();
	}

	/**
	 * @return The scene's renderer.
	 */
	public Renderer getRenderer() {
		return renderer;
	}
}
