(function($) {
	var HEADER_NAME = Imperator.settings.CSRF.header,
	COOKIE_NAME = Imperator.settings.CSRF.cookie;

	if(Imperator.settings.API === undefined) {
		return;
	}
	if(Imperator.settings.API.ajaxModifiers === undefined) {
		Imperator.settings.API.ajaxModifiers = [];
	}
	Imperator.settings.API.ajaxModifiers.push(function($request) {
		if($request.headers === undefined) {
			$request.headers = {};
		}
		$request.headers[HEADER_NAME] = getCookie(COOKIE_NAME);
	});

	function getCookie($name) {
		var $i, $cookie,
		$cookies = document.cookie.split(';');
		$name += '=';
		for($i = 0; $i < $cookies.length; $i++) {
			$cookie = $cookies[$i].trim();
			if($cookie.startsWith($name)) {
				return $cookie.slice($name.length);
			}
		}
	}
})(jQuery);