angular.module('ProyectoCRUDApp')
.service('UserService', ['$http', function($http) {
    var baseUrl = 'http://localhost:5000/api/users';

    // Todas las llamadas envían cookies para mantener la sesión
    this.getUsers = function() {
        return $http.get(baseUrl, { withCredentials: true });
    };

    this.getUser = function(id) {
        return $http.get(baseUrl + '/' + id, { withCredentials: true });
    };

    this.createUser = function(userData) {
        return $http.post(baseUrl, userData, { withCredentials: true });
    };

    this.updateUser = function(id, userData) {
        return $http.put(baseUrl + '/' + id, userData, { withCredentials: true });
    };

    this.deleteUser = function(id) {
        return $http.delete(baseUrl + '/' + id, { withCredentials: true });
    };
}]);
