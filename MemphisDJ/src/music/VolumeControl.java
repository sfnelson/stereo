package music;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.net.SocketFactory;


public class VolumeControl implements interfaces.VolumeControl {

	private int volume;
	
	public int getVolume() {
		
		try {
			Socket sock = SocketFactory.getDefault().createSocket("localhost", 50123);
			new PrintWriter(sock.getOutputStream()).append("get\n").flush();
			this.volume = new Scanner(sock.getInputStream()).nextInt();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		URL url = DJ.class.getResource("getvolume.sh");

		try {
			System.out.println("getting volume");
			Process p = Runtime.getRuntime().exec(
					"bash " + url.getFile());
			for (Scanner sc = new Scanner(p.getErrorStream()); sc.hasNextLine();) {
				System.out.println(sc.nextLine());
			}
			Scanner sc = new Scanner(p.getInputStream());
			if (sc.hasNextInt()) {
				this.volume = sc.nextInt();
			}
		} catch (Exception e) {
			System.err.println("set volume failed");
			e.printStackTrace();
		}*/
		
		return this.volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
		
		try {
			Socket sock = SocketFactory.getDefault().createSocket("localhost", 50123);
			new PrintWriter(sock.getOutputStream()).append(this.volume + "\n").flush();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		URL url = DJ.class.getResource("setvolume.sh");

		try {
			System.out.println("setting volume to " + volume);
			Process p = Runtime.getRuntime().exec(
					"bash " + url.getFile() + " " + volume);
			for (Scanner sc = new Scanner(p.getErrorStream()); sc.hasNextLine();) {
				System.out.println(sc.nextLine());
			}
		} catch (Exception e) {
			System.err.println("set volume failed");
			e.printStackTrace();
		}
		*/
	}

}
