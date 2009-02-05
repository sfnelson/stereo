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
				contentcodesresponse: new DaapConstant("dmap.contentcodesresponse", "mccr", 12),
				dictionary: new DaapConstant("dmap.dictionary", "mdcl", 12),
				contentcodesnumber: new DaapConstant("dmap.contentcodesnumber", "mcnm",  5),
				contentcodesname: new DaapConstant("dmap.contentcodesname", "mcna",  9),
				contentcodestype: new DaapConstant("dmap.contentcodestype", "mcty",  3)
		},
		daap: new Object(),
		dacp: new Object(),
		dmcp: new Object(),
		com: { apple: { itunes: new Object() } }
};

function DaapConstant(name, code, type) {
	this.name = name;
	this.code = code;
	this.type = type;
}

var BigNumber = {}
document.write('<script type="text/javascript" src="/bignum.js"></script>');

function DACPNode(text) {
	this.text = text;
	
	var parseBigNum = function (index, len) {
		var num = new BigNumber(text.charCodeAt(index) & 255);
		for (var i = 1; i < len; i++) {
			num = num.multiply(256);
			num = num.add(text.charCodeAt(index + i) & 255);
		}
		return num;
	};
	
	var parseNum = function (index, len) {
		if (len > 4) return parseBigNum(index, len);
	
		var num = text.charCodeAt(index) & 255;
		for (var i = 1; i < len; i++) {
			num = num * 256;
			num += text.charCodeAt(index + i) & 255;
		}
		return num;
	};

	this.name =		function (index) { return text.substring(index, index+4); };
	this.length =	function (index) { return parseNum(index+4, 4); };
	this.byteVal =	function (index) { return parseNum(index+8, 1); };
	this.shortVal =	function (index) { return parseNum(index+8, 2); };
	this.intVal =	function (index) { return parseNum(index+8, 4); };
	this.dateVal =	function (index) { return parseNum(index+8, 8); };
	this.longVal =	function (index) { return parseNum(index+8, 8); };
	this.longlongVal =	function (index) {
		return {
			0:	parseNum(index+8, 4),
			1:	parseNum(index+12, 4),
			2:	parseNum(index+16, 4),
			3:	parseNum(index+20, 4)
		};
	};
	this.stringVal =	function (index) {
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
	this.versionVal =	function (index) {
		var version = text.charCodeAt(index+8) & 255;
		for (i in 1, 2, 3) {
			version += ".";
			version += text.charCodeAt(index+8 + i) & 255;
		}
		return version;
	};
	this.children =		function (index) {
		var kids = new Array();
		var len = index + 8 + this.length(index);
		var i = index+8;
		while (i < len) {
			kids.push(i);
			i += 8 + this.length(i);
		}
		return kids;
	};
	this.findNode =		function (index, tag) {
		var kids = this.children(index);
		for (child in kids) {
			if (this.name(kids[child]) == tag) {
				return kids[child];
			}
		}
		return -1;
	};
}

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
