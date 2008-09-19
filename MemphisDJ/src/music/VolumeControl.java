package music;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class VolumeControl implements interfaces.VolumeControl {

	private int volume;
	
	public int getVolume() {
		return this.volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
		URL url = DJ.class.getResource("setvolume.sh");

		try {
			System.out.println("setting volume to " + volume);
			Process p = Runtime.getRuntime().exec(
					"bash " + url.getFile() + " " + volume);
			for (Scanner sc = new Scanner(p.getErrorStream()); sc.hasNextLine();) {
				System.out.println(sc.nextLine());
			}
		} catch (IOException e) {
			System.err.println("set volume failed");
			e.printStackTrace();
		}
	}

}
