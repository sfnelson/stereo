package controller;

import interfaces.DJInterface;

import org.junit.Before;
import org.junit.Test;

import player.Controller;


public class ControllerTest {
	DJInterface c;

	@Before
	public void testInstantiation(){
		c = new Controller("fiebigs", 3689);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void volumeSetNegative(){
		c.setVolume(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void volumeSetGreaterThan100(){
		c.setVolume(101);
	}
	
	@Test
	public void volumeAcceptableValue(){
		c.setVolume(0);
		c.setVolume(40);
		c.setVolume(100);
	}
	
	@Test
	public void trackPlayed() {
		c.play();
	}
	
	@Test
	public void trackPaused() {
		c.pause();
	}
	
}
