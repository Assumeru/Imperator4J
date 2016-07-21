(function($) {
	var $gid = Imperator.settings.gid,
	$time = 0,
	__ = Imperator.Language.__;

	function init() {
		Imperator.API.onError(parseErrorMessage);
		Imperator.API.onMessage(parseGameUpdate);
		Imperator.API.onOpen(sendUpdateRequest);
		Imperator.API.onDisconnect(handleDisconnect);
		addKickListeners();
		$('#owner-controls input[name="startgame"]').removeClass('hidden').hide();
	}

	function handleDisconnect($updateErrors) {
		if($updateErrors < Imperator.API.MAX_GAME_ERRORS) {
			setTimeout(sendUpdateRequest, 100 + $updateErrors * 400);
		} else {
			Imperator.Dialog.showDialog(__('An error has occurred'), __('Connection to the server has been lost.'), true);
		}
	}

	function parseErrorMessage($msg) {
		if($msg !== undefined && $msg !== '' && $msg.error !== undefined && $msg.request !== undefined && $msg.request.mode === 'update' && $msg.request.type === 'pregame') {
			Imperator.API.incrementDisconnects();
		}
	}

	function addKickListeners() {
		$('#player-list [data-kick]').click(kickPlayer);
	}

	function kickPlayer() {
		if(window.confirm(__('Are you sure you want to kick this player?'))) {
			Imperator.API.send({
				gid: $gid,
				mode: 'game',
				type: 'kick',
				uid: $(this).attr('data-kick')
			});
		}
	}

	function sendUpdateRequest() {
		Imperator.API.send({
			gid: $gid,
			mode: 'update',
			type: 'pregame',
			time: $time
		});
	}

	function parseGameUpdate($msg) {
		var $n, $playerLi, $playerList = $('#player-list');
		if($msg !== undefined && $msg !== '' && $msg.update !== undefined) {
			Imperator.API.resetDisconnects();
			$time = $msg.update;
			if($msg.gameState !== undefined) {
				window.alert($msg.gameState);
				window.location = $msg.redirect;
				return;
			}
			if($msg.players !== undefined) {
				$playerList.empty();
				for($n = 0; $n < $msg.players.length; $n++) {
					$playerLi = $('#template-player-list > li').clone();
					$playerLi.find('[data-template="name"]').text($msg.players[$n].name).css('color', '#' + $msg.players[$n].color);
					if($msg.players[$n].canKick) {
						$playerLi.find('[data-template="kick"]').attr('data-kick', $msg.players[$n].id);
					} else {
						$playerLi.find('[data-template="kick"]').hide();
					}
					if($msg.players[$n].id !== $msg.owner) {
						$playerLi.find('[data-template="owner"]').hide();
					}
					$playerList.append($playerLi);
				}
				addKickListeners();
				if($msg.players.length === $msg.maxPlayers) {
					$('#join-game').hide();
					$('#owner-controls input[name="startgame"]').show();
				} else {
					$('#join-game').show();
					$('#owner-controls input[name="startgame"]').hide();
				}
			}
			if($msg.ownerControls !== undefined) {
				$('#owner-controls').html($($msg.ownerControls).find('form'));
			}
			sendUpdateRequest();
		}
	}

	$(init);
})(jQuery);