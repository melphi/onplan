/** ListPricesController */
angularApp.controller('ListPricesController', function($http) {
	var self = this;

	self.serviceConnectionInfo = {};

	$http.get('/rest/prices/serviceconnectioninfo').then(function(response) {
   	self.serviceConnectionInfo = response.data;
  });
});
