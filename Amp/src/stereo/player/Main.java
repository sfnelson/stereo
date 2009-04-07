package stereo.player;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class Main {

	public static void main(String args[]) throws InterruptedException {
		
		String bind = "localhost";
		String host = null;
		int port = 3689;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--bind") || args[i].equals("-b")) {
				if (i+1 < args.length) {
					bind = args[i+1];
					i++;
				}
			}
			else if (args[i].equals("--host") || args[i].equals("-h")) {
				if (i+1 < args.length) {
					host = args[i+1];
					i++;
				}
			}
			else if (args[i].equals("--port") || args[i].equals("-p")) {
				if (i+1 < args.length) {
					port = Integer.parseInt(args[i+1]);
					i++;
				}
			}
		}
		
		if (host == null) {
			
			ServiceInfo[] infos = null;
			try {
				infos = register(bind);
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			
			if (infos == null) {
				System.exit(1);
			}
			
			System.out.println("Available hosts: ");
			for (ServiceInfo info: infos) {
				String name = new String(info.getPropertyBytes("CtlN"));
				System.out.printf("\t%s:%s\t%s\n", info.getHostAddress(), info.getPort(), name);
			}
			
			if (infos.length == 1) {
				host = infos[0].getHostAddress();
				port = infos[0].getPort();
			}
			else {
				System.exit(0);
			}
		}
		
		StereoAmp amp = new StereoAmp(host, port);
		amp.start();
		amp.listen();
		
	}
	
	public static ServiceInfo[] register(String address) throws IOException, InterruptedException {
		
		try {
            JmDNS jmdns = JmDNS.create(InetAddress.getByName(address));
            ServiceInfo[] infos = null;
            
            while (true) {
                
                infos = jmdns.list("_touch-able._tcp.local.");
                
                if (infos.length > 0) break;
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            jmdns.close();
            
            return infos;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
		
        return null;
	}
}
