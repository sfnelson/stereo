function View(title) {}
View.prototype = {
	init:	function (titleText) {
	
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
			},
			
	collapse:function () {
				this.wrapper.className += " collapsed";
				this.collapsed = true;
			},
			
	show:	function () {
				this.refresh();
				this.wrapper.className = this.wrapper.className.replace("collapse","");
				this.collapsed = false;
			},
			
	refresh:function () {
				while (this.container.firstChild) {
					this.container.removeChild(this.container.firstChild);
				}
		
				for (i in this.items) {
					this.container.appendChild(this.items[i].node);
				}
			},
			
	put:	function (id, item) {
				if (!item) {
					this.items.push(id);
					id.container = this;
				}
				else {
					this.items[id] = item;
					item.container = this;
				}
			},
			
	get:	function (id) {
				return this.items[id];
			},
			
	sort:	function () { this.items.sort(); },
	
	clear:	function () { this.items = new Array(); },
	
	select:	function (item) {
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
			},
			
	activate:function () {
				//do nothing
			}
}

function Item() {}
Item.prototype = {
	init:	function () {
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
			},
			
	appendChild:function (inner, cls) {
				var node = document.createElement("DIV");
				node.className = cls;
				if ('string' == typeof(inner)) {
					node.appendChild(document.createTextNode(inner));
				}
				else {
					node.appendChild(inner);
				}
				this.node.appendChild(node);
			},
			
	selected:function () {
				this.node.className += " selected";
			},
			
	deselected:function () {	
				this.node.className = this.node.className.replace("selected", "");
			},
			
	clicked: function (e) {
				if (this.container) {
					this.container.select(this);
				}
			}
}

function Queue() {
	this.init();
}
Queue.prototype = {
	init:	function () {
				this.count = 0;
				this.first = 0;
				this.last = 0;
			},
	
	size:	function () {
				return this.count;
			},
	
	poll:	function () {
				if (this.first) {
					this.count--;
					var value = this.first.value;
					this.first = this.first.next;
					if (!this.first) this.last = 0;
					return value;
				}
				return 0;
			},
	
	offer:	function (value) {
				this.count++;
				var n = { next: 0, value: value };
				if (this.last) {
					this.last.next = n;
					this.last = n;
				}
				else {
					this.first = n;
					this.last = n;
				}
			}
};
