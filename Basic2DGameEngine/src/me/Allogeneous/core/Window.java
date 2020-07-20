package me.Allogeneous.core;

import javax.swing.JFrame;

/**
 * This class is a wrapper for creating a new JFrame using Swing, but allows for the process to be simplified using the Scene class.
 * This class is the window that will display the game.
 *
 */
@SuppressWarnings("serial")
public class Window extends JFrame{
	
	//JPanel associated with the game engine
	private Background background;
	
	/**
	 * This is how you start and display every game made with the game engine.
	 * 
	 * @param title - Window title, for example, the name in the top left corner of the window container on Windows
	 * @param startingScene - The Scene that will begin running when the window is created
	 * @param width - Window width
	 * @param height - Window height
	 */
	public Window(String title, Scene startingScene, int width, int height) {
		this.setTitle(title);
		this.setSize((int)width, (int)(height));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.background = new Background(startingScene, this.getContentPane().getSize().width, this.getContentPane().getSize().height);
		this.add(background);
		Thread t = new Thread(this.background);
		t.start();
		this.revalidate();
	}
	
	
	/**
	 * This returns the Background JPanel associated with the game engine. Call this method to add component listers
	 * if so desired.
	 * 
	 * @return The Background JPanel that contains the Renderer and Universe of the game
	 */
	public Background getBackgroundPanel() {
		return this.background;
	}
	
}
