package writer;

import java.util.Map;

public class DACPRequestGenerator {
	
	public static String play() {
		return "playpause";
	}
	
	public static String pause() {
		return "pause";
	}
	
	public static String skip() {
		return "skip";
	}

	public static String getTracks(Map<String, String> parameters) {
		return "requestplaylist";
	}

	public static String changeVolume(int newVolume) {
		if(newVolume>255 || newVolume <0) throw new IllegalArgumentException("Volume must be between 0~255");
		return "setproperty?dmcp.volume=" + newVolume;
	}
}
