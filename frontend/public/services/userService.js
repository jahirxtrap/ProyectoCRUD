angular.module("ProyectoCRUDApp")
.service("UserService", ["$http", function($http) {
    const baseUrl = "http://localhost:5000/api";

    this.login = (data) => $http.post(baseUrl + "/login", data);
    this.logout = () => $http.post(baseUrl + "/logout");
    this.register = (data) => $http.post(baseUrl + "/register", data);
    this.profile = () => $http.get(baseUrl + "/profile");

    this.getUsers = () => $http.get(baseUrl + "/users");
    this.getUser = (id) => $http.get(baseUrl + "/users/" + id);
    this.createUser = (data) => $http.post(baseUrl + "/users", data);
    this.updateUser = (id, data) => $http.put(baseUrl + "/users/" + id, data);
    this.deleteUser = (id) => $http.delete(baseUrl + "/users/" + id);
}]);
