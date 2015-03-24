/** ListPricesController */
angularApp.controller('ListPricesController', function($http) {
	var self = this;

	self.serviceConnection = {};

	$http.get('/rest/prices/serviceconnectioninfo').then(function(response) {
   	self.serviceConnection = response.data;
  });
});
