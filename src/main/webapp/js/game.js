(function($) {
	var $game,
	$resizeTimeout,
	$emptyBorder,
	$currentTab = ['territories'],
	$time = 0,
	$dialogs = {},
	$missed = {
		chat: null,
		log: null
	},
	__ = Imperator.Language.__;
	if(Number.parseInt === undefined) {
		Number.parseInt = parseInt;
	}
	function UnseenElement($element) {
		this.element = $element;
		this.amount = 0;
	}
	UnseenElement.prototype.add = function($number) {
		this.amount += $number;
		if(this.amount > 0) {
			this.element.text(this.amount);
		}
	};
	UnseenElement.prototype.reset = function() {
		this.amount = 0;
		this.element.text('');
	};

	function init() {
		var $unitGraphics = Imperator.Store.getItem('unit-graphics', 'default');
		$missed.chat = new UnseenElement($('[href="#tab-chatbox"] .number'));
		$missed.log = new UnseenElement($('[href="#tab-log"] .number'));
		Imperator.API.onError(parseErrorMessage);
		Imperator.API.onMessage(parseUpdateMessage);
		Imperator.API.onOpen(sendUpdateRequest);
		Imperator.API.onDisconnect(handleDisconnect);
		initEventListeners();
		$('#settings input[name="unitgraphics"][value="'+$unitGraphics+'"]').prop('checked', true);
		$emptyBorder = $('#territory [data-value="border"]');
		$emptyBorder.remove();
		parseHash();
		resetTabScroll();
		initRadialMenu();
		initTabSwiping();
	}

	function handleDisconnect($updateErrors) {
		if($updateErrors < Imperator.API.MAX_GAME_ERRORS) {
			setTimeout(sendUpdateRequest, 100 + $updateErrors * 400);
		} else if($updateErrors === Imperator.API.MAX_GAME_ERRORS) {
			Imperator.Dialog.showDialog(__('An error has occurred'), __('Connection to the server has been lost.'), true);
		}
	}

	function parseErrorMessage($msg) {
		if($msg !== undefined && $msg !== '' && $msg.error !== undefined && $msg.request !== undefined && (($msg.request.mode === 'update' && $msg.request.type === 'game') || $msg.request.mode === 'game')) {
			if($msg.request.mode === 'update') {
				Imperator.API.incrementDisconnects();
			} else if($msg.error !== '') {
				Imperator.Dialog.showDialog(__('An error has occurred'), $msg.error, true);
			}
			if($msg.request.type === 'fortify' && $dialogs.fortify !== undefined) {
				$dialogs.fortify.close();
				delete $dialogs.fortify;
			} else if($msg.request.type === 'start-move' && $dialogs.startmove !== undefined) {
				$dialogs.startmove.close();
				delete $dialogs.startmove;
			} else if($msg.request.type === 'attack' && $dialogs.attack !== undefined) {
				$dialogs.attack.close();
				delete $dialogs.attack;
			}
		}
	}

	function initEventListeners() {
		var $window = $(window);
		Imperator.Map.onLoad(function() {
			$('#map svg g[id]').click(function() {
				window.location = '#tab-territory-'+this.id;
			}).on('contextmenu', function($e) {
				if($game.turn === $game.player) {
					$e.preventDefault();
					showRadialMenu(this.id, $e.pageX, $e.pageY);
				}
			});
		});
		$window.on('hashchange', function($e) {
			var $previous = $currentTab[0];
			parseHash();
			updateTab($previous);
		});
		$window.resize(function() {
			clearTimeout($resizeTimeout);
			$resizeTimeout = setTimeout(resetTabScroll, 250);
		});
		$('#settings input[name="unitgraphics"]').change(setUnitGraphics)
		$('#settings input[name="autoroll"]').change(setAutoRoll)
		$('#regions [data-button="highlight"]').click(highlightRegion);
		$('#controls-box [data-button="stack"]').click(sendFortify);
		$('#controls-box [data-button="endturn"]').click(sendEndTurn);
		$('#controls-box [data-button="forfeit"]').click(sendForfeit);
		$('#controls-box [data-button="move"]').click(sendMove);
		$('#card-controls [data-button="cards"]').click(sendCards);
	}

	function initRadialMenu() {
		var $radialMenu = $('#radial-menu');
		function closeRadialMenu() {
			$('#radial-menu').hide();
		}
		$radialMenu.mouseleave(closeRadialMenu);
		$radialMenu.find('.inner').click(closeRadialMenu);
		$radialMenu.find('[data-button="stack"]').click(function() {
			showFortifyFor($radialMenu.attr('data-territory'));
			closeRadialMenu();
		});
		$radialMenu.find('[data-button="attack-to"]').click(function() {
			showAttackDialog(undefined, $radialMenu.attr('data-territory'));
			closeRadialMenu();
		});
		$radialMenu.find('[data-button="attack-from"]').click(function() {
			showAttackDialog($radialMenu.attr('data-territory'));
			closeRadialMenu();
		});
		$radialMenu.find('[data-button="move-to"]').click(function() {
			showMoveDialog(undefined, $radialMenu.attr('data-territory'));
			closeRadialMenu();
		});
		$radialMenu.find('[data-button="move-from"]').click(function() {
			showMoveDialog($radialMenu.attr('data-territory'));
			closeRadialMenu();
		});
	}

	function initTabSwiping() {
		var $touches = {};
		$('.swipe-panes').on('touchstart', function($e) {
			$e = $e.originalEvent;
			$touches = {
				x: $e.touches[0].clientX,
				y: $e.touches[0].clientY
			};
		});
		$('.swipe-panes').on('touchmove', function($e) {
			var $deltaX, $deltaY, $absX;
			if($touches.x !== undefined && $touches.y !== undefined) {
				$e = $e.originalEvent;
				$deltaX = $touches.x - $e.touches[0].clientX;
				$deltaY = $touches.y - $e.touches[0].clientY;
				$absX = Math.abs($deltaX);
				if(Math.abs($deltaY) < $absX && $absX > 10) {
					if($deltaX < 0) {
						swipeToTab('prev');
					} else {
						swipeToTab('next');
					}
					$touches = {};
				}
			}
		});
	}

	function sendUpdateRequest() {
		Imperator.API.send({
			mode: 'update',
			type: 'game',
			gid: Imperator.settings.gid,
			time: $time
		});
	}

	function getTerritoryOption($territory) {
		return $('<option></option>').attr('value', $territory.id).text($territory.name + ' (' + $territory.units + ')');
	}

	function showMoveDialog($from, $to) {
		var $n, $territory,
		$ok = Imperator.Dialog.getButton('ok'),
		$cancel = Imperator.Dialog.getButton('cancel'),
		$dialog = Imperator.Dialog.showDialogForm(
			__('Move'),
			$('#template-dialog-form-move > div').clone(),
			$('<div>').append($ok).append(' ').append($cancel), true),
		$inputM = $dialog.message.find('[name="move"]'),
		$selectF = $dialog.message.find('[name="from"]'),
		$selectT = $dialog.message.find('[name="to"]');
		function change() {
			var $mMax = Math.min($game.map.territories[$selectF.val()].units - 1, $game.units),
			$mVal = $inputM.val();
			$inputM.attr('max', $mMax);
			if($mVal !== '' && !isNaN($mVal) && $mVal > 0) {
				$inputM.val(Math.min($mMax, $mVal));
			}
		}
		if($from !== undefined) {
			$territory = $game.map.territories[$from];
			$selectF.append(getTerritoryOption($territory));
			$selectF.prop('disabled', true);
			for($n = 0; $n < $territory.borders.length; $n++) {
				if($territory.borders[$n].owner === $territory.owner) {
					$selectT.append(getTerritoryOption($territory.borders[$n]));
				}
			}
			$selectT.focus();
		} else {
			$territory = $game.map.territories[$to];
			$selectT.append(getTerritoryOption($territory));
			$selectT.prop('disabled', true);
			for($n = 0; $n < $territory.borders.length; $n++) {
				if($territory.borders[$n].owner === $territory.owner && $territory.borders[$n].units > 1) {
					$selectF.append(getTerritoryOption($territory.borders[$n]));
				}
			}
			$selectF.focus();
		}
		$dialog.message.find('form').submit(function($e) {
			$e.preventDefault();
			$dialog.close();
			Imperator.API.send({
				gid: $game.id,
				mode: 'game',
				type: 'move',
				to: $selectT.val(),
				from: $selectF.val(),
				move: $inputM.val()
			});
		});
		$selectF.change(change);
		change();
		$cancel.click(function($e) {
			$e.preventDefault();
			$dialog.close();
		});
	}

	function sendMove() {
		if($game.state === Imperator.Game.STATE_COMBAT && window.confirm(__('Are you sure you want to stop attacking?'))) {
			if($dialogs.startmove !== undefined) {
				$dialogs.startmove.close();
			}
			$dialogs.startmove = Imperator.Dialog.showWaitDialog();
			Imperator.API.send({
				mode: 'game',
				type: 'start-move',
				gid: $game.id
			});
		}
	}

	function setAutoRoll() {
		Imperator.API.send({
			mode: 'game',
			gid: $game.id,
			type: 'autoroll',
			autoroll: this.checked
		});
	}

	function showAttackDialog($from, $to, $prevUnits, $prevMove) {
		var $territory, $n,
		$ok = Imperator.Dialog.getButton('ok'),
		$cancel = Imperator.Dialog.getButton('cancel'),
		$dialog = Imperator.Dialog.showDialogForm(
			__('Attack'),
			$('#template-dialog-form-attack > div').clone(),
			$('<div>').append($ok).append(' ').append($cancel), true),
		$selectF = $dialog.message.find('[name="from"]'),
		$selectT = $dialog.message.find('[name="to"]'),
		$inputA = $dialog.message.find('[name="attack"]'),
		$inputM = $dialog.message.find('[name="move"]'),
		$move = $dialog.message.find('[data-value="move"]');
		function change() {
			var $mMax = $game.map.territories[$selectF.val()].units - 1,
			$aMax = Math.min(3, $mMax),
			$aVal = $inputA.val(),
			$mVal = $inputM.val(),
			$defender = $game.map.territories[$selectT.val()];
			$inputA.attr('max', $aMax);
			if($aVal !== '' && !isNaN($aVal) && $aVal > 0) {
				$inputA.val(Math.min($aMax, $aVal));
			}
			$inputM.attr('max', $mMax);
			if($mVal !== '' && !isNaN($mVal) && $mVal > 0) {
				$inputM.val(Math.min($mMax, $mVal));
			}
			if($aMax < $defender.units || $defender.units > 2) {
				$move.hide();
			} else {
				$move.show();
			}
		}
		if($from !== undefined && $to !== undefined) {
			$selectF.append(getTerritoryOption($game.map.territories[$from]));
			$selectF.prop('disabled', true);
			$selectT.append(getTerritoryOption($game.map.territories[$to]));
			$selectT.prop('disabled', true);
			if($prevMove !== undefined) {
				$inputM.val($prevMove);
			}
			if($prevUnits !== undefined) {
				$inputA.val($prevUnits);
			}
			$ok.focus();
		} else if($from !== undefined) {
			$territory = $game.map.territories[$from];
			$selectF.append(getTerritoryOption($territory));
			$selectF.prop('disabled', true);
			for($n = 0; $n < $territory.borders.length; $n++) {
				if($territory.borders[$n].owner !== $game.player) {
					$selectT.append(getTerritoryOption($territory.borders[$n]));
				}
			}
			$inputM.val($territory.units - 1);
			$selectT.focus();
		} else {
			$territory = $game.map.territories[$to];
			$selectT.append(getTerritoryOption($territory));
			$selectT.prop('disabled', true);
			for($n = 0; $n < $territory.borders.length; $n++) {
				if($territory.borders[$n].owner === $game.player && $territory.borders[$n].units > 1) {
					$selectF.append(getTerritoryOption($territory.borders[$n]));
				}
			}
			$selectF.focus();
		}
		$selectF.change(change);
		$selectT.change(change);
		change();
		$cancel.click(function($e) {
			$e.preventDefault();
			$dialog.close();
		});
		$dialog.message.find('form').submit(function($e) {
			$e.preventDefault();
			Imperator.API.send({
				gid: $game.id,
				mode: 'game',
				type: 'attack',
				to: $selectT.val(),
				from: $selectF.val(),
				units: $inputA.val(),
				move: $inputM.val()
			});
			$dialog.close();
			if($dialogs.attack !== undefined) {
				$dialogs.attack.close();
			}
			$dialogs.attack = Imperator.Dialog.showWaitDialog();
		});
	}

	function showFortifyFor($id) {
		var $input,
		$territory = $game.map.territories[$id],
		$ok = Imperator.Dialog.getButton('ok'),
		$cancel = Imperator.Dialog.getButton('cancel'),
		$max = Imperator.Dialog.getButton('max');
		if($dialogs.stackInput !== undefined) {
			$dialogs.stackInput.close();
		}
		$dialogs.stackInput = Imperator.Dialog.showDialogForm(
			__('Fortify %1$s', $territory.name),
			$('#template-dialog-form-fortify > div').clone(),
			$('<div>').append($ok).append(' ').append($max).append(' ').append($cancel), true);
		$input = $dialogs.stackInput.message.find('[name="stack"]');
		$input.attr('max', $game.units);
		$input.focus();
		$dialogs.stackInput.message.find('form').submit(function($e) {
			var $num = Number.parseInt($input.val(), 10);
			$e.preventDefault();
			if(isNaN($num) || $num > $game.units || $num < 1 || !window.confirm(__(Imperator.Language.resolve('Are you sure you want to place one unit in %2$s?', 'Are you sure you want to place %1$d units in %2$s?', $num), $num, $territory.name))) {
				$input.focus();
			} else {
				$dialogs.stackInput.close();
				delete $dialogs.stackInput;
				Imperator.API.send({
					mode: 'game',
					type: 'place-units',
					gid: $game.id,
					units: $num,
					territory: $territory.id
				});
			}
		});
		$cancel.click(function($e) {
			$e.preventDefault();
			$dialogs.stackInput.close();
			delete $dialogs.stackInput;
		});
		$max.click(function($e) {
			$e.preventDefault();
			$input.val($game.units);
		});
	}

	function showRadialMenu($id, $x, $y) {
		var $menu = $('#radial-menu'),
		$stack = $menu.find('[data-button="stack"]'),
		$moveTo = $menu.find('[data-button="move-to"]'),
		$moveFrom = $menu.find('[data-button="move-from"]'),
		$attackTo = $menu.find('[data-button="attack-to"]'),
		$attackFrom = $menu.find('[data-button="attack-from"]'),
		$territory = $game.map.territories[$id];
		$menu.find('[data-button]').attr('class', 'disabled');
		if(($game.state === Imperator.Game.STATE_TURN_START || $game.state === Imperator.Game.STATE_FORTIFY) && $game.units > 0) {
			$stack.attr('class', '');
		}
		if($game.state === Imperator.Game.STATE_COMBAT || $game.state === Imperator.Game.STATE_TURN_START) {
			if($territory.owner === $game.player) {
				if($territory.units > 1 && $territory.bordersEnemyTerritory()) {
					$attackFrom.attr('class', '');
				}
			} else if($territory.canBeAttackedBy($game.player)) {
				$attackTo.attr('class', '');
			}
		} else if($game.state === Imperator.Game.STATE_POST_COMBAT && $territory.owner === $game.player && $game.units > 0) {
			if($territory.units > 1 && $territory.bordersFriendlyTerritory()) {
				$moveFrom.attr('class', '');
			}
			if($territory.canReceiveReinforcements()) {
				$moveTo.attr('class', '');
			}
		}
		$menu.attr('data-territory', $id);
		$menu.css('left', $x - $menu.outerWidth() / 2);
		$menu.css('top', $y - $menu.outerHeight() / 2);
		$menu.show();
	}

	function sendForfeit() {
		if($game.player !== undefined && $game.player.playing && window.confirm(__('Are you sure you want to forfeit?'))) {
			Imperator.API.send({
				mode: 'game',
				gid: $game.id,
				type: 'forfeit'
			});
		}
	}

	function sendCards() {
		var $this = $(this),
		$num = $this.attr('data-value');
		if($game.turn === $game.player && ($game.state === Imperator.Game.STATE_TURN_START || $game.state === Imperator.Game.STATE_FORTIFY)) {
			if($dialogs.playcards !== undefined) {
				$dialogs.playcards.close();
			}
			$dialogs.playcards = Imperator.Dialog.showWaitDialog();
			Imperator.API.send({
				gid: $game.id,
				mode: 'game',
				type: 'play-cards',
				units: $num
			});
		}
	}

	function sendEndTurn() {
		var $dialog, $n, $cards, $num;
		function send($card) {
			$dialogs.endturn = Imperator.Dialog.showWaitDialog();
			Imperator.API.send({
				gid: $game.id,
				mode: 'game',
				type: 'end-turn',
				card: $card
			});
		}
		if($game.turn === $game.player) {
			if($dialogs.endturn !== undefined) {
				$dialogs.endturn.close();
			}
			if($game.cards.getNumberOfCards() >= Imperator.Cards.MAX_CARDS && $game.conquered) {
				$dialog = Imperator.Dialog.showConfirmDialog(
					__('End turn'),
					$('#template-dialog-form-discard > div').clone(),
					'discard-dialog',
					function($dialog) {
						send($dialog.message.find('[name="discard"]:checked').val());
					});
				$cards = [Imperator.Cards.CARD_ARTILLERY, Imperator.Cards.CARD_INFANTRY, Imperator.Cards.CARD_CAVALRY, Imperator.Cards.CARD_JOKER];
				for($n = 0; $n < $cards.length; $n++) {
					$num = $game.cards.getCard($cards[$n]);
					if($num < 1) {
						$dialog.message.find('[data-card="'+$cards[$n]+'"]').hide();
					} else {
						$dialog.message.find('[data-card="'+$cards[$n]+'"] .number').text($num);
					}
				}
			} else if($game.state === Imperator.Game.STATE_FORTIFY && $game.units > 0) {
				Imperator.Dialog.showConfirmDialog(
					__('Are you sure you want to end your turn?'),
					__('You still have units left to place.'),
					undefined,
					function() {
						send(Imperator.Cards.CARD_NONE);
					});
			} else if(window.confirm(__('Are you sure you want to end your turn?'))) {
				send(Imperator.Cards.CARD_NONE);
			}
		}
	}

	function sendFortify() {
		if($game.state === Imperator.Game.STATE_TURN_START) {
			if($dialogs.fortify !== undefined) {
				$dialogs.fortify.close();
			}
			$dialogs.fortify = Imperator.Dialog.showWaitDialog();
			Imperator.API.send({
				gid: $game.id,
				mode: 'game',
				type: 'fortify'
			});
		}
	}

	function highlightRegion() {
		var $n,
		$this = $(this),
		$id = $this.attr('data-region'),
		$region = $game.map.regions[$id];
		if($this.attr('data-highlight') === 'true') {
			$('#map svg g[id]').attr('class', '');
			$this.attr('data-highlight', 'false');
		} else {
			$('#map svg g[id]').attr('class', 'active border');
			for($n = 0; $n < $region.territories.length; $n++) {
				$('#'+$region.territories[$n].id).attr('class', 'active');
			}
			$('#regions .btn[data-highlight="true"]').attr('data-highlight', 'false');
			$this.attr('data-highlight', 'true');
		}
	}

	function setUnitGraphics() {
		var $this = $(this);
		Imperator.Store.setItem('unit-graphics', $this.val());
		updateUnitBoxes();
	}

	function parseUpdateMessage($msg) {
		var $id, $key, $n,
		$update = {
			territories: [false, updateTerritories],
			turn: [false, updateTurn],
			state: [false, updateState]
		};
		if($msg !== undefined && $msg !== '') {
			if($msg.update !== undefined && $msg.update > $time) {
				if($time === 0) {
					$('#combatlog > div').empty();
				}
				$time = $msg.update;
				Imperator.API.resetDisconnects();
			}
			if($game === undefined && $msg.regions !== undefined && $msg.territories !== undefined && $msg.players !== undefined && $msg.units !== undefined && $msg.state !== undefined && $msg.turn !== undefined && $msg.conquered !== undefined) {
				$game = new Imperator.Game(Imperator.settings.gid, $msg.players, $msg.regions, $msg.territories, $msg.cards, $msg.units, $msg.state, $msg.turn, $msg.conquered);
				$game.player = $game.players[Imperator.settings.uid];
				$update.territories[0] = true;
				$update.turn[0] = true;
				$update.state[0] = true;
			}
			if($game !== undefined) {
				if($msg.state !== undefined && $msg.state !== $game.state) {
					if($msg.state === Imperator.Game.STATE_FORTIFY && $msg.units !== undefined && $dialogs.fortify !== undefined) {
						$dialogs.fortify.close();
						delete $dialogs.fortify;
					} else if($msg.state === Imperator.Game.STATE_POST_COMBAT && $dialogs.startmove !== undefined) {
						$dialogs.startmove.close();
						delete $dialogs.startmove;
					} else if($msg.state === Imperator.Game.STATE_FINISHED) {
						showGameOverDialog();
						return;
					}
					if($msg.state === Imperator.Game.STATE_COMBAT) {
						updateCards(Imperator.Cards.CARD_NONE);
					}
					$game.state = $msg.state;
					$update.state[0] = true;
				}
				if($msg.territories !== undefined) {
					for($id in $msg.territories) {
						if($msg.territories[$id].units !== undefined) {
							$game.map.territories[$id].units = $msg.territories[$id].units;
							$update.territories[0] = true;
						}
						if($msg.territories[$id].uid !== undefined) {
							$game.map.territories[$id].owner = $game.players[$msg.territories[$id].uid];
							$update.territories[0] = true;
						}
					}
				}
				if($msg.cards !== undefined) {
					for($key in $msg.cards) {
						$game.cards.setCard($key, $msg.cards[$key]);
					}
					updateCards(Imperator.Cards.CARD_NONE);
				}
				if($msg.card !== undefined && $msg.card !== Imperator.Cards.CARD_NONE) {
					$game.cards.setCard($msg.card, $game.cards.getCard($msg.card) + 1);
					updateCards($msg.card);
				}
				if($msg.turn !== undefined && $msg.turn !== $game.turn.id) {
					$game.turn = $game.players[$msg.turn];
					$update.turn[0] = true;
				}
				if($msg.units !== undefined) {
					$game.units = $msg.units;
					updateUnits();
				}
				if($msg.attacks !== undefined) {
					$game.attacks = [];
					for($n = 0; $n < $msg.attacks.length; $n++) {
						$game.attacks.push(new Imperator.Attack(
							$game.map.territories[$msg.attacks[$n].attacker],
							$game.map.territories[$msg.attacks[$n].defender],
							$msg.attacks[$n].attackroll
						));
					}
					updateAttacks();
				}
				if($msg.attack !== undefined && $dialogs.attack !== undefined) {
					$dialogs.attack.close();
					delete $dialogs.attack;
					showAttackResultDialog($msg.attack);
				}
				if($msg.players !== undefined) {
					for($key in $msg.players) {
						if($msg.players[$key].playing !== undefined) {
							$game.players[$key].playing = $msg.players[$key].playing;
						}
					}
					if($game.player === undefined || !$game.player.playing) {
						$('#controls-box [data-button="forfeit"]').hide();
					}
				}
				if($msg.conquered !== undefined) {
					$game.conquered = $msg.conquered;
				}
			}
			if($msg.autoroll !== undefined) {
				$('#settings input[name="autoroll"]').prop('checked', $msg.autoroll);
			}
			if($msg.request !== undefined && $msg.request.mode === 'update' && $msg.request.type === 'game') {
				sendUpdateRequest();
			}
			if($msg.mission !== undefined) {
				$('#players [data-value="mission-name"]').text($msg.mission.name);
				$('#players [data-value="mission-description"]').text($msg.mission.description);
			}
			if($msg.combatlog !== undefined) {
				addCombatLogs($msg.combatlog);
			}
			if($msg.messages !== undefined && $currentTab[0] !== 'chatbox') {
				$missed.chat.add($msg.messages.length);
			}
		}
		for($key in $update) {
			if($update[$key][0]) {
				$update[$key][1]();
			}
		}
	}

	function addCombatLogs($logs) {
		var $n, $entry, $time, $date,
		$combatlog = $('#combatlog > div');
		for($n = 0; $n < $logs.length; $n++) {
			$entry = $('#template-combatlog-entry > div').clone();
			$time = $entry.find('time');
			$date = new Date($logs[$n].time);
			$time.attr('datetime', $logs[$n].time);
			$time.text($date.toLocaleTimeString());
			$time.attr('title', $date.toLocaleString());
			$entry.find('.message').html(getLogMessage($logs[$n]));
			$combatlog.append($entry);
		}
		if($('#log [name="logscrolling"]').prop('checked')) {
			$combatlog.scrollTop($combatlog[0].scrollHeight);
		}
		if($currentTab[0] !== 'log') {
			$missed.log.add($logs.length);
		}
	}

	function getTerritoryEntry($territory, $owner) {
		return $('<a></a>').attr('href', '#' + $territory.id).css('color', '#' + $owner.color).text($territory.name).prop('outerHTML');
	}

	function getLogMessage($entry) {
		var $cards, $n;
		if($entry.type === 0) {
			return __($entry.message.message,
					$('#players [data-player="' + $entry.message.uid + '"] [data-value="name"]').html(),
					getTerritoryEntry($game.map.territories[$entry.message.territory], $game.players[$entry.message.uid]));
		} else if($entry.type === 1) {
			return __($entry.message.message,
					getTerritoryEntry($game.map.territories[$entry.message.attacking], $game.players[$entry.message.attacker]),
					getTerritoryEntry($game.map.territories[$entry.message.defending], $game.players[$entry.message.defender]),
					getDice('attack', $entry.message.attackRoll),
					getDice('defend', $entry.message.defendRoll));
		} else if($entry.type === 2 || $entry.type === 3) {
			return __($entry.message.message, $('#players [data-player="' + $entry.message.uid + '"] [data-value="name"]').html());
		}
		$cards = $('<div></div>');
		for($n = 0; $n < $entry.message.cards.length; $n++) {
			$cards.append(getCardTemplate($entry.message.cards[$n]));
		}
		return __(Imperator.Language.resolve($entry.message.singular, $entry.message.plural, $entry.message.units),
				$('#players [data-player="' + $entry.message.uid + '"] [data-value="name"]').html(),
				$cards.html(), $entry.message.units);
	}

	function showGameOverDialog() {
		var $dialog = Imperator.Dialog.showDialogForm(__('Game Over'), __('This game has ended.'), Imperator.Dialog.getButton('ok'), false);
		$dialog.message.find('form').submit(function($e) {
			$e.preventDefault();
			window.location.reload();
		});
	}

	function showAttackResultDialog($attack) {
		var $ok, $dialog, $again,
		$attacker = $game.map.territories[$attack.attacker],
		$defender = $game.map.territories[$attack.defender],
		$message = $('#template-dialog-attack-result > div').clone();
		$message.find('[data-value="attack-roll"]').html(getDice('attack', $attack.attackroll));
		$ok = Imperator.Dialog.getButton('ok');
		if($attack.defendroll === undefined) {
			$message.find('[data-value="defend"]').hide();
			$dialog = Imperator.Dialog.showDialogForm(__('%1$s has disabled Autoroll', $defender.owner.name), $message, $ok, true);
		} else {
			$message.find('[data-value="defend-roll"]').html(getDice('defend', $attack.defendroll));
			if($attacker.owner === $defender.owner) {
				$dialog = Imperator.Dialog.showDialogForm(__('%1$s has been conquered', $defender.name), $message, $ok, true);
			} else {
				$again = Imperator.Dialog.getButton('attack-again').hide();
				$dialog = Imperator.Dialog.showDialogForm('', $message, $('<div>').append($again).append(' ').append($ok), true);
				$dialog.header.html(getVS($attacker, $defender));
				if($attacker.canAttack($defender) && $attacker.owner === $game.player) {
					$again.show();
					$again.focus();
					$dialog.message.find('form').submit(function($e) {
						$e.preventDefault();
						showAttackDialog($attacker.id, $defender.id, $attack.attackroll.length, $attack.move);
						$dialog.close();
					});
				}
			}
		}
		$ok.click(function($e) {
			$e.preventDefault();
			$dialog.close();
		});
	}

	function getDice($type, $roll) {
		var $out = '', $n;
		for($n = 0; $n < $roll.length; $n++) {
			$out += '<div class="die d' + $roll[$n] + ' ' + $type + '">' + $roll[$n] + '</div>';
		}
		return $out;
	}

	function getSpan($text, $color) {
		return $('<span></span>').css('color', '#' + $color).text($text).prop('outerHTML');
	}

	function getVS($attacker, $defender) {
		return __('%1$s vs. %2$s', getSpan($attacker.name, $attacker.owner.color), getSpan($defender.name, $defender.owner.color));
	}

	function updateAttacks() {
		var $n, $attack;
		for($n = 0; $n < $game.attacks.length && $dialogs.attack === undefined; $n++) {
			$attack = $game.attacks[$n];
			if($attack.defender.owner === $game.player) {
				$dialogs.attack = Imperator.Dialog.showDialogForm('', $('#template-dialog-form-defend > div').clone(), Imperator.Dialog.getButton('ok'), false);
				$dialogs.attack.header.html(getVS($attack.attacker, $attack.defender));
				$dialogs.attack.message.find('[data-value="attack-roll"]').html(getDice('attack', $attack.roll));
				$dialogs.attack.message.find('form').submit(function($e) {
					$e.preventDefault();
					Imperator.API.send({
						mode: 'game',
						gid: $game.id,
						type: 'defend',
						to: $attack.defender.id,
						from: $attack.attacker.id,
						units: $dialogs.attack.message.find('[name="defend"]:checked').val()
					});
					if($dialogs.attack !== undefined) {
						$dialogs.attack.close();
					}
					$dialogs.attack = Imperator.Dialog.showWaitDialog();
				});
				break;
			}
		}
	}

	function getCardTemplate($card) {
		var $template = $('#template-card');
		return $template.find('.card').clone().attr('alt', __(Imperator.Cards.NAMES[$card])).attr('src', $template.attr('data-src').replace('-card-number-', $card));
	}

	function updateCards($newCard) {
		var $n, $ok, $dialog,
		$cards = $('#cards [data-value="card-list"]'),
		$controls = $('#card-controls'),
		$buttons = {
			4: $controls.find('[data-button="cards"][data-value="4"]'),
			6: $controls.find('[data-button="cards"][data-value="6"]'),
			8: $controls.find('[data-button="cards"][data-value="8"]'),
			10: $controls.find('[data-button="cards"][data-value="10"]')
		};
		if($newCard !== Imperator.Cards.CARD_NONE) {
			$ok = Imperator.Dialog.getButton('ok');
			$dialog = Imperator.Dialog.showDialogForm(__('You have received a new card!'),
				getCardTemplate($newCard),
				$ok, true, 'text-center');
			$ok.click(function($e) {
				$e.preventDefault();
				$dialog.close();
			});
		}
		if($dialogs.playcards !== undefined) {
			$dialogs.playcards.close();
			delete $dialogs.playcards;
			
		}
		$cards.empty();
		for($n = 0; $n < $game.cards.artillery; $n++) {
			$cards.append(getCardTemplate(Imperator.Cards.CARD_ARTILLERY));
		}
		for($n = 0; $n < $game.cards.infantry; $n++) {
			$cards.append(getCardTemplate(Imperator.Cards.CARD_INFANTRY));
		}
		for($n = 0; $n < $game.cards.cavalry; $n++) {
			$cards.append(getCardTemplate(Imperator.Cards.CARD_CAVALRY));
		}
		for($n = 0; $n < $game.cards.jokers; $n++) {
			$cards.append(getCardTemplate(Imperator.Cards.CARD_JOKER));
		}
		for($n in $buttons) {
			if($game.cards.canPlayCombination($n) && ($game.state === Imperator.Game.STATE_TURN_START || $game.state === Imperator.Game.STATE_FORTIFY)) {
				$buttons[$n].show();
			} else {
				$buttons[$n].hide();
			}
		}
	}

	function updateUnits() {
		var $box = $('#controls-box'),
		$unitsF = $box.find('[data-value="units-left-fortify"] .number'),
		$unitsM = $box.find('[data-value="units-left-move"] .number');
		$unitsF.text(0);
		$unitsM.text(0);
		if($game.state === Imperator.Game.STATE_TURN_START || $game.state === Imperator.Game.STATE_FORTIFY) {
			$unitsF.text($game.units);
		} else if($game.state === Imperator.Game.STATE_POST_COMBAT) {
			$unitsM.text($game.units);
		}
	}

	function updateTurn() {
		var $btn,
		$a = $('#controls-box .user');
		$a.css('color', '#'+$game.turn.color);
		$a.text($game.turn.name);
		$a.attr('href', $game.turn.link);
		if($game.turn === $game.player) {
			$('body').addClass('my-turn');
			$btn = $('#turn-controls [data-toggle="collapse"]');
			if($btn.hasClass('collapsed')) {
				$btn.click();
			}
		} else {
			if($dialogs.endturn !== undefined) {
				$dialogs.endturn.close();
				delete $dialogs.endturn;
			}
			$('body').removeClass('my-turn');
		}
	}

	function updateState() {
		var $box = $('#controls-box'),
		$stack = $box.find('[data-button="stack"]'),
		$move = $box.find('[data-button="move"]'),
		$unitsF = $box.find('[data-value="units-left-fortify"]'),
		$unitsM = $box.find('[data-value="units-left-move"]');
		$stack.css('display', 'none');
		$move.css('display', 'none');
		$unitsF.css('display', 'none');
		$unitsM.css('display', 'none');
		if($game.state === Imperator.Game.STATE_TURN_START || $game.state === Imperator.Game.STATE_FORTIFY) {
			$unitsF.css('display', '');
		}
		if($game.state === Imperator.Game.STATE_TURN_START) {
			$stack.css('display', '');
		} else if($game.state === Imperator.Game.STATE_COMBAT) {
			$move.css('display', '');
		} else if($game.state === Imperator.Game.STATE_POST_COMBAT) {
			$unitsM.css('display', '');
		}
	}

	function updateTerritories() {
		var $id, $territory, $player, $upt, $players = [], $a,
		$territories = $('#territories');
		for($id in $game.players) {
			$players[$id] = {
				territories: 0,
				units: 0
			};
		}
		for($id in $game.map.territories) {
			$territory = $game.map.territories[$id];
			$players[$territory.owner.id].territories++;
			$players[$territory.owner.id].units += $territory.units;
			$('#'+$id).css('fill', '#'+$game.players[$territory.owner.id].color);
			$territories.find('[data-territory="'+$id+'"] [data-value="units"]').text($territory.units);
			$a = $territories.find('[data-territory="'+$id+'"] .user');
			$a.css('color', '#'+$territory.owner.color);
			$a.text($territory.owner.name);
			$a.attr('href', $territory.owner.link);
		}
		for($id in $players) {
			$upt = {
				territories: $game.players[$id].getUnitsPerTurnFromTerritories($players[$id].territories),
				regions: $game.players[$id].getUnitsPerTurnFromRegions(),
			};
			$player = $('#players [data-player="'+$id+'"]');
			$player.find('[data-value="territories"]').text($players[$id].territories);
			$player.find('[data-value="units"]').text($players[$id].units);
			$player.find('[data-value="unitsperturn"]').text($upt.territories + $upt.regions);
			$player.find('[data-value="unitsperturn-regions"]').text($upt.regions);
			$player.find('[data-value="unitsperturn-territories"]').text($upt.territories);
			if($game.player !== undefined && $game.player.id == $id) {
				$('#controls-box [data-button="stack"] .number').text($upt.territories);
			}
		}
		updateRegionDivision();
		updateUnitBoxes();
		if($currentTab[0] === 'territory') {
			fillTerritoryTab();
		}
	}

	function updateUnitBoxes() {
		var $id,
		$unitGraphics = Imperator.Store.getItem('unit-graphics', 'default');
		for($id in $game.map.territories) {
			Imperator.Map.updateUnitBox($unitGraphics, $id, $game.map.territories[$id].units);
		}
	}

	function updateRegionDivision() {
		var $region, $players, $uid, $n, $territories, $div, $span;
		for($region in $game.map.regions) {
			$players = {};
			for($uid in $game.players) {
				$players[$uid] = 0;
			}
			$territories = $game.map.regions[$region].territories;
			for($n = 0; $n < $territories.length; $n++) {
				$players[$territories[$n].owner.id]++;
			}
			$div = $('#regions .region-division[data-region="'+$region+'"]');
			$div.empty();
			for($uid in $players) {
				$span = $('<div>');
				$span.css('backgroundColor', '#'+$game.players[$uid].color);
				$span.css('width', (100 * $players[$uid] / $territories.length) + '%');
				$span.attr('title', $game.players[$uid].name);
				$div.append($span);
			}
		}
	}

	function updateTab($current) {
		var $destination,
		$target = $('#'+$currentTab[0]),
		$parent = $target.parent(),
		$panes = $('#content .swipe-panes'),
		$current = $('#'+$current),
		$currentParent = $current.parent(),
		$nav = $('#content nav'),
		$tab = $nav.find('a[href|="#tab-'+$currentTab[0]+'"]').parent();
		$nav.find('li.active').removeClass('active');
		$tab.addClass('active');
		$nav.animate({
			scrollLeft: $tab.offset().left
		}, 500);
		function getDestination() {
			return $target.offset().left - getOffset($target, 'right') + $parent.scrollLeft();
		}
		if(!$currentParent.is($parent)) {
			$destination = ($currentParent.index() < $parent.index() ? 1 : -1) * $parent.outerWidth();
			$parent.scrollLeft(getDestination() - $destination);
			$panes.animate({
				scrollLeft: $destination
			}, 750, 'swing', function() {
				$parent.scrollLeft(getDestination());
			});
		} else {
			$parent.animate({
				scrollLeft: getDestination()
			}, 750);
		}
		if($currentTab[0] === 'chatbox') {
			$missed.chat.reset();
		} else if($currentTab[0] === 'log') {
			$missed.log.reset();
		}
	}

	function resetTabScroll() {
		updateTab($currentTab[0]);
	}

	function fillTerritoryTab() {
		var $n, $border, $bordering,
		$tab = $('#territory'),
		$territory = $game.map.territories[$currentTab[1]],
		$a = $('<span></span>').css('color', '#'+$territory.owner.color).text($territory.owner.name),
		$borders = $tab.find('[data-value="borders"]');
		$tab.find('[data-value="name"]').text($territory.name);
		$tab.find('[data-value="units"]').text($territory.units);
		$tab.find('[data-value="owner"]').html($a);
		$tab.find('[data-value="regions"]').html($('#territories [data-territory="'+$territory.id+'"] [data-value="regions"]').html());
		$tab.find('[data-value="flag"]').attr('src', getFlagFor($territory.id));
		$('#'+$territory.id).attr('class', 'active');
		$borders.empty();
		for($n = 0; $n < $territory.borders.length; $n++) {
			$bordering = $territory.borders[$n];
			$('#'+$bordering.id).attr('class', 'active border');
			$border = $emptyBorder.clone();
			$border.find('[data-value="border-name"]')
				.text($bordering.name)
				.attr('href', '#tab-territory-'+$bordering.id)
				.css('color', '#'+$bordering.owner.color);
			$border.find('[data-value="border-flag"]').attr('src', getFlagFor($bordering.id));
			$borders.append($border);
		}
	}

	function getFlagFor($territory) {
		return $('#territories [data-territory="'+$territory+'"] [data-value="flag"]').attr('src');
	}

	function getOffset($element, $side) {
		var $n, $add,
		$css = ['padding-?', 'border-?-width', 'margin-?'],
		$offset = 0;
		for($n = 0; $n < $css.length; $n++) {
			$add = Number.parseInt($element.css($css[$n].replace('?', $side)), 10);
			if(!isNaN($add)) {
				$offset += $add;
			}
		}
		return $offset;
	}

	function parseHash() {
		var $page = window.location.hash.replace('#', ''),
		$userIsPlayer = !$('#main').hasClass('not-player'),
		$a = $('#content nav a[href|="#tab-territory"]'),
		$territoryTab = $a.parent();
		$a.attr('href', '#tab-territory');
		$territoryTab.hide();
		$('#content svg g[id]').attr('class', '');
		if($page !== '') {
			$page = $page.split('-');
			if($page.length === 2) {
				if(['players', 'regions', 'territories', 'map', 'settings', 'log'].indexOf($page[1]) >= 0 || ($userIsPlayer && ($page[1] === 'cards' || $page[1] === 'chatbox'))) {
					$currentTab = [$page[1]];
				}
			} else if($page.length === 3 && $page[1] === 'territory' && $game !== undefined && $game.map.territories[$page[2]] !== undefined) {
				$page.shift();
				$currentTab = $page;
				$a.text($game.map.territories[$page[1]].name);
				$a.attr('href', '#tab-territory-'+$page[1]);
				$territoryTab.show();
				fillTerritoryTab();
			}
		} else {
			$currentTab = ['territories'];
			return;
		}
		window.location.hash = 'tab-'+$currentTab.join('-');
	}

	function swipeToTab($func) {
		var $page, $current,
		$tab = $('#content nav li.active')[$func]();
		if($tab.length !== 0 && !$tab.is(':hidden')) {
			$page = $tab.find('a').attr('href').split('-');
			$current = $currentTab[0];
			$currentTab = [$page[1]];
			updateTab($current);
		}
	}

	Imperator.Map.onLoad(init);
})(jQuery);