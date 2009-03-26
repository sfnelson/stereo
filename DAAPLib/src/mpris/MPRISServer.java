package mpris;

import interfaces.DJInterface;

import java.io.IOException;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

public class MPRISServer {
	private final DJInterface dj;

	public MPRISServer(DJInterface dj) throws IOException {
		this.dj = dj;
		
		DBusConnection conn;
		try {
			conn = DBusConnection.getConnection(DBusConnection.SESSION);
			System.out.println("Got connection " + conn);
			conn.requestBusName("org.mpris.stereo");
			conn.exportObject("/", new RootObject());
			System.out.println("All fine so far.");
		} catch (DBusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
