angular.module('ProyectoCRUDApp', ['ngRoute'])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.withCredentials = true;
}]);
