package util.command;

import interfaces.DJInterface;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;

import util.node.Node;
import util.node.PageNode;

public class PageRequest implements Command {

	public void init(Map<String, String> args) {
		// TODO Auto-generated method stub

	}

	public Node run(DJInterface dj) {

		String body = "";
		try {
			File f = new File(PageRequest.class.getResource("stereo.html").toURI());
			Scanner sc = new Scanner(f);
			while (sc.hasNextLine()) {
				body += sc.nextLine() + "\r\n";
			}
			return new PageNode("application/xhtml+xml", body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
