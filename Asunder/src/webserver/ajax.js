// ajax functions

var ajax = {
	request: function (string, callback) {
		var request = new XMLHttpRequest();
		if (callback) {
			request.onreadystatechange = function() {
				if (request.readyState == 4) {
					callback(request.responseText);
				}
			};
		}
		request.open("GET", string, true);
		request.overrideMimeType('text/plain; charset=x-user-defined');
		request.send(null);
	}
}