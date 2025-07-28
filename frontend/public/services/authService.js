angular.module("ProyectoCRUDApp")
.service("AuthService", ["$http", function($http) {
    const baseUrl = "http://localhost:5000/api";

    this.login = (data) => $http.post(baseUrl + "/login", data);
    this.logout = () => $http.post(baseUrl + "/logout");
    this.register = (data) => $http.post(baseUrl + "/register", data);
    this.profile = () => $http.get(baseUrl + "/profile");
}]);
