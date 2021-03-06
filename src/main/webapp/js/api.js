Imperator.API = (function($) {
	var $open = false,
	$unloading = false,
	$onOpen = [],
	$onMessage = [],
	$onError = [],
	$mode,
	$longPollingURL = Imperator.settings.API.longpollingURL,
	$webSocketURL = Imperator.settings.API.webSocketURL,
	$ws,
	$disconnects = 0,
	$onDisconnect = [],
	$ajaxModifiers = (Imperator.settings.API.ajaxModifiers || []).slice(0),
	__ = Imperator.Language.__;
	if(window.console === undefined) {
		window.console = {};
	}
	if(typeof window.console.log !== 'function') {
		window.console.log = function() {};
	}
	if(typeof window.console.error !== 'function') {
		window.console.error = function() {};
	}
	if(!String.prototype.startsWith) {
		String.prototype.startsWith = function($searchString, $position){
			$position = $position || 0;
			return this.substr($position, $searchString.length) === $searchString;
		};
	}

	function connect() {
		$(window).on('beforeunload', function() {
			$unloading = true;
		});
		if($webSocketURL !== undefined && supportsWebSocket()) {
			fixWebSocketURL();
			$mode = 'WebSocket';
			makeWebSocketConnection();
		} else {
			$mode = 'LongPolling';
			makeLongPollingConnection();
		}
	}

	function fixWebSocketURL() {
		if(!$webSocketURL.startsWith('ws://') && !$webSocketURL.startsWith('wss://')) {
			if($webSocketURL.startsWith('http://') || $webSocketURL.startsWith('https://')) {
				$webSocketURL = $webSocketURL.replace('http', 'ws');
			} else if($webSocketURL.startsWith('//')) {
				if(window.location.protocol === 'https:') {
					$webSocketURL = 'wss:' + $webSocketURL;
				} else {
					$webSocketURL = 'ws:' + $webSocketURL;
				}
			} else {
				$webSocketURL = window.location.origin + $webSocketURL;
				$webSocketURL = $webSocketURL.replace('http', 'ws');
			}
		}
	}

	function makeWebSocketConnection() {
		$ws = new WebSocket($webSocketURL);
		$ws.onopen = function() {
			if(!$open) {
				$open = true;
				onOpen();
			}
		};
		$ws.onmessage = receiveWebSocket;
		$ws.onerror = function() {
			onError({});
		};
		$ws.onclose = function() {
			if(!$unloading) {
				onError({
					error: '',
					request: {
						mode: 'update',
						type: Imperator.settings.gid === 0 ? 'chat' : 'game'
					}
				});
			}
		};
	}

	function attemptWebSocketReconnect() {
		onError({
			error: '',
			request: {
				mode: 'update',
				type: Imperator.settings.gid === 0 ? 'chat' : 'game'
			}
		});
		$ws.close();
		makeWebSocketConnection();
	}

	function receiveWebSocket($msg) {
		try {
			$msg = JSON.parse($msg.data);
			onMessage($msg);
			if($msg !== undefined && $msg.error !== undefined) {
				onError($msg);
			}
		} catch($e) {
			console.log($msg);
		}
	}

	function makeLongPollingConnection() {
		$open = true;
		onOpen();
	}

	function supportsWebSocket() {
		try {
			return window.WebSocket !== undefined;
		} catch($e) {
			return false;
		}
	}

	function onOpen() {
		for(var $n = 0; $n < $onOpen.length; $n++) {
			$onOpen[$n]();
		}
	}

	function addOnOpen($function) {
		if($open) {
			$function();
		} else {
			$onOpen.push($function);
		}
	}

	function onError($response, $msg) {
		if($msg !== undefined && $msg.mode !== undefined && $msg.type !== undefined) {
			if($response === undefined || $response.error === undefined) {
				$response = {
					request: {
						mode: $msg.mode,
						type: $msg.type
					},
					error: $response
				};
			} else if($response.request === undefined || $response.request.mode === undefined || $response.request.type === undefined) {
				if($response.request === undefined) {
					$response.request = {};
				}
				if($response.request.mode === undefined) {
					$response.request.mode = $msg.mode;
				}
				if($response.request.type === undefined) {
					$response.request.type = $msg.type;
				}
			}
		}
		if($response.error === undefined) {
			$response.error = __('Unknown error.');
		}
		for(var $n = 0; $n < $onError.length; $n++) {
			$onError[$n]($response);
		}
	}

	function addOnError($function) {
		$onError.push($function);
	}

	function onMessage($json) {
		for(var $n = 0; $n < $onMessage.length; $n++) {
			$onMessage[$n]($json);
		}
	}

	function addOnMessage($function) {
		$onMessage.push($function);
	}

	function sendWebSocket($json) {
		if($ws.readyState === WebSocket.OPEN) {
			$ws.send(JSON.stringify($json));
		} else {
			attemptWebSocketReconnect();
		}
	}

	function sendLongPolling($json) {
		$.ajax(modifyAjax({
			method: 'POST',
			url: $longPollingURL,
			data: $json
		})).done(function($msg) {
			onMessage($msg);
			if($msg !== undefined && $msg.error !== undefined) {
				onError($msg, $json);
			}
		}).fail(function($msg) {
			if(!$unloading) {
				onError($msg.responseJSON, $json);
			}
		});
	}

	function send($json) {
		if($open) {
			if($mode === 'WebSocket') {
				return sendWebSocket($json);
			} else {
				return sendLongPolling($json);
			}
		}
		return false;
	}

	function resetDisconnects() {
		$disconnects = 0;
	}

	function incrementDisconnects() {
		$disconnects++;
		for(var $i = 0; $i < $onDisconnect.length; $i++) {
			$onDisconnect[$i]($disconnects);
		}
	}

	function addOnDisconnect($listener) {
		$onDisconnect.push($listener);
	}

	function addAjaxMod($listener) {
		$ajaxModifiers.push($listener);
	}

	function modifyAjax($ajax) {
		for(var $i = 0; $i < $ajaxModifiers.length; $i++) {
			$ajaxModifiers[$i]($ajax);
		}
		return $ajax;
	}

	connect();

	return {
		onOpen: addOnOpen,
		onError: addOnError,
		onMessage: addOnMessage,
		send: send,
		onDisconnect: addOnDisconnect,
		incrementDisconnects: incrementDisconnects,
		resetDisconnects: resetDisconnects,
		modifyLongPolling: addAjaxMod,
		MAX_CHAT_ERRORS: 5,
		MAX_GAME_ERRORS: 5
	};
})(jQuery);