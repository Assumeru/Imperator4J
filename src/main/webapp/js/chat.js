(function($) {
	var $chatWindow,
	$gid = Imperator.settings.gid,
	$time = 0,
	$loading = true,
	$postgame = Imperator.settings.postgame !== undefined ? Imperator.settings.postgame : false,
	$canDelete = Imperator.settings.chat.canDelete,
	__ = Imperator.Language.__;

	function create() {
		var $chat = $('#chat');
		$chatWindow = $('#chat-window');
		Imperator.API.onDisconnect(handleDisconnect);
		Imperator.API.onError(parseErrorMessage);
		Imperator.API.onMessage(parseChatMessage);
		Imperator.API.onOpen(function() {
			var $chatScrolling = $chat.find('input[name="chatscrolling"]');
			$chat.submit(submitChat);
			$chatScrolling[0].checked = Imperator.Store.getBoolean('chatscrolling', true);
			$chatScrolling.change(chatScrolling);
			sendUpdateRequest();
			$chat.parent().removeClass('hidden');
		});
	}

	function chatScrolling($e) {
		$e.preventDefault();
		Imperator.Store.setItem('chatscrolling', this.checked);
	}

	function submitChat($e) {
		var $input = $('#chat input[name="message"]'),
		$message = $input.val();
		$e.preventDefault();
		if($message !== undefined && $message !== '') {
			$message += '';
			$message = $message.trim();
			if($message !== '') {
				$input.val('');
				Imperator.API.send({
					mode: 'chat',
					type: 'add',
					gid: $gid,
					message: $message
				});
			}
		}
	}

	function sendUpdateRequest() {
		if($gid === 0 || $postgame) {
			Imperator.API.send({
				gid: $gid,
				time: $time,
				mode: 'update',
				type: 'chat'
			});
		}
	}

	function handleDisconnect($updateErrors) {
		if($updateErrors < Imperator.API.MAX_CHAT_ERRORS) {
			setTimeout(sendUpdateRequest, 100 + $updateErrors * 400);
		} else if($updateErrors === Imperator.API.MAX_CHAT_ERRORS) {
			Imperator.Dialog.showDialog(__('An error has occurred'), __('Connection to the server has been lost.'), true);
		}
	}

	function parseErrorMessage($msg) {
		if($msg !== undefined && $msg !== '' && $msg.error !== undefined && $msg.request !== undefined && (($msg.request.mode === 'update' && $msg.request.type === 'chat') || $msg.request.mode === 'chat')) {
			if($msg.request.mode === 'update') {
				Imperator.API.incrementDisconnects();
			} else if($msg.error !== '') {
				Imperator.Dialog.showDialog(__('Chat Error'), $msg.error, true);
			}
		}
	}

	function parseChatMessage($msg) {
		if($loading) {
			$chatWindow.empty();
			$loading = false;
		}
		if($msg !== undefined && $msg !== '' && $msg.update !== undefined && $msg.messages !== undefined) {
			for(var $n = 0; $n < $msg.messages.length; $n++) {
				addMessage($msg.messages[$n]);
			}
			if($msg.messages.length > 0 && Imperator.Store.getBoolean('chatscrolling', true)) {
				$chatWindow.scrollTop($chatWindow[0].scrollHeight);
			}
			$time = $msg.update;
			sendUpdateRequest();
			Imperator.API.resetDisconnects();
		}
	}

	function addMessage($msg) {
		var $time = new Date($msg.time),
		$message = $('#template-chat-message > .chat').clone(),
		$deleteButton = $message.find('[data-type="delete"]'),
		$user = $message.find('.user'),
		$stamp = $message.find('time');
		if($canDelete) {
			$deleteButton.click(function($e) {
				$e.preventDefault();
				Imperator.API.send({
					uid: $msg.user.id,
					time: $msg.timestamp,
					gid: $gid,
					type: 'delete',
					mode: 'chat'
				});
				$message.remove();
			});
		} else {
			$deleteButton.hide();
		}
		$user.text($msg.user.name);
		if($msg.user.color !== undefined) {
			$user.css('color', '#'+$msg.user.color);
		}
		$stamp.attr('title', $time.toLocaleString());
		$stamp.attr('datetime', $msg.time);
		$stamp.text($time.toLocaleTimeString());
		$message.find('.message').text($msg.message);
		$chatWindow.append($message);
	}

	$(create);
})(jQuery);