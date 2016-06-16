(function() {
	var $jed;

	function PluralString($singular, $plural, $amount) {
		this.singular = $singular;
		this.plural = $plural;
		this.amount = $amount;
	}
	PluralString.prototype.toString = function() {
		return this.plural;
	};

	function getTranslation($object) {
		if($jed === undefined) {
			return String($object);
		} else if($object instanceof PluralString) {
			return $jed.translate($object.singular).ifPlural($object.amount, $object.plural).fetch();
		}
		return $jed.translate(String($object)).fetch();
	}

	Imperator.Language.resolve = function($singular, $plural, $amount) {
		return new PluralString($singular, $plural, $amount);
	};

	if(Imperator.settings.gettext !== undefined) {
		$jed = new Jed(Imperator.settings.gettext);
	}

	Imperator.Language.translate = function() {
		if(arguments.length === 0) {
			throw "translate requires at least one argument";
		} else if(arguments.length === 1) {
			return getTranslation(arguments[0]);
		}
		return vsprintf(getTranslation(arguments[0]), Array.prototype.slice.call(arguments, 1));
	};

	Imperator.Language.__ = Imperator.Language.translate;
})();