// ajax functions

var ajax = {
	request: function (string, callback) {
		if (window.netscape) {
			window.netscape.security.PrivilegeManager.enablePrivilege('UniversalBrowserRead');
		}
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