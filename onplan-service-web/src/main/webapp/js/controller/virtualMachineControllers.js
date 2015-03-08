/** ShowVirtualMachineController */
angularApp.controller('ShowVirtualMachineController', function($http) {
	var self = this;

	self.virtualMachineInfo = {};

	$http.get('/rest/virtualmachine/virtualmachineinfo').then(function(response) {
   	self.virtualMachineInfo = response.data;
  });
});
