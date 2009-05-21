package dmap.response;

import interfaces.Constants;
import api.Reader;
import api.Response;
import api.Writer;

public class Login extends Response {

	private final int sessionId;

	public static Login read(Reader reader) {

		for (Constants c: reader) {
			switch (c) {
			case dmap_sessionid:
				return new Login(reader.nextInteger(c));
			}
		}

		throw new RuntimeException("session id not found");
	}

	public Login(int sessionId) {
		super(Constants.dmap_loginresponse, Response.OK);

		this.sessionId = sessionId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void write(Writer writer) {
		super.write(writer);

		writer.appendInteger(Constants.dmap_sessionid, sessionId);
	}
}
