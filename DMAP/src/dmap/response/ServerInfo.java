package dmap.response;

import interfaces.Constants;
import interfaces.HasMetadata;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import api.Reader;
import api.Response;
import api.Writer;

public class ServerInfo extends Response implements HasMetadata {
	
	private static final byte TRUE = 1;

	private final Map<Constants, Object> metadata = new LinkedHashMap<Constants, Object>();

	public static ServerInfo read(Reader reader) {

		ServerInfo info = new ServerInfo();

		for (Constants c: reader) {
			switch (c.type) {
			case Constants.BYTE: info.metadata.put(c, reader.nextByte(c)); break;
			case Constants.SHORT: info.metadata.put(c, reader.nextShort(c)); break;
			case Constants.INTEGER: info.metadata.put(c, reader.nextInteger(c)); break;
			case Constants.SIGNED_INTEGER: info.metadata.put(c, reader.nextInteger(c)); break;
			case Constants.LONG: info.metadata.put(c, reader.nextLong(c)); break;
			case Constants.DATE: info.metadata.put(c, reader.nextInteger(c)); break;
			case Constants.VERSION: info.metadata.put(c, reader.nextVersion(c)); break;
			case Constants.STRING: info.metadata.put(c, reader.nextString(c)); break;
			default:
				throw new RuntimeException("unexpected node " + c.longName);
			}
		}

		return info;
	}

	public ServerInfo(String serverName, int databases) {

		super(Constants.dmap_serverinforesponse, Response.OK);

		metadata.put(Constants.dmap_protocolversion, new byte[] { 0, 2, 0, 5 }); //mpro
		metadata.put(Constants.daap_protocolversion, new byte[] { 0, 3, 0, 7 }); //apro

		metadata.put(Constants.com_apple_itunes_musicSharingVersion, 0x30000); //aeSV
		metadata.put(Constants.com_apple_itunes_aeFP, TRUE); //aeFP

		metadata.put(Constants.daap_supportsextradata, (short)3); //ated
		metadata.put(Constants.dmap_supportsedit, TRUE); //msed

		//speaker listing goes here in itunes (msml (msma int, msma int))

		metadata.put(Constants.dmap_itemname, serverName); //minm
		metadata.put(Constants.dmap_loginrequired, TRUE); //mslr
		metadata.put(Constants.dmap_timeoutinterval, 1800); //mstm
		metadata.put(Constants.dmap_supportsautologout, TRUE); //msal
		metadata.put(Constants.dmap_authenticationschemes, (byte)3); //msas
		metadata.put(Constants.dmap_supportsupdate, TRUE); //msup
		metadata.put(Constants.dmap_supportspersistentids, TRUE); //mspi
		metadata.put(Constants.dmap_supportsextensions, TRUE); //msex
		metadata.put(Constants.dmap_supportsbrowse, TRUE); //msbr
		metadata.put(Constants.dmap_supportsquery, TRUE); //msqy
		metadata.put(Constants.dmap_supportsindex, TRUE); //msix
		metadata.put(Constants.dmap_supportsresolve, TRUE); //msrs
		metadata.put(Constants.dmap_databasescount, databases); //msdc
		metadata.put(Constants.dmap_utctime, (int)(System.currentTimeMillis()/1000)); //mstc
		metadata.put(Constants.dmap_utcoffset, Calendar.getInstance().getTimeZone().getRawOffset()/1000); //msto
	}

	private ServerInfo() {
		super(Constants.dmap_serverinforesponse, Response.OK);

		//do nothing - factory will populate metadata
	}

	public Object get(Constants key) {
		return metadata.get(key);
	}

	public Iterable<Constants> getAllTags() {
		return metadata.keySet();
	}

	public void write(Writer writer) {
		super.write(writer);

		for (Constants c: metadata.keySet()) {
			try {
				Object v = metadata.get(c);
				
				switch (c.type) {
				case Constants.BYTE: writer.appendByte(c, (Byte)v); break;
				case Constants.SHORT: writer.appendShort(c, (Short)v); break;
				case Constants.INTEGER: writer.appendInteger(c, (Integer)v); break;
				case Constants.SIGNED_INTEGER: writer.appendInteger(c, (Integer)v); break;
				case Constants.LONG: writer.appendLong(c, (Long)v); break;
				case Constants.DATE: writer.appendDate(c, (Integer)v); break;
				case Constants.VERSION: writer.appendVersion(c, (byte[])v); break;
				case Constants.STRING: writer.appendString(c, (String)v); break;
				default:
					throw new RuntimeException("unexpected node " + c.longName);
				}
			}
			catch (ClassCastException ex) {
				throw new RuntimeException("error writing " + c.longName, ex);
			}
		}

	}

}
