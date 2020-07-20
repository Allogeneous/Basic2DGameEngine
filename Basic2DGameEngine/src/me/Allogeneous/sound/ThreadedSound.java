package me.Allogeneous.sound;

import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

public class ThreadedSound {
	
	private static final int BUFFER_SIZE = 4096;

	private final URL resource;
	
	public ThreadedSound(URL resource) {
		this.resource = resource;
	}
	
	public void play(float volume) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource);
		 
		            AudioFormat format = audioStream.getFormat();
		 
		            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		 
		            SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
		 
		            audioLine.open(format);
		            
		            if (audioLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
		                FloatControl volumeC = (FloatControl) audioLine.getControl(FloatControl.Type.MASTER_GAIN);
		                volumeC.setValue(volume);
		            }
		 
		            audioLine.start();
		             
		            byte[] bytesBuffer = new byte[BUFFER_SIZE];
		            int bytesRead = -1;
		 
		            while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
		                audioLine.write(bytesBuffer, 0, bytesRead);
		            }
		             
		            audioLine.drain();
		            audioLine.close();
		            audioStream.close();
		             
		        } catch (Exception ex) {
		        	ex.printStackTrace();
		        }
				
			}
			
		}).start();
	}
	
}
