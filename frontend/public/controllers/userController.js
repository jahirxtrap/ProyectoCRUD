angular.module("ProyectoCRUDApp")
.controller("UserController", ["UserService", "$http", function(UserService, $http) {
    var vm = this;

    // Estado y datos
    vm.loginData = {};
    vm.registerData = {};
    vm.userForm = {};
    vm.users = [];
    vm.currentUser = null;
    vm.editingUser = null;

    // Control de vistas
    vm.showLogin = true;
    vm.showRegister = false;
    vm.showCreateForm = false;

    // Mensajes de error/success
    vm.loginError = null;
    vm.registerError = null;
    vm.registerSuccess = null;
    vm.formError = null;
    vm.formSuccess = null;

    // Filtros
    vm.searchText = "";
    vm.searchField = "username";
    vm.showOnlyAdmins = false;

    // Control de autenticación
    vm.isLoggedIn = function() {
        return vm.currentUser !== null;
    };

    // Inicializar e estado
    vm.init = function () {
        UserService.profile().then(function (response) {
            vm.currentUser = {
                id: response.data.id,
                username: response.data.username,
                email: response.data.email,
                is_admin: response.data.is_admin
            };
            vm.showLogin = false;

            if (vm.currentUser.is_admin) {
                vm.loadUsers();
            } else {
                vm.loadCurrentUserProfile();
            }
        }, function () {
            vm.resetState();
        });
    };

    vm.login = function() {
        vm.loginError = null;
        UserService.login(vm.loginData).then(function(response) {
            vm.currentUser = {
                id: response.data.id,
                username: vm.loginData.username.trim(),
                is_admin: response.data.is_admin,
            };
            vm.loginData = {};

            if (vm.currentUser.is_admin) {
                vm.loadUsers();
            } else {
                vm.loadCurrentUserProfile();
            }
            vm.showLogin = false;
        }, function(error) {
            vm.loginError = error.data.error || "Error al iniciar sesión";
        });
    };

    vm.logout = function() {
        UserService.logout().then(function() {
            vm.resetState();
        });
    };

    vm.register = function() {
        vm.registerError = null;
        vm.registerSuccess = null;

        if (!vm.registerData.password || !vm.registerData.passwordConfirm) {
            vm.registerError = "Debes ingresar la contraseña dos veces.";
            return;
        }

        if (vm.registerData.password !== vm.registerData.passwordConfirm) {
            vm.registerError = "Las contraseñas no coinciden.";
            return;
        }

        var dataToSend = {
            username: vm.registerData.username.trim(),
            email: vm.registerData.email.trim(),
            password: vm.registerData.password
        };

        UserService.register(dataToSend).then(function(response) {
                vm.registerSuccess = "Registro exitoso. Ahora puedes iniciar sesión.";
                vm.toggleLogin();
                vm.registerData = {};
            })
            .catch(function(err) {
                vm.registerError = err.data.error || "Error en el registro.";
            });
    };

    // Filtros de búsqueda
    vm.filteredUsers = function(user) {
        var field = vm.searchField;
        var searchText = (vm.searchText || "").toLowerCase();

        if (searchText) {
            var value = (user[field] || "").toString().toLowerCase();
            if (value.indexOf(searchText) === -1) {
                return false;
            }
        }

        if (vm.showOnlyAdmins && !user.is_admin) {
            return false;
        }

        return true;
    };

    // Toggle entre login y registro
    vm.toggleLogin = function() {
        vm.showLogin = true;
        vm.showRegister = false;
        vm.clearMessages();
    };

    vm.toggleRegister = function() {
        vm.showRegister = true;
        vm.showLogin = false;
        vm.clearMessages();
    };

    vm.clearMessages = function() {
        vm.loginError = "";
        vm.registerError = "";
        vm.registerSuccess = "";
        vm.formError = "";
        vm.formSuccess = "";
    };

    // CRUD de usuarios
    vm.loadUsers = function() {
        UserService.getUsers().then(function(response) {
            vm.users = response.data.users;
        }, function() {
            vm.users = [];
        });
    };

    vm.loadCurrentUserProfile = function() {
        UserService.getUser(vm.currentUser.id).then(function(response) {
            vm.currentUser.email = response.data.email;
        });
    };

    vm.createUser = function() {
        vm.formError = null;
        vm.formSuccess = null;
        vm.userForm.username = vm.userForm.username.trim();
        vm.userForm.email = vm.userForm.email.trim();

        if (!vm.userForm.username || !vm.userForm.email || !vm.userForm.password) {
            vm.formError = "Todos los campos son obligatorios";
            return;
        }

        UserService.createUser(vm.userForm).then(function(response) {
            vm.formSuccess = response.data.message;
            vm.userForm = {};
            vm.showCreateForm = false;
            vm.loadUsers();
        }, function(error) {
            vm.formError = error.data.error || "Error al crear usuario";
        });
    };

    vm.editUser = function(user) {
        vm.editingUser = angular.copy(user);
        vm.userForm = angular.copy(user);
        vm.showCreateForm = false;
    };

    vm.updateUser = function() {
        vm.formError = null;
        vm.formSuccess = null;

        var id = vm.editingUser ? vm.editingUser.id : vm.currentUser.id;
        var dataToUpdate = {
            email: vm.userForm.email,
            password: vm.userForm.password || undefined,
        };

        if (vm.currentUser.is_admin && vm.editingUser) {
            dataToUpdate.username = vm.userForm.username;
            dataToUpdate.is_admin = vm.userForm.is_admin;
        }

        UserService.updateUser(id, dataToUpdate).then(function(response) {
            vm.formSuccess = response.data.message;
            vm.editingUser = null;
            vm.userForm = {};

            if (vm.currentUser.is_admin) {
                vm.loadUsers();
            } else {
                vm.loadCurrentUserProfile();
                vm.userForm.password = "";
            }
        }, function(error) {
            vm.formError = error.data.error || "Error al actualizar usuario";
        });
    };

    vm.deleteUser = function(user) {
        if (confirm("¿Eliminar usuario " + user.username + "?")) {
            UserService.deleteUser(user.id).then(function(response) {
                vm.loadUsers();
            }, function(error) {
                alert(error.data.error || "Error al eliminar usuario");
            });
        }
    };

    vm.cancelEdit = function() {
        vm.editingUser = null;
        vm.userForm = {};
        vm.showCreateForm = false;
    };

    vm.resetState = function() {
        vm.currentUser = null;
        vm.users = [];
        vm.userForm = {};
        vm.editingUser = null;
        vm.showCreateForm = false;
        vm.showLogin = true;
        vm.showRegister = false;
        vm.clearMessages();
    };

    vm.init();
}]);
