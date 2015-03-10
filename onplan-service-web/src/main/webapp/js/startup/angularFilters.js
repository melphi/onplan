var onplanFilters = angular.module('onplanFilters', []);
onplanFilters.filter('connected', function() {
  return function(input) {
    return input ? 'Connected' : 'Disconnected';
  };
});

onplanFilters.filter('datetime', function() {
  return function(input) {
  	if (0 == input) {
  		return "";
  	}
		var date = new Date(input);
		if (null == date) {
    	console.error("Error while converting input to date:" + input);
    	return;
    }
    return $.format.date(date, "dd.MM.yyyy HH:mm:ss");
  };
});

onplanFilters.filter('nanotomilliseconds', function() {
  return function(input) {
  	var nanoseconds = parseInt(input);
  	return nanoseconds / 1000000;
  };
});

onplanFilters.filter('bytestomegabytes', function() {
  return function(input) {
  	var bytes = parseInt(input);
  	return Math.floor(bytes / 1048576);
  };
});
