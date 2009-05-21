package util.command;

import interfaces.DJInterface;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import webserver.FileLoader;
import api.Response;
import dmap.node.PageNode;

public class HTMLClient implements Command {

	private String request;
	
	public void init(Map<String, String> args) {
		request = args.get("request");
		if (request == null || request.equals("")) {
			request = "index.xhtml";
		}
	}

	public Response run(DJInterface dj) {
		
		Scanner sc = new Scanner(request);
		sc.useDelimiter(Pattern.compile("[.]"));
		
		String ext = "";
		while (sc.hasNext()) {
			ext = sc.next();
		}
		
		String type = "text/html";
		if (ext.equals("xhtml")) {
			type = "application/xhtml+xml";
		}
		else if (ext.equals("js")) {
			type = "text/javascript";
		}
		else if (ext.equals("css")) {
			type = "text/css";
		}
		else if (ext.equals("png")) {
			type = "image/png";
		}
		else if (ext.equals("jpg")) {
			type = "image/jpg";
		}
		else if (ext.equals("gif")) {
			type = "image/gif";
		}
		else if (ext.equals("ico")) {
			type = "image/ico";
		}
		
		try {
			String body = FileLoader.load(request);
			return new PageNode(type, body);
		} catch (NullPointerException ex) {
			System.err.println("File not found: " + request);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	

}
