function Node(text) {
	
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
}

function PlayStatusUpdate(tree, index) {

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

function findNode(tree, index, tag) {
	var kids = tree.children(index);
	for (child in kids) {
		if (tree.name(kids[child]) == tag) {
			return kids[child];
		}
	}
	return 0;
}

function getItems(tree, index, handler) {
	
	var items = new Array();
	
	var kids = tree.children(index);
	for (child in kids) {
		items.push(handler(tree, kids[child]));
	}
	
	return items;
}

function Container(tree, index) {
	
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
		case "miid":
			this.id = tree.intVal(node);
			break;
		case "mper":
			this.persistent = tree.longVal(node);
			break;
		case "minm":
			this.name = tree.stringVal(node);
			break;
		case "abpl":
			this.base = tree.byteVal(node);
			break;
		case "mpco":
			this.parent = tree.intVal(node);
			break;
		case "mimc":
			this.items = tree.intVal(node);
			break;
		}
	}
}

function Playlist(tree, index) {

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
	
	kids = tree.children(list);
	for (child in kids) {
		this.tracks.push(new Track(tree, kids[child]));
	}
}

function Album(tree, index) {

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
}

function Track(tree, index) {

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
}

function sysout(text) {
	var p = document.createElement("PRE");
	p.className = "debug";
	p.appendChild(document.createTextNode(text));
	document.body.appendChild(p);
}
