/** ListStrategiesController */
angularApp.controller('ListStrategiesController', function($http) {
	var self = this;

	self.strategies = {};

	$http.get('/rest/strategies/').then(function(response) {
   	self.strategies = response.data;
  });
});
