package webserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class FileLoader {

	public static final String load(String request) throws URISyntaxException, FileNotFoundException {
		System.out.println("loading: " + request);
		URI uri = FileLoader.class.getResource(request).toURI();
		File f = new File(uri);
		Scanner sc = new Scanner(f);
		String body = "";
		while (sc.hasNextLine()) {
			body += sc.nextLine() + "\r\n";
		}
		return body;
	}
}
