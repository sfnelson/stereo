package reader;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.command.Command;
import util.command.CtrlInt;
import util.command.Databases;
import util.command.Login;
import util.command.Logout;
import util.command.PageRequest;
import util.command.PathNode;
import util.command.RequestNode;
import util.command.ServerInfo;
import util.command.Update;

public class DACPRequestParser {

	@SuppressWarnings("unused")
	private static PathNode base = new PathNode() {
		public RequestNode serverInfo() {
			return new ServerInfo();
		}
		public RequestNode login() {
			return new Login();
		}
		public RequestNode logout() {
			return new Logout();
		}
		public PathNode ctrlInt() {
			return new CtrlInt();
		}
		public PathNode databases() {
			return new Databases();
		}
		public RequestNode update() {
			return new Update();
		}
	};
	
	public static Command parse(String p) {

		try {
			final Scanner request = new Scanner(p);

			final String type = request.next();
			final String uri = request.next();
			final String protocol = request.next();

			System.out.println("Received request: " + uri + " (" + type + ", " + protocol + ")");

			Scanner req = new Scanner(uri);
			req.useDelimiter("[?]");
			
			Scanner cmd = new Scanner(req.next());
			cmd.useDelimiter("[/]");
			
			Scanner args = new Scanner(req.hasNext()?req.next():"");
			args.useDelimiter("[&]");
			
			Map<String, String> argsMap = new HashMap<String, String>();
			while (args.hasNext()) {
				Scanner param = new Scanner(args.next());
				param.useDelimiter("[=]");
				argsMap.put(param.next(), param.next());
			}
			
			if (!cmd.hasNext()) {
				//a request for head: return html client page
				
				return new PageRequest();
			}
			
			RequestNode node = base;
			while (cmd.hasNext()) {
				
				int arg = -1;
				if (cmd.hasNextInt()) {
					arg = cmd.nextInt();
					if (!cmd.hasNext()) {
						throw new IllegalArgumentException("invalid command string: " + uri);
					}
				}
				
				if (node instanceof PathNode) {
					
					String name = cmd.next();
					if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
						throw new IllegalArgumentException("invalid command string: " + uri);
					}
					while (name.indexOf('-') != -1) {
						int i = name.indexOf('-');
						name = name.substring(0, i) + name.substring(i+1,i+2).toUpperCase() + name.substring(i+2);
					}
					PathNode path = (PathNode)node;
					Method method = null;
					for (Method m: path.getClass().getMethods()) {
						if (m.getName().equals(name)) {
							method = m;
						}
					}
					if (method == null) {
						throw new IllegalArgumentException("unexpected command '" + name + "' for path " + node.getClass().getName() + ": " + uri);
					}
					try {
						if (arg == -1) {
							node = (RequestNode)method.invoke(path);
						}
						else {
							node = (RequestNode)method.invoke(path, arg);
						}
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
						throw new IllegalArgumentException("unable to access method: " + name);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
						throw new IllegalArgumentException("exception while calling method: " + name);
					}
				}
				else {
					throw new IllegalArgumentException("invalid node: " + node);
				}
			}
			
			if (node instanceof Command) {
				Command c = (Command)node;
				c.init(argsMap);
				return c;
			}

			throw new IllegalArgumentException("invalid command string: " + uri);
		}	
		catch (NoSuchElementException e) {
			throw new IllegalArgumentException("invalid command string");
		}
	}
	
}
