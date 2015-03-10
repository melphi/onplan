/** ListStrategiesController */
angularApp.controller('ListStrategiesController', function($http) {
	var self = this;

	self.strategies = {};

	self.getActiveStrategies = function() {
		$http.get('/rest/strategies/').then(function(response) {
			self.strategies = response.data;
		});
  }

  self.loadSampleStrategies = function() {
		$http.get('/rest/strategies/loadsamplestrategies').then(getActiveStrategies);
  }

  self.getActiveStrategies();
});
