Imperator.Language = {
	translate: function() {
		if(arguments.length === 0) {
			throw "translate requires at least one argument";
		} else if(arguments.length === 1) {
			return arguments[0];
		}
		return sprintf(arguments[0], Array.prototype.slice.call(arguments, 1));
	},
	resolve: function() {
		if(arguments.length === 0) {
			throw "resolve requires at least one argument";
		}
		return arguments[arguments.length - 1];
	}
};
Imperator.Language.__ = Imperator.Language.translate;