angular.module('ProyectoCRUDApp')
.service('AuthService', ['$http', function($http) {
    const baseUrl = 'http://localhost:5000/api';

    this.login = function(credentials) {
        return $http.post(baseUrl +'/login', credentials, { withCredentials: true });
    };

    this.logout = function() {
        return $http.post(baseUrl +'/logout', {}, { withCredentials: true });
    };

    this.register = function(userData) {
        return $http.post(baseUrl +'/register', userData, { withCredentials: true });
    };

    this.profile = function() {
        return $http.get(baseUrl +'/profile', { withCredentials: true });
    };
}])