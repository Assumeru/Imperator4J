(function($) {
	var $game,
	$resizeTimeout,
	$emptyBorder,
	$currentTab = ['territories'],
	$time = 0,
	$dialogs = {},
	$updateErrors = 0,
	$missed = {
		chat: null,
		log: null
	};
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
		initEventListeners();
		$('#settings input[name="unitgraphics"][value="'+$unitGraphics+'"]').prop('checked', true);
		$emptyBorder = $('#territory [data-value="border"]');
		$emptyBorder.remove();
		parseHash();
		resetTabScroll();
		initRadialMenu();
		initTabSwiping();
	}

	function parseErrorMessage($msg) {
		console.error($msg);
		if($msg !== undefined && $msg !== '' && $msg.error !== undefined && $msg.request !== undefined && (($msg.request.mode == 'update' && $msg.request.type == 'game') || $msg.request.mode == 'game')) {
			if($msg.request.mode == 'update') {
				if($updateErrors < Imperator.API.MAX_GAME_ERRORS) {
					$updateErrors++;
					setTimeout(sendUpdateRequest, 100 + $updateErrors * 400);
				} else {
					Imperator.Dialog.showDialog(Imperator.settings.language.error, Imperator.settings.language.disconnected, true);
				}
			} else if($msg.error !== '') {
				Imperator.Dialog.showDialog(Imperator.settings.language.error, $msg.error, true);
			}
			if($msg.request.type == 'fortify' && $dialogs.fortify !== undefined) {
				$dialogs.fortify.close();
				delete $dialogs.fortify;
			} else if($msg.request.type == 'start-move' && $dialogs.startmove !== undefined) {
				$dialogs.startmove.close();
				delete $dialogs.startmove;
			} else if($msg.request.type == 'attack' && $dialogs.attack !== undefined) {
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
				if($game.turn == $game.player) {
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
					if($deltaX > 0) {
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

	function showMoveDialog($from, $to) {
		var $n, $territory,
		$ok = $(Imperator.settings.templates.okbutton),
		$cancel = $(Imperator.settings.templates.cancelbutton),
		$dialog = Imperator.Dialog.showDialogForm(
			Imperator.settings.language.move,
			Imperator.settings.templates.dialogformmove,
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
			$selectF.append('<option value="'+$from+'">'+$territory.name+' ('+$territory.units+')</option>');
			$selectF.prop('disabled', true);
			for($n = 0; $n < $territory.borders.length; $n++) {
				if($territory.borders[$n].owner == $territory.owner) {
					$selectT.append('<option value="'+$territory.borders[$n].id+'">'+$territory.borders[$n].name+' ('+$territory.borders[$n].units+')</option>');
				}
			}
			$selectT.focus();
		} else {
			$territory = $game.map.territories[$to];
			$selectT.append('<option value="'+$to+'">'+$territory.name+' ('+$territory.units+')</option>');
			$selectT.prop('disabled', true);
			for($n = 0; $n < $territory.borders.length; $n++) {
				if($territory.borders[$n].owner == $territory.owner && $territory.borders[$n].units > 1) {
					$selectF.append('<option value="'+$territory.borders[$n].id+'">'+$territory.borders[$n].name+' ('+$territory.borders[$n].units+')</option>');
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
		if($game.state == Imperator.Game.STATE_COMBAT && window.confirm(Imperator.settings.language.confirmmove)) {
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
		$ok = $(Imperator.settings.templates.okbutton),
		$cancel = $(Imperator.settings.templates.cancelbutton),
		$dialog = Imperator.Dialog.showDialogForm(
			Imperator.settings.language.attack,
			Imperator.settings.templates.dialogformattack,
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
			$selectF.append('<option value="'+$from+'">'+$game.map.territories[$from].name+' ('+$game.map.territories[$from].units+')</option>');
			$selectF.prop('disabled', true);
			$selectT.append('<option value="'+$to+'">'+$game.map.territories[$to].name+' ('+$game.map.territories[$to].units+')</option>');
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
			$selectF.append('<option value="'+$from+'">'+$territory.name+' ('+$territory.units+')</option>');
			$selectF.prop('disabled', true);
			for($n = 0; $n < $territory.borders.length; $n++) {
				if($territory.borders[$n].owner != $game.player) {
					$selectT.append('<option value="'+$territory.borders[$n].id+'" style="color: #'+$territory.borders[$n].owner.color+';">'+$territory.borders[$n].name+' ('+$territory.borders[$n].units+')</option>');
				}
			}
			$inputM.val($territory.units - 1);
			$selectT.focus();
		} else {
			$territory = $game.map.territories[$to];
			$selectT.append('<option value="'+$to+'">'+$territory.name+' ('+$territory.units+')</option>');
			$selectT.prop('disabled', true);
			for($n = 0; $n < $territory.borders.length; $n++) {
				if($territory.borders[$n].owner == $game.player && $territory.borders[$n].units > 1) {
					$selectF.append('<option value="'+$territory.borders[$n].id+'" style="color: #'+$territory.borders[$n].owner.color+';">'+$territory.borders[$n].name+' ('+$territory.borders[$n].units+')</option>');
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
		$ok = $(Imperator.settings.templates.okbutton),
		$cancel = $(Imperator.settings.templates.cancelbutton),
		$max = $(Imperator.settings.templates.maxbutton);
		if($dialogs.stackInput !== undefined) {
			$dialogs.stackInput.close();
		}
		$dialogs.stackInput = Imperator.Dialog.showDialogForm(
			Imperator.settings.language.fortify.replace('%1$s', $territory.name),
			Imperator.settings.templates.dialogformfortify,
			$('<div>').append($ok).append(' ').append($max).append(' ').append($cancel), true);
		$input = $dialogs.stackInput.message.find('[name="stack"]');
		$input.attr('max', $game.units);
		$input.focus();
		$dialogs.stackInput.message.find('form').submit(function($e) {
			var $num = Number.parseInt($input.val(), 10);
			$e.preventDefault();
			if(isNaN($num) || $num > $game.units || $num < 1 || !window.confirm(Imperator.settings.language.confirmfortify.replace('%1$d', $num).replace('%2$s', $territory.name))) {
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
		$menu.find('g').attr('class', 'disabled');
		if(($game.state === Imperator.Game.STATE_TURN_START || $game.state === Imperator.Game.STATE_FORTIFY) && $game.units > 0) {
			$stack.attr('class', '');
		}
		if($game.state === Imperator.Game.STATE_COMBAT || $game.state === Imperator.Game.STATE_TURN_START) {
			if($territory.owner == $game.player) {
				if($territory.units > 1 && $territory.bordersEnemyTerritory()) {
					$attackFrom.attr('class', '');
				}
			} else if($territory.canBeAttackedBy($game.player)) {
				$attackTo.attr('class', '');
			}
		} else if($game.state === Imperator.Game.STATE_POST_COMBAT && $territory.owner == $game.player && $game.units > 0) {
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
		if($game.player !== undefined && $game.player.playing && window.confirm(Imperator.settings.language.forfeit)) {
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
		if($game.turn == $game.player && ($game.state === Imperator.Game.STATE_TURN_START || $game.state === Imperator.Game.STATE_FORTIFY)) {
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
		if($game.turn == $game.player) {
			if($dialogs.endturn !== undefined) {
				$dialogs.endturn.close();
			}
			if($game.cards.getNumberOfCards() >= Imperator.Cards.MAX_CARDS && $game.conquered) {
				$dialog = Imperator.Dialog.showConfirmDialog(
					Imperator.settings.language.endturn,
					Imperator.settings.templates.discardcard,
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
			} else if($game.state == Imperator.Game.STATE_FORTIFY && $game.units > 0) {
				Imperator.Dialog.showConfirmDialog(
					Imperator.settings.language.confirmend,
					Imperator.settings.language.unitsleft,
					undefined,
					function() {
						send(Imperator.Cards.CARD_NONE);
					});
			} else if(window.confirm(Imperator.settings.language.confirmend)) {
				send(Imperator.Cards.CARD_NONE);
			}
		}
	}

	function sendFortify() {
		if($game.state == Imperator.Game.STATE_TURN_START) {
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
				$updateErrors = 0;
			}
			if($game === undefined && $msg.regions !== undefined && $msg.territories !== undefined && $msg.players !== undefined /*&& $msg.cards !== undefined*/ && $msg.units !== undefined && $msg.state !== undefined && $msg.turn !== undefined && $msg.conquered !== undefined) {
				$game = new Imperator.Game(Imperator.settings.gid, $msg.players, $msg.regions, $msg.territories, $msg.cards, $msg.units, $msg.state, $msg.turn, $msg.conquered);
				$game.player = $game.players[Imperator.settings.uid];
				$update.territories[0] = true;
				$update.turn[0] = true;
				$update.state[0] = true;
			}
			if($game !== undefined) {
				if($msg.state !== undefined && $msg.state !== $game.state) {
					if($msg.state == Imperator.Game.STATE_FORTIFY && $msg.units !== undefined && $dialogs.fortify !== undefined) {
						$dialogs.fortify.close();
						delete $dialogs.fortify;
					} else if($msg.state == Imperator.Game.STATE_POST_COMBAT && $dialogs.startmove !== undefined) {
						$dialogs.startmove.close();
						delete $dialogs.startmove;
					} else if($msg.state == Imperator.Game.STATE_FINISHED) {
						showGameOverDialog();
						return;
					}
					if($msg.state == Imperator.Game.STATE_COMBAT) {
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
			if($msg.request !== undefined && $msg.request.mode == 'update' && $msg.request.type == 'game') {
				sendUpdateRequest();
			}
			if($msg.mission !== undefined) {
				$('#players [data-value="mission-name"]').text($msg.mission.name);
				$('#players [data-value="mission-description"]').text($msg.mission.description);
			}
			if($msg.combatlog !== undefined) {
				addCombatLogs($msg.combatlog);
			}
			if($msg.messages !== undefined && $currentTab[0] != 'chatbox') {
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
			$entry = $(Imperator.settings.templates.combatlogentry);
			$time = $entry.find('time');
			$date = new Date($logs[$n].time);
			$time.attr('datetime', $logs[$n].time);
			$time.text($date.toLocaleTimeString());
			$time.attr('title', $date.toLocaleString());
			$entry.find('.message').html($logs[$n].message);
			$combatlog.append($entry);
		}
		if($('#log [name="logscrolling"]').prop('checked')) {
			$combatlog.scrollTop($combatlog[0].scrollHeight);
		}
		if($currentTab[0] != 'log') {
			$missed.log.add($logs.length);
		}
	}

	function showGameOverDialog() {
		var $dialog = Imperator.Dialog.showDialogForm(Imperator.settings.language.gameover, Imperator.settings.language.endedmessage, $(Imperator.settings.templates.okbutton), false);
		$dialog.message.find('form').submit(function($e) {
			$e.preventDefault();
			window.location.reload();
		});
	}

	function showAttackResultDialog($attack) {
		var $ok, $dialog, $again,
		$attacker = $game.map.territories[$attack.attacker],
		$defender = $game.map.territories[$attack.defender],
		$message = $(Imperator.settings.templates.dialogattackresult);
		$message.find('[data-value="attack-roll"]').html(getDice('attack', $attack.attackroll));
		$ok = $(Imperator.settings.templates.okbutton);
		if($attack.defendroll === undefined) {
			$message.find('[data-value="defend"]').hide();
			$dialog = Imperator.Dialog.showDialogForm(Imperator.settings.language.autorolldisabled.replace('%1$s', $defender.owner.name), $message, $ok, true);
		} else {
			$message.find('[data-value="defend-roll"]').html(getDice('defend', $attack.defendroll));
			if($attacker.owner == $defender.owner) {
				$dialog = Imperator.Dialog.showDialogForm(Imperator.settings.language.conquered.replace('%1$s', $defender.name), $message, $ok, true);
			} else {
				$again = $(Imperator.settings.templates.attackagainbutton).hide();
				$dialog = Imperator.Dialog.showDialogForm('', $message, $('<div>').append($again).append(' ').append($ok), true);
				$dialog.header.html(getVS($attacker, $defender));
				if($attacker.canAttack($defender) && $attacker.owner == $game.player) {
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
			$out += Imperator.settings.templates.die.replace('{$type}', $type).replace(/\{\$roll\}/g, $roll[$n]);
		}
		return $out;
	}

	function getVS($attacker, $defender) {
		return Imperator.settings.language.vs.replace('%1$s', '<span style="color: #'+$attacker.owner.color+';">'+$attacker.name+'</span>').replace('%2$s', '<span style="color: #'+$defender.owner.color+';">'+$defender.name+'</span>');
	}

	function updateAttacks() {
		var $n, $attack;
		for($n = 0; $n < $game.attacks.length && $dialogs.attack === undefined; $n++) {
			$attack = $game.attacks[$n];
			if($attack.defender.owner == $game.player) {
				$dialogs.attack = Imperator.Dialog.showDialogForm('', Imperator.settings.templates.dialogformdefend, Imperator.settings.templates.okbutton, false);
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

	function updateCards($newCard) {
		var $n, $ok, $dialog,
		$cards = $('#cards [data-value="card-list"]'),
		$controls = $('#card-controls'),
		$buttons = {
			4: $controls.find('[data-button="cards"][data-value="4"]'),
			6: $controls.find('[data-button="cards"][data-value="6"]'),
			8: $controls.find('[data-button="cards"][data-value="8"]'),
			10: $controls.find('[data-button="cards"][data-value="10"]')
		},
		$artillery = Imperator.settings.templates.card.replace('%1$s', Imperator.Cards.CARD_ARTILLERY).replace('%2$s', Imperator.settings.language.card[Imperator.Cards.CARD_ARTILLERY]),
		$infantry = Imperator.settings.templates.card.replace('%1$s', Imperator.Cards.CARD_INFANTRY).replace('%2$s', Imperator.settings.language.card[Imperator.Cards.CARD_INFANTRY]),
		$cavalry = Imperator.settings.templates.card.replace('%1$s', Imperator.Cards.CARD_CAVALRY).replace('%2$s', Imperator.settings.language.card[Imperator.Cards.CARD_CAVALRY]),
		$joker = Imperator.settings.templates.card.replace('%1$s', Imperator.Cards.CARD_JOKER).replace('%2$s', Imperator.settings.language.card[Imperator.Cards.CARD_JOKER]);
		if($newCard !== Imperator.Cards.CARD_NONE) {
			$ok = $(Imperator.settings.templates.okbutton);
			$dialog = Imperator.Dialog.showDialogForm(Imperator.settings.language.newcard,
				Imperator.settings.templates.card.replace('%1$s', $newCard).replace('%2$s', Imperator.settings.language.card[$newCard]),
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
			$cards.append($artillery);
		}
		for($n = 0; $n < $game.cards.infantry; $n++) {
			$cards.append($infantry);
		}
		for($n = 0; $n < $game.cards.cavalry; $n++) {
			$cards.append($cavalry);
		}
		for($n = 0; $n < $game.cards.jokers; $n++) {
			$cards.append($joker);
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
	}

	function updateUnitBoxes() {
		var $id, $units,
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
		if($currentTab[0] == 'chatbox') {
			$missed.chat.reset();
		} else if($currentTab[0] == 'log') {
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
		$a = $('<a></a>').attr('href', $territory.owner.link).css('color', '#'+$territory.owner.color).text($territory.owner.name),
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
				if($page[1] == 'players' || $page[1] == 'regions' || $page[1] == 'territories' || $page[1] == 'map' || $page[1] == 'settings' || $page[1] == 'log' || ($userIsPlayer && ($page[1] == 'cards' || $page[1] == 'chatbox'))) {
					$currentTab = [$page[1]];
				}
			} else if($page.length === 3 && $page[1] == 'territory') {
				if($game !== undefined && $game.map.territories[$page[2]] !== undefined) {
					$page.shift();
					$currentTab = $page;
					$a.text($game.map.territories[$page[1]].name);
					$a.attr('href', '#tab-territory-'+$page[1]);
					$territoryTab.show();
					fillTerritoryTab();
				}
			}
		} else {
			$currentTab = ['territories'];
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

	$(init);
})(jQuery);