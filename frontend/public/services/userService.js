angular.module("ProyectoCRUDApp")
.service("UserService", ["$http", function($http) {
    const baseUrl = "http://localhost:5000";

    this.login = (data) => $http.post(baseUrl + "/api/login", data);
    this.logout = () => $http.post(baseUrl + "/api/logout");
    this.register = (data) => $http.post(baseUrl + "/api/register", data);
    this.profile = () => $http.get(baseUrl + "/api/profile");

    this.getUsers = () => $http.get(baseUrl + "/api/users");
    this.getUser = (id) => $http.get(baseUrl + "/api/users/" + id);
    this.createUser = (data) => $http.post(baseUrl + "/api/users", data);
    this.updateUser = (id, data) => $http.put(baseUrl + "/api/users/" + id, data);
    this.deleteUser = (id) => $http.delete(baseUrl + "/api/users/" + id);
}]);
