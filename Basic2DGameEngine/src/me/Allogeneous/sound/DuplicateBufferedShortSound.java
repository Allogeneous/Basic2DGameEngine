package me.Allogeneous.sound;

import java.net.URL;

public class DuplicateBufferedShortSound{
	
	private final BufferedShortSound[] clipBuffer;
	private final URL resource;
	
	public DuplicateBufferedShortSound(URL resource, int amount) {    
		this.resource = resource;
		this.clipBuffer = new BufferedShortSound[amount];
			for(int i = 0; i < amount; i++) {
				this.clipBuffer[i] = new BufferedShortSound(resource);
			}
	}
	
	public BufferedShortSound getFirstNotBusy() {
		for(int i = 0; i < this.clipBuffer.length; i++) {
			if(!this.clipBuffer[i].isBusy()) {
				return this.clipBuffer[i];
			}
		}
		return null;
	}
	
	public BufferedShortSound getFirstBusy() {
		for(int i = 0; i < this.clipBuffer.length; i++) {
			if(this.clipBuffer[i].isBusy()) {
				return this.clipBuffer[i];
			}
		}
		return null;
	}
	
	public BufferedShortSound get(int index) {
		return clipBuffer[index];
	}

	public URL getResource() {
		return resource;
	}
}
