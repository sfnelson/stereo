package daap;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;


public class TrackTest {
	
	DAAPTrack t;
	
	@Before
	public void setUp() {
		HashMap<Integer, Object> values = new HashMap<Integer, Object>();
		values.put(DAAPConstants.ALBUM, "The album");
		
		t = new DAAPTrack(values, null);
	}
	
	@Test
	public void testInitialise() {
		new DAAPTrack(new HashMap<Integer, Object>(), null);
	}
	
	@Test (expected = NullPointerException.class)
	public void testNullValuesInitialise() {
		new DAAPTrack(null, null);
	}

	@Test
	public void testGetTag() {
		assertEquals("The album", t.get(DAAPConstants.ALBUM));
	}
	
	@Test
	public void testUnsetTag() {
		assertNull(t.get(-1));
	}

	
	@Test
	public void testToString() {
		System.out.println(t.toString());
		assertEquals("null - null - The album", t.toString());
	}
}
