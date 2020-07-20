package me.Allogeneous.sound;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class BufferedShortSound {
	
	private volatile Clip clip;
	
	private volatile boolean busy;
	private volatile boolean paused;

	private int pauseFrame;
	private boolean looping;
	
	public BufferedShortSound(URL resource) {
		this.busy = true;
		this.paused = false;
		this.pauseFrame = 0;
		this.looping = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedShortSound.this.clip = load(resource);
				BufferedShortSound.this.clip.addLineListener(new LineListener(){
		            @Override
		            public void update(LineEvent event) {
		                if (event.getType() == LineEvent.Type.STOP){
		                	if(!BufferedShortSound.this.paused) {
		                		BufferedShortSound.this.busy = false;
		                		clip.setFramePosition(0);
		                	}
		                }

		            }

		        });
				BufferedShortSound.this.busy = false;
			}
			
		}).start();
	}
	
	private Clip load(URL resource) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			audioInputStream.close();
			return clip;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public boolean isBusy() {
		return this.busy;
	}
	
	public void start() {
		if(!clip.isActive()) {
			clip.setFramePosition(0);
			busy = true;
			looping = false;
			pauseFrame = 0;
			clip.start();
		}
	}
	
	public void loop() {
		if(!clip.isActive()) {
			busy = true;
			looping = true;
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void pause() {
		if(clip.isActive()) {
			this.paused = true;
			this.pauseFrame = clip.getFramePosition();
			clip.stop();
		}
	}
	
	public void end() {
		if(clip.isActive()) {
			this.paused = false;
			this.pauseFrame = 0;
			clip.stop();
		}
	}
	
	public void resume() {
		if(!clip.isActive()) {
			this.paused = false;
			if(looping) {
				loop();
			}else {
				if(!clip.isActive()) {
					clip.setFramePosition(pauseFrame);
					busy = true;
					looping = false;
					clip.start();
				}
			}
		}
	}
	
	public int getFrame() {
		if(clip == null || !clip.isActive()) {
			return pauseFrame;
		}
		return clip.getFramePosition();
	}
	
	public float getVolume() {
		FloatControl volumeC = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	    return volumeC.getValue();
	}

	public void setVolume(float volume) {
		FloatControl volumeC = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volumeC.setValue(volume);
	}

}
