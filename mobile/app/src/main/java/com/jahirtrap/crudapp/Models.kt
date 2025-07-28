package com.jahirtrap.crudapp

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val id: Int,
    val is_admin: Boolean
)

data class ApiResponse(
    val message: String,
    val error: String? = null
)

data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
    val is_admin: Boolean
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val is_admin: Boolean = false
)

data class UpdateUserRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val is_admin: Boolean? = null
)

data class UsersResponse(
    val users: List<UserProfile>
)
