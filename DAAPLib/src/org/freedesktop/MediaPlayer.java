package org.freedesktop;

import mpris.dbustypes.FullVersion;

import org.freedesktop.dbus.DBusInterface;

public interface MediaPlayer extends DBusInterface {
	public String Identity();
	public void Quit();
	public FullVersion MprisVersion();
}
