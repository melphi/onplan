// Bootstrap.
var angularApp = angular.module('onPlan', ["onplanFilters"]);
angular.element(document).ready(function() {
	angular.bootstrap(document, ['onPlan']);
});
