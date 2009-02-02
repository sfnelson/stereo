function View(title) {
	
	this.init = function (titleText) {

		if (this.prototype && this.prototype.init) this.prototype.init();

		var view = this;

		this.wrapper = document.createElement("DIV");
		this.wrapper.className = "-stereo-container";
		this.wrapper.addEventListener("mousemove", function(e) {
			if (view.dragging) {
				e.stopPropagation();
				var t = e.target;
				while (t != document.body) {
					if (t.parentNode == view.container) {
						if (view.dragging.node.nextSibling && view.dragging.node.nextSibling == t) {
							view.container.insertBefore(t, view.dragging.node);
						}
						else if (t != view.dragging.node) {
							view.container.insertBefore(view.dragging.node, t);
						}
						break;
					}
					t = t.parentNode;
				}
			}
		}, false);
		this.wrapper.addEventListener("mouseup", function(e) {
			if (view.dragging) {
				e.stopPropagation();
				view.dragging = 0;
			}
		}, false);

		this.container = document.createElement("DIV");
		this.container.className = "-stereo-container-inner";
		this.items = new Array();

		if (titleText) {
			this.title = document.createElement("DIV");
			this.title.className = "-stereo-container-title";
			this.title.appendChild(document.createTextNode(titleText));
			
			this.title.addEventListener("click", function (e) {
				e.stopPropagation();
				view.activate();
			}, false);
			
			this.wrapper.appendChild(this.title);
		}
		
		this.wrapper.appendChild(this.container);

		this.node = function () {
			return this.wrapper;
		};
	};

	this.collapse = function () {
		this.wrapper.className += " collapsed";
		this.collapsed = true;
	};

	this.show = function () {
		this.refresh();
		this.wrapper.className = this.wrapper.className.replace("collapse","");
		this.collapsed = false;
	};

	this.refresh = function () {
		while (this.container.firstChild) {
			this.container.removeChild(this.container.firstChild);
		}

		for (i in this.items) {
			this.container.appendChild(this.items[i].node);
		}
	};

	this.put = function (id, item) {
		if (!item) {
			this.items.push(id);
			id.container = this;
		}
		else {
			this.items[id] = item;
			item.container = this;
		}
	};

	this.get = function (id) {
		return this.items[id];
	};

	this.sort = function () {
		this.items.sort();
	};

	this.clear = function () {
		this.items = new Array();
	};

	this.select = function (item) {
		if (this.selected) {
			this.selected.deselected();
		}
		
		if (this.selected == item) {
			this.selected = 0;
		}
		else {
			this.selected = item;
			item.selected();
		}
	};
	
	this.activate = function () {
		//do nothing by default
	};
}

function Item() {
	
	this.init = function () {
		this.node = document.createElement("DIV");
		this.node.className = "-stereo-container-item";
		this.container = 0;
		
		var dis = this;

		this.node.addEventListener("click", function (e) {
			e.stopPropagation();
			dis.clicked(e);
		}, false);
		this.node.addEventListener("mousedown", function (e) {
			e.stopPropagation();
			if (dis.container) {
				dis.container.dragging = dis;
			}
		}, false);
	};
	this.appendChild = function (inner, cls) {
		var node = document.createElement("DIV");
		node.className = cls;
		if ('string' == typeof(inner)) {
			node.appendChild(document.createTextNode(inner));
		}
		else {
			node.appendChild(inner);
		}
		this.node.appendChild(node);
	};
	this.selected = function () {
		this.node.className += " selected";
	};
	this.deselected = function () {	
		this.node.className = this.node.className.replace("selected", "");
	};
	this.clicked = function (e) {
		if (this.container) {
			sysout(this);
			sysout(this.container);
			this.container.select(this);
		}
	};
}

function Queue() {
	var size = 0;
	var first = 0;
	var last = 0;
	
	this.size = function () {
		return size;
	};
	
	this.poll = function () {
		if (first) {
			size--;
			var value = first.value;
			first = first.next;
			if (!first) last = 0;
			return value;
		}
		return 0;
	};
	
	this.offer = function (value) {
		size++;
		var n = { next: 0, value: value };
		if (last) {
			last.next = n;
			last = n;
		}
		else {
			first = n;
			last = n;
		}
	};
}