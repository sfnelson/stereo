var constants = {
		put: function (name, code, type) {
			var n = name.split('.');
			var c = this;
			for (i in n) {
				if (i+1 < n.length) {
					c = c[n[i]];
				}
				else {
					c[n[i]] = new Constant(name, code, type);
				}
			}
		},
		dmap: {
				contentcodesresponse: new Constant("dmap.contentcodesresponse", "mccr", 12),
				dictionary: new Constant("dmap.dictionary", "mdcl", 12),
				contentcodesnumber: new Constant("dmap.contentcodesnumber", "mcnm",  5),
				contentcodesname: new Constant("dmap.contentcodesname", "mcna",  9),
				contentcodestype: new Constant("dmap.contentcodestype", "mcty",  3)
		},
		daap: new Object(),
		dacp: new Object(),
		dmcp: new Object(),
		com: { apple: { itunes: new Object() } }
};

function Constant(name, code, type) {
	this.name = name;
	this.code = code;
	this.type = type;
}

function DACPNode(text) {
	
	var parseNum = function (index, len) {
		if (len > 4) return parseBigNum(index, len);
	
		var num = text.charCodeAt(index) & 255;
		for (var i = 1; i < len; i++) {
			num = num * 256;
			num += text.charCodeAt(index + i) & 255;
		}
		return num;
	};
	
	var parseBigNum = function (index, len) {
		var num = new BigNumber(text.charCodeAt(index) & 255);
		for (var i = 1; i < len; i++) {
			num = num.multiply(256);
			num = num.add(text.charCodeAt(index + i) & 255);
		}
		return num;
	};
	
	this.name = function (index) {
		return text.substring(index, index+4);
	};
	this.length = function (index) {
		return parseNum(index+4, 4);
	};
	this.byteVal = function (index) {
		return parseNum(index+8, 1);
	};
	this.shortVal = function (index) {
		return parseNum(index+8, 2);
	};
	this.intVal = function (index) {
		return parseNum(index+8, 4);
	};
	this.longVal = function (index) {
		return parseNum(index+8, 8);
	};
	this.longlongVal = function (index) {
		return {
			0:	parseNum(index+8, 4),
			1:	parseNum(index+12, 4),
			2:	parseNum(index+16, 4),
			3:	parseNum(index+20, 4)
		};
	};
	this.stringVal = function (index) {
		var length = this.length(index);
		var string = "";
		for ( var i = 8; i < 8+length;) {
			var c = text.charCodeAt(index + i) & 255;
			var c2,c3;
			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			} else if (c > 191 && c < 224) {
				c2 = text.charCodeAt(index + i + 1) & 255;
				string += String.fromCharCode(((c & 31)) << 6 | (c2 & 63));
				i += 2;
			} else {
				c2 = text.charCodeAt(index + i + 1) & 255;
				c3 = text.charCodeAt(index + i + 2) & 255;
				string += String.fromCharCode(((c & 15)) << 12 | (c2 & 63) << 6
						| (c3 & 63));
				i += 3;
			}
		}
		return string;
	};
	this.dateVal = function (index) {
		return parseNum(index+8, 8);
	};
	this.versionVal = function (index) {
		var version = text.charCodeAt(index+8) & 255;
		for (i in 1, 2, 3) {
			version += ".";
			version += text.charCodeAt(index+8 + i) & 255;
		}
		return version;
	};
	this.children = function (index) {
		var kids = new Array();
		var len = index + 8 + this.length(index);
		var i = index+8;
		while (i < len) {
			kids.push(i);
			i += 8 + this.length(i);
		}
		return kids;
	};
	this.findNode = function (index, tag) {
		var kids = this.children(index);
		for (child in kids) {
			if (this.name(kids[child]) == tag) {
				return kids[child];
			}
		}
		return -1;
	}
}
 
function DACPPlayStatusUpdate(tree, index) {
 
	var kids = tree.children(index);
 
	this.album = "";
	this.title = "";
	this.artist = "";
	this.genre = "";
	this.id = 0;
	this.status = 0;
	this.revision = 0;
	this.container = 0;
	
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
 
function getItems(tree, index, handler) {
	
	var items = new Array();
	
	var kids = tree.children(index);
	for (child in kids) {
		items.push(handler(tree, kids[child]));
	}
	
	return items;
}

function DACPContainer(tree, index) {
	
	this.id = 0;
	this.persistent = 0;
	this.name = "";
	this.base = false;
	this.parent = 0;
	this.items = 0;
 
	var kids = tree.children(index);
	for (child in kids) {
		var node = kids[child];
		switch (tree.name(node)) {
		case constants.dmap.itemid.code:
			this.id = tree.intVal(node);
			break;
		case constants.dmap.persistentid.code:
			this.persistent = tree.longVal(node);
			break;
		case constants.dmap.itemname.code:
			this.name = tree.stringVal(node);
			break;
		case constants.daap.baseplaylist.code:
			this.base = tree.byteVal(node);
			break;
		case constants.dmap.parentcontainerid.code:
			this.parent = tree.intVal(node);
			break;
		case constants.dmap.itemcount.code:
			this.items = tree.intVal(node);
			break;
		}
	};
	
	this.init();
	this.node.className += " -database-playlist";
	this.appendChild(this.name, "-database-playlist-name");
	//this.appendChild(this.items, "-database-playlist-items");
	//this.appendChild(this.id, "-database-playlist-id");
}
DACPContainer.prototype = new Item;
DACPContainer.prototype.selected = function () {
	Item.prototype.selected.call(this);
	dacp.playlist = this.id;
	dacp.search();
}
DACPContainer.prototype.deselected = function () {
	Item.prototype.deselected.call(this);
	if (dacp.playlist == this.id) {
		dacp.playlist = 1;
	}
}
 
function DACPPlaylist(tree, index) {
 
	this.tracks = new Array();
 
	var kids = tree.children(index);
	var list = 0;
	for (child in kids) {
		if (tree.name(kids[child]) == "mlcl") {
			list = kids[child];
			break;
		}
	}
 
	if (!list) return;
	
	this.init();
	
	kids = tree.children(list);
	for (child in kids) {
		this.tracks.push(new DACPTrack(tree, kids[child]));
	}
	
}
DACPPlaylist.prototype = new View;
 
function DACPAlbum(tree, index) {
 
	this.id = 0;
	this.name = "";
	this.artist = 0;
	this.persistantId = 0;
 
	var kids = tree.children(index);
	for (child in kids) {
		var node = kids[child];
		switch (tree.name(node)) {
		case "miid":
			this.id = tree.intVal(node);
			break;
		case "minm":
			this.name = tree.stringVal(node);
			break;
		case "asaa":
			this.artist = tree.stringVal(node);
			break;
		case "mper":
			this.persistantId = tree.longVal(node);
			break;
		}
	}
	
	this.init();
	this.node.className += " -stereo-album";
	
	this.appendChild(this.name, "-stereo-album-title");
	this.appendChild(this.title, "-stereo-album-artist");
}
DACPAlbum.prototype = new Item;
 
function DACPTrack(tree, index) {
 
	this.album = "";
	this.title = "";
	this.artist = "";
	this.genre = "";
	this.id = 0;
	this.persistent = 0;
	
	var kids = tree.children(index);
	for (child in kids) {
		var node = kids[child];
		switch (tree.name(node)) {
		case "asal":
			this.album = tree.stringVal(node);
			break;
		case "minm":
			this.title = tree.stringVal(node);
			break;
		case "asar":
			this.artist = tree.stringVal(node);
			break;
		case "asag":
			this.genre = tree.stringVal(node);
			break;
		case "miid":
			this.id = tree.intVal(node);
			break;
		case "mper":
			this.persistent = tree.longVal(node);
			break;
		}
	}

	this.init();
	this.node.className += " -stereo-track";
	
	var title = document.createElement("DIV");
	title.appendChild(document.createTextNode(this.title));
	
	this.appendChild(this.artist, "-stereo-track-artist");
	this.appendChild(title, "-stereo-track-title");
	this.appendChild(this.album, "-stereo-track-album");
	this.appendChild(this.genre, "-stereo-track-genre");
	
	var arrow = document.createElement("SPAN");
	//arrow.innerHTML = "&#9654;";
	arrow.innerHTML = "enqueue";
	arrow.className = "enqueue hover";
	title.appendChild(arrow);
	
	var play = document.createElement("SPAN");
	play.innerHTML = "play";
	play.className = "play hover";
	title.appendChild(play);
	
	this.clicked = function (e) {
		if (e.target.className && e.target.className.indexOf("play") != -1) {
			dacp.play(constants.dmap.itemid.name + ":" + this.id);
		}
		else if (e.target.className && e.target.className.indexOf("enqueue") != -1) {
			dacp.enqueue(constants.dmap.itemid.name + ":" + this.id);
		}
		else {
			DACPTrack.prototype.clicked.call(this);
		}
	};
}
DACPTrack.prototype = new Item;

function BrowseItem(tree, index, type) {
 
	this.type = type;
	this.value = tree.stringVal(index);
	
	this.init();
	this.node.className += " -browse-item";
	
	var arrow = document.createElement("DIV");
	//arrow.innerHTML = "&#9654;";
	arrow.innerHTML = "enqueue";
	this.appendChild(arrow, "enqueue hover");
	
	var play = document.createElement("DIV");
	play.innerHTML = "play";
	this.appendChild(play, "play hover");
	
	this.appendChild(this.value);
	
	this.clicked = function (e) {
		if (e.target.className && e.target.className.indexOf("play") != -1) {
			dacp.play(this.type.name + ":" + this.value);
		}
		else if (e.target.className && e.target.className.indexOf("enqueue") != -1) {
			dacp.enqueue(this.type.name + ":" + this.value);
		}
		else {
			BrowseItem.prototype.clicked.call(this);
		}
	};

}
BrowseItem.prototype = new Item;
BrowseItem.prototype.selected = function () {
	dacp.setSearch(this.type.name + ":" + this.value);
};

function sysout(text) {
	if (window.console) {
		window.console.log(text);
	}
	else {
		var p = document.createElement("PRE");
		p.className = "debug";
		p.appendChild(document.createTextNode(text));
		document.body.appendChild(p);
	}
}
