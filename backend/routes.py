def includeme(config):
    config.add_route('home', '/')
    config.add_route('users', '/api/users')
    config.add_route('user_detail', '/api/users/{id}')
    config.add_route('login', '/api/login')
    config.add_route('logout', '/api/logout')
    config.add_route('register', '/api/users/register')
