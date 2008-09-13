package sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import memphis.stereo.backend.sources.Source;
import nz.ac.vuw.mcs.memphis.stereo.client.tokens.SourceToken;
import nz.ac.vuw.mcs.memphis.stereo.server.StereoLibraryImpl;
import nz.ac.vuw.mcs.memphis.stereo.server.StereoLog;

public class SourceFactory {

	static {
		factory = new SourceFactory();
	}
	
	private static SourceFactory factory;
	
	public static SourceFactory factory() {
		return factory;
	}
	
	public SourceToken addSource(String hostname) {
		
		try {
			DaapSource client = new DaapSource(hostname);
			client.init();
			
			SourceToken token = new SourceToken();
			token.host = hostname;
			token.name = client.getName();

			this.sources.put(token, client);
			this.hosts.put(client.database().getName(), client);
			
			StereoLibraryImpl.getDatabase().add(client.database());
			
			return token;
		}
		catch (IOException ex) {
			StereoLog.getLog(SourceFactory.class).error("error loading source " + hostname, ex);
		}
		
		return null;
	}
	
	public void removeSource(SourceToken token) {
		
	}
	
	public List<SourceToken> getTokens() {
		
		Set<SourceToken> keys = this.sources.keySet();
		
		List<SourceToken> list = new Vector<SourceToken>();
		
		for (SourceToken token: keys) {
			list.add(token);
		}
		
		for (java.util.Iterator<SourceToken> it = list.iterator(); it.hasNext();) {
			
			SourceToken key = it.next();
			
			Source s = this.sources.get(key);
			
			if (!s.isAvailable()) {
				it.remove();
				this.sources.remove(key);
			}
		}
		
		return list;
	}
	
	public Source getSource(SourceToken token) {
		
		if (token == null) return null;
		
		Source source = this.sources.get(token);
		
		if (source == null) return null;
		
		if (!source.isAvailable()) {
			this.sources.remove(token);
			
			return null;
		}
		
		return source;
	}
	
	public Source getSource(String hostname) {
		
		return this.hosts.get(hostname);
	}
	
	public void addListener(SourceListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(SourceListener listener) {
		this.listeners.remove(listener);
	}
	
	private  Map<SourceToken, Source> sources = new HashMap<SourceToken, Source>();
	private Map<String, Source> hosts = new HashMap<String, Source>();
	
	private Set<SourceListener> listeners = new HashSet<SourceListener>();
	
	public static interface SourceListener {
		
		public void onSourceAdded(SourceToken source);
		public void onSourceRemoved(SourceToken source);
		public void onSourceRefreshed(SourceToken source);
		
	}
}