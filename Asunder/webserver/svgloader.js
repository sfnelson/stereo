/*
 * This file contains a single function that replaces the stereo controls with
 * svg images. This avoids having the svg clutter in the main file.
 */

function createSVGcontrols() {

	var ns = 'http://www.w3.org/2000/svg';
	var createImage = function (width, height) {
		var svg = document.createElementNS(ns, 'svg');
		svg.setAttribute('viewPort', '0, 0, '+width+', '+height);
		svg.setAttribute('width', width);
		svg.setAttribute('height', height);
		return svg;
	}
	var createPath = function (path, style) {
		var node = document.createElementNS(ns, 'path');
		node.setAttribute('d', path);
		node.setAttribute('style', style);
		return node;
	}
	var createCircle = function (x, y, radius, style) {
		var node = document.createElementNS(ns, 'circle');
		node.setAttribute('cx', x);
		node.setAttribute('cy', y);
		node.setAttribute('r', radius);
		node.setAttribute('style', style);
		return node;
	}
	var replace = function(id, img) {
		var node = document.getElementById(id);
		node.removeChild(node.firstChild);
		node.appendChild(img);
	}

	var barStyle = 'fill: #222;';
	var arrowStyle = 'fill: #222;stroke:#222;stroke-width:0.2em;stroke-linejoin:round;';
	var sliderTrackStyle = 'stroke:#888;stroke-width:0.4em;stroke-linecap:round;';
	var sliderButton = 'fill:#222;';
	
	var prev = createImage(33, 30);
	prev.appendChild(createPath('m 0,3 4,0 0,24 -4,0 z', barStyle));
	prev.appendChild(createPath('m 7,15 10,11 0,-22 z', arrowStyle));
	prev.appendChild(createPath('m 20,15 10,11 0,-22 z', arrowStyle));
	replace("previous", prev);

	var play = createImage(30, 30);
	play.appendChild(createPath('m 5,3 22,12 -22,12 z', arrowStyle));
	replace("play", play);

	var pause = createImage(17, 30);
	pause.appendChild(createPath('m 0,2 6,0 0,26 -6,0 z', barStyle));
	pause.appendChild(createPath('m 11,2 6,0 0,26 -6,0 z', barStyle));
	replace("pause", pause);
	
	var next = createImage(33, 30);
	next.appendChild(createPath('m 1,4 10,11 -10,11 z', arrowStyle));
	next.appendChild(createPath('m 15,4 10,11 -10,11 z', arrowStyle));
	next.appendChild(createPath('m 27,3 4,0 0,24 -4,0 z', barStyle));
	replace("next", next);
	
	var wrapper = document.createElement("SPAN");
	wrapper.id = "volume-wrapper";
	replace("volume", wrapper);
	
	var volume = createImage(300, 30);
	volume.appendChild(createPath('m 15,15 270,0', sliderTrackStyle));
	wrapper.appendChild(volume);
	
	var button = createCircle(15,15,8, sliderButton)
	button.id = "volume-control";
	volume.appendChild(button);
}