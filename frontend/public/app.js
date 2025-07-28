angular.module("ProyectoCRUDApp", ["ngRoute"])
.config(($httpProvider) => {
    $httpProvider.defaults.withCredentials = true;
});
