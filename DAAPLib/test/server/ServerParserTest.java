package server;

import org.junit.Test;

import reader.DACPRequestParser;


public class ServerParserTest {

	@Test
	public void testParsePlay() {
		//assertTrue(DACPRequestParser.parse("GET ctrl-int/1/playpause HTTP/1.1") instanceof DACPPlay);
	}
	
	@Test
	public void testParsePause() {
		//assertTrue(DACPRequestParser.parse("GET ctrl-int/1/pause HTTP/1.1") instanceof DACPPause);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidCommand() {
		DACPRequestParser.parse("GET ctrl-int/1/puase HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidControl() {
		DACPRequestParser.parse("GET ctrl-itn/1/pause HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidRequestFormat() {
		DACPRequestParser.parse("GET ctrl-int/1/pause");
	}
	
	@Test 
	public void testVolume(){
		//assertTrue(DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=0&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		//assertTrue(DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=100&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		//assertTrue(DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=255&felix=cool HTTP/1.1") instanceof DACPSetVolume);
		
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume1() {
		DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=-1&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume2() {
		DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=-100&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume3() {
		DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=256&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidNumberVolume4() {
		DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=10000&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidVolume() {
		DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcp.volume=abc&felix=cool HTTP/1.1");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidParameters() {
		DACPRequestParser.parse("GET ctrl-int/1/setproperty?dmcpvolume=abc&felix=cool HTTP/1.1");
	}

}
