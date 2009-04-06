package mpris.dbustypes;

import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.UInt16;

public class FullVersion extends Struct {
	public final @Position(0) UInt16 major;
	public final @Position(1) UInt16 minor;
	
	public FullVersion(UInt16 major, UInt16 minor) {
		this.major = major;
		this.minor = minor;
	}
	
	public FullVersion(int major, int minor) {
		this.major = new UInt16(major);
		this.minor = new UInt16(minor);
	}
}
