package daap;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;


public class DaapClientTest2 {
	@SuppressWarnings("unused")
	private static DAAPClient client;

	@BeforeClass
	public static void setup(){
		try {
			client = new DAAPClient("serranos.mcs.vuw.ac.nz", 3689, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


//	@Test
//	public void playTest(){
	//
//	try {
//	List<Track> music = client.getTrackList();
//	InputStream in = client.getStream(music.get(0));
//	int x = in.read();
////	while(x!=-1){
////	System.out.print(x);
////	x = in.read();
////	}

//	in.close();
//	} catch (IOException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//	}

//	}




	@AfterClass
	public static void teardown(){
		client = null;
	}
}
