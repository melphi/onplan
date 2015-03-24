var STRATEGIES_BASE_URL = '/rest/strategies/';

/**
 * ListStrategiesController
 */
angularApp.controller('ListStrategiesController', function($http) {
	var self = this;

	self.strategies = [];

	self.list = function() {
		$http.get(STRATEGIES_BASE_URL).then(function(response) {
			self.strategies = response.data;
		});
  }

  self.loadSampleStrategies = function() {
		$http.get(STRATEGIES_BASE_URL + 'loadsamplestrategies').then(self.list);
  }

	self.delete = function(strategyId) {
		$http.delete(STRATEGIES_BASE_URL + strategyId).then(self.list);
	}

  self.list();
});

/**
 * CreateStrategyController
 */
angularApp.controller('CreateStrategyController', ['$http', '$window', function($http, $window) {
	var self = this;

	self.templateIds = [];
	self.selectedTemplate = null;
	self.selectedInstrument = null;
	self.selectedParameter = null;
	self.strategyConfiguration = {};
	self.error = null;

	self.listTemplates = function() {
		$http.get(STRATEGIES_BASE_URL + 'strategytemplateid').then(function(response) {
			self.templateIds = response.data;
		});
	}

	self.loadSelectedTemplate = function() {
		if (self.strategyConfiguration.className) {
			$http.get(STRATEGIES_BASE_URL + 'strategytemplate/' + self.strategyConfiguration.className)
					.then(function(response) {
							self.selectedTemplate = response.data;
					});
		} else {
			self.selectedTemplate = null;
		}
	}

	self.addInstrument = function() {
		if (!self.selectedInstrument) {
			console.error("No instrument selected.");
			return;
		}
		if (!self.strategyConfiguration.instruments) {
			self.strategyConfiguration.instruments = [];
		}
		if (self.strategyConfiguration.instruments.indexOf(self.selectedInstrument) < 0) {
			self.strategyConfiguration.instruments.push(self.selectedInstrument);
		}
		self.selectedInstrument = null;
	}

	self.removeInstrument = function(instrumentId) {
		if (!self.strategyConfiguration.instruments) {
			return;
		}
		var index = self.strategyConfiguration.instruments.indexOf(instrumentId);
		if (index >= 0) {
			self.strategyConfiguration.instruments.splice(index, 1);
		}
	}

	self.submit = function() {
		if (!self.strategyConfiguration) {
			console.error("Attempt to post a null strategy configuration.");
			return;
		}
		$http.post(STRATEGIES_BASE_URL, self.strategyConfiguration)
				.success(function() {
					self.strategyConfiguration = {};
					$window.location.href = "/strategies/list";
				});
	}

	self.listTemplates();
}]);
