
function DACP (host, container, contentCodes) {
	
	var dis = this;

	this.current = 0;
	var requests = new Queue();
	
	this.request = function (request) {
		
		if (this.current) {
			requests.offer(request);
		}
		else {
			this.current = request;
			ajax.request(request, function (text) { dis.response(text); });
		}
	};
	
	this.response = function (response) {
		
		this.current = 0;
		if (requests.size() > 0) {
			this.request(requests.poll());
		}
		
		if (response.length > 0) {
			var tree = new DACPNode(response);
			var node = 0;
			switch (tree.name(0)) {
			case constants.dmap.contentcodesresponse.code:
				node = this.contentCodes(tree);
				break;
			case constants.dmcp.status.code:
				node = new PlayStatusUpdate(tree);
				this.playstatusupdate(node);
				break;
			default:
				alert(tree.name(0) + " not found");
			}
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

		this.clear();
		
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
	
	this.play = function (query) {
		this.request("/ctrl-int/1/cue?command=clear");
		this.request("/ctrl-int/1/cue?command=play&query='"+query+"'");
	};
	
	this.playstatusupdate = function (tree) {
		
	};
	
	if (!contentCodes) {
		this.request("/content-codes");
	}
}

function PlayStatusUpdate(tree) {

	var kids = tree.children(0);
	
	for (child in kids) {
		var node = kids[child];
		switch (tree.name(node)) {
		case "caps":
			this.status = tree.byteVal(node);
			break;
		case "cmsr":
			this.revision = tree.intVal(node);
			break;
		case "cann":
			this.title = tree.stringVal(node);
			break;
		case "cana":
			this.artist = tree.stringVal(node);
			break;
		case "canl":
			this.album = tree.stringVal(node);
			break;
		case "cang":
			this.genre = tree.stringVal(node);
			break;
		case "canp":
			this.container = tree.longlongVal(node)[1];
			this.id = tree.longlongVal(node)[3];
			break;
		}
	}
}
