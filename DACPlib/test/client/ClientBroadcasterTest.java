package client;

import static org.junit.Assert.*;

import org.junit.Test;

import writer.DACPRequestGenerator;


public class ClientBroadcasterTest {

	@Test
	public void testPlay() {
		assertTrue(DACPRequestGenerator.play().equals("playpause"));
	}

	@Test
	public void testPause() {
		assertTrue(DACPRequestGenerator.pause().equals("pause"));
	}

	@Test
	public void testChangeVolumeHigh() {
		int f=255;
		assertTrue(DACPRequestGenerator.changeVolume(f).equals("setproperty?dmcp.volume=255"));
	}
	
	@Test
	public void testChangeVolumeLow() {
		int f=0;
		assertTrue(DACPRequestGenerator.changeVolume(f).equals("setproperty?dmcp.volume=0"));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume() {
		int f=-1;
		DACPRequestGenerator.changeVolume(f);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume2() {
		int f=256;
		DACPRequestGenerator.changeVolume(f);
	}
}
