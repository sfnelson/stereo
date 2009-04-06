package mpris.dbustypes;

import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;

public class StatusCode extends Struct {
	public final @Position(0) int playstate;
	public final @Position(1) int random;
	public final @Position(2) int repeat;
	public final @Position(3) int loop;
	
	public StatusCode(int playstate, boolean random, boolean repeat, boolean loop) {
		this.playstate = playstate;
		this.random = random ? 1 : 0;
		this.repeat = repeat ? 1 : 0;
		this.loop = loop ? 1 : 0;
	}
}
