from pyramid.response import Response

def includeme(config):
    config.add_route('home', '/')
    config.add_route('users', '/api/users')
    config.add_route('user_detail', '/api/users/{id}')
    config.add_route('login', '/api/login')
    config.add_route('logout', '/api/logout')
    config.add_route('options', '/options')

    config.add_view(lambda request: Response(status=200, headers={
        'Access-Control-Allow-Origin': request.headers.get('Origin', '*'),
        'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization',
        'Access-Control-Allow-Credentials': 'true',
    }), route_name='options', request_method='OPTIONS')
