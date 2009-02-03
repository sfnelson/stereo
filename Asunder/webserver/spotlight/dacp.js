function DACP (host, container, contentCodes) {
	
	var dis = this;
	
	this.host = host;
	this.container = container;
	this.playlist = 1;
	
	this.current = 0;
	var requests = new Queue();
	
	this.request = function (db, pl, request) {
		sysout(db + " " + pl + " " + request);
		
		var req = 0;
		if (!pl) {
			req = db;
		}
		else if (!request) {
			req = "/databases/" + db + request;
		}
		else {
			req = "/databases/" + db + "/containers/" + pl + request;
		}
		
		if (this.current) {
			requests.offer(req);
		}
		else {
			this.current = req;
			ajax.request(this.host + req, function (text) { dis.response(text); });
		}
	};
	
	this.response = function (response) {
		
		if (response.length > 0) {

			var tree = new DACPNode(response);
			var node = 0;
			switch (tree.name(0)) {
			case constants.dmap.contentcodesresponse.code:
				node = this.contentCodes(tree);
				break;
			case constants.daap.databaseplaylists.code:
				node = new DatabasePlaylists(tree);
				break;
			case constants.daap.databasebrowse.code:
				node = new BrowseResponse(tree);
				break;
			case constants.daap.playlistsongs.code:
				node = new PlaylistSongs(tree);
				break;
			case constants.dmap.editdictionary.code:
				this.request("/databases/1/containers");
				break;
			default:
				alert(tree.name(0) + " not found");
			}

			if (node && node.items.length > 0) {
				
				this.container.appendChild(node.wrapper);
			}

		}
		
		this.current = 0;
		if (requests.size() > 0) {
			this.request(requests.poll());
		}
	};
	
	this.clear = function () {
		while (this.container.firstChild) {
			this.container.removeChild(this.container.firstChild);
		}
	};
	
	this.contentCodes = function (tree) {
		
		var parse = function (tree, entry) {
			var name;
			var code;
			var type;
			var nodes = tree.children(entry);
			for (n in nodes) {
				var node = nodes[n];
				switch (tree.name(node)) {
				case constants.dmap.contentcodesname.code:
					name = tree.stringVal(node);
					break;
				case constants.dmap.contentcodestype.code:
					type = tree.intVal(node);
					break;
				case constants.dmap.contentcodesnumber.code:
					code = tree.stringVal(node);
					break;
				}
			}
			constants.put(name, code, type);
		};
		
		var children = tree.children(0);
		for (i in children) {
			var child = children[i];
			if (tree.name(child) == constants.dmap.dictionary.code) {
				parse(tree, child);
			}
		}
	};
	
	this.search = function () {
		
		var db = 1;
		var pl = this.playlist;
		
		sysout(this.playlist);
		
		var query = document.getElementById("search-field").value;
		
		if (query.length == 0) {
			dacp.request(db, pl,"/browse/artists");
		}
		else if (query.indexOf(":") != -1) {
			query = "daap.song"+query;
			switch (query) {
			case "daap.songartist:":
				dacp.request(db, pl, "/browse/artists");
				break;
			case "daap.songalbum:":
				dacp.request(db, pl, "/browse/albums");
				break;
			default:
				dacp.request(db, pl, "/browse/artists?filter='"+query+"'");
				dacp.request(db, pl, "/browse/albums?filter='"+query+"'");
				dacp.request(db, pl, "/items?query='"+query+"'");
				break;
			}
		}
		else {
			dacp.request(db, pl, "/browse/artists?filter='daap.songartist:*"+query+"*'");
			dacp.request(db, pl, "/browse/albums?filter='daap.songalbum:*"+query+"*'");
			dacp.request(db, pl, "/items?query='dmap.itemname:*"+query+"*'");
		}
		
		this.clear();
	};
	
	this.setSearch = function (query) {
		if (query.indexOf(":") != -1) {
			query = query.replace("daap.song","");
		}
		var field = document.getElementById("search-field");
		field.value = query;
		this.search();
	};
	
	this.setSearchCategory = function (type) {
		var field = document.getElementById("search-field");
		query = field.value;
		
		if (query.indexOf(":") != -1) {
			query = query.substring(query.indexOf(":")+1);
		}
		else if (query.charAt(0) != "*") {
			query = "*"+query+"*";
		}
		
		query = type.name + ":" + query;
		query = query.replace("daap.song","");
		
		this.setSearch(query);
	};
	
	this.enqueue = function (query) {
		this.request("/ctrl-int/1/cue?query='"+query+"'");
	};
	
	if (!contentCodes) {
		this.request("/content-codes");
	}
}

function DatabasePlaylists(tree) {
	this.init();
	
	var list = tree.findNode(0, constants.dmap.listing.code);
	if (list == -1) {
		alert("invalid database containers response");
		return;
	}
	
	var children = tree.children(list);
	
	for (var i in children) {
		this.put(new DACPContainer(tree, children[i]));
	}

	this.wrapper.className += " -stereo-database-containers";
	this.show();
	
	var dis = this;
	
	var edit = document.createElement("FORM");
	edit.name = "playlists-form";
	edit.onsubmit = function () {
		dis.add();
		return false;
	};
	edit.className = "-stereo-databases-containers-editbar";
	
	var add = document.createElement("INPUT");
	add.className = "-stereo-databases-containers-add-button";
	add.value = "+";
	add.type = "submit";
	edit.appendChild(add);
	
	this.wrapper.appendChild(edit);
}
DatabasePlaylists.prototype = new View;
DatabasePlaylists.prototype.add = function () {
	var name = prompt("Collection Name:", "");
	playlists.clear();
	playlists.request("/databases/1/edit?action=add&edit-params='dmap.itemname:"+name+"'");
};

function BrowseResponse(tree) {

	var list = -1;
	
	var children = tree.children(0);
	for (var i in children) {
		var child = children[i];
		switch (tree.name(child)) {
		case constants.daap.browseartistlisting.code:
			this.name = "Artists"; list = child;
			this.type = constants.daap.songartist;
			break;
		case constants.daap.browsealbumlisting.code:
			this.name = "Albums"; list = child;
			this.type = constants.daap.songalbum;
			break;
		case constants.daap.browsegenrelisting.code:
			this.name = "Genres"; list = child;
			this.type = constants.daap.songgenre;
			break;
		case constants.daap.browsecomposerlisting.code:
			this.name = "Composers"; list = child;
			this.type = constants.daap.songcomposer;
			break;
		default:
			continue;
		}
	}
	
	if (list == -1) {
		alert("invalid database browse response");
		this.init();
		return;
	}
	
	this.init(this.name);
	
	var children = tree.children(list);
	
	for (var i in children) {
		this.put(new BrowseItem(tree, children[i], this.type));
	}

	this.wrapper.className += " database-browse"
	this.show();
	
	var type = this.type;
	this.activate = function () {
		dacp.setSearchCategory(type);
	};
}
BrowseResponse.prototype = new View;

function PlaylistSongs(tree) {
	this.init("Songs");
	
	var list = tree.findNode(0, constants.dmap.listing.code);
	if (list == -1) {
		alert("invalid playlist songs response");
		return;
	}
	
	var children = tree.children(list);
	
	for (var i in children) {
		this.put(new DACPTrack(tree, children[i]));
	}

	this.wrapper.className += " playlist-songs"
	this.show();
	
}
PlaylistSongs.prototype = new View;
