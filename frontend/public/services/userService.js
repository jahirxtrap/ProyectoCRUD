angular.module("ProyectoCRUDApp")
.service("UserService", ["$http", function($http) {
    const baseUrl = "http://localhost:5000/api/users";

    this.getUsers = () => $http.get(baseUrl);
    this.getUser = (id) => $http.get(baseUrl + "/" + id);
    this.createUser = (data) => $http.post(baseUrl, data);
    this.updateUser = (id, data) => $http.put(baseUrl + "/" + id, data);
    this.deleteUser = (id) => $http.delete(baseUrl + "/" + id);
}]);
