/** ListAlertsController */
angularApp.controller('ListAlertsController', function($http) {
	var self = this;

	self.alerts = {};

  self.getActiveAlerts = function() {
  	$http.get('/rest/alerts/').then(function(response) {
     	self.alerts = response.data;
    });
  }

  self.loadSampleAlerts = function() {
  	$http.get('/rest/alerts/loadsamplealerts').then(getActiveAlerts);
  }

  self.getActiveAlerts();
});
