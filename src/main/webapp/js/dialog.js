Imperator.Dialog = (function($) {
	function Dialog($html) {
		var $message = $html.find('[data-value="message"]'),
		$header = $html.find('[data-value="header"]');
		this.close = function() {
			closeDialog($html)
		};
		this.message = $message;
		this.header = $header;
		this.dialog = $html;
	}

	function isEscapeKey($e) {
		return $e.key === 'Escape'
			|| $e.key === 'Esc'
			|| $e.keyCode === 27
			|| $e.which === 27;
	}

	function closeDialog($dialog) {
		$dialog.fadeOut(500, function() {
			$dialog.remove();
		});
	}

	function showDialogForm($header, $message, $buttons, $canBeClosed, $class) {
		var $dialog = showDialog($header, $('#template-dialog-form > form').clone(), $canBeClosed, $class);
		$dialog.message.find('[data-value="dialog-form-message"]').append($message);
		$dialog.message.find('[data-value="dialog-form-controls"]').append($buttons);
		return $dialog;
	}

	function showDialog($header, $message, $canBeClosed, $class) {
		var $dialog = $('#template-dialog > div').clone(),
		$closeButton = $dialog.find('[data-value="close-button"]');
		$dialog.find('[data-value="header"]').text($header);
		if($class) {
			$dialog.find('[data-value="window"]').addClass($class);
		}
		if($message) {
			$dialog.find('[data-value="message"]').html($message);
		}
		$dialog.hide();
		$(document.body).append($dialog);
		$dialog.fadeIn(500);
		if(!$canBeClosed) {
			$closeButton.hide();
		} else {
			$(window).keyup(function($e) {
				if(isEscapeKey($e)) {
					$closeButton.click();
				}
			});
			$closeButton.click(function() {
				closeDialog($dialog);
			});
			$closeButton.focus();
		}
		return new Dialog($dialog);
	}

	function showWaitDialog() {
		return showDialog(Imperator.Language.__('Please wait...'), $('<p class="loading"></p>').text(Imperator.Language.__('Contacting server.')), false, 'loading');
	}

	function getButton($button) {
		return $('#template-dialog-button-' + $button + ' > button').clone();;
	}

	function showConfirmDialog($header, $message, $class, $okListener, $cancelListener) {
		var $ok = getButton('ok'),
		$cancel = getButton('cancel'),
		$dialog = showDialogForm($header, $message, $('<div>').append($ok).append(' ').append($cancel), false, $class);
		$ok.click(function($e) {
			$e.preventDefault();
			if($okListener === undefined || $okListener($dialog) !== true) {
				$dialog.close();
			}
		});
		$cancel.click(function($e) {
			$e.preventDefault();
			if($cancelListener === undefined || $cancelListener($dialog) !== true) {
				$dialog.close();
			}
		});
		$ok.focus();
		return $dialog;
	}

	return {
		showDialog: showDialog,
		showDialogForm: showDialogForm,
		closeDialog: closeDialog,
		showWaitDialog: showWaitDialog,
		showConfirmDialog: showConfirmDialog,
		getButton: getButton
	};
})(jQuery);