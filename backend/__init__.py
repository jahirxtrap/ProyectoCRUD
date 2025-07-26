from pyramid.config import Configurator
from pyramid.session import SignedCookieSessionFactory
from pyramid.response import Response
from pyramid_redis_sessions import session_factory_from_settings
from sqlalchemy import engine_from_config
from .models import DBSession, Base, create_admin_user
from .routes import includeme as routes_includeme

def add_cors_tween(handler, registry):
    def cors_tween(request):
        print(f"CORS tween ejecutado para {request.method} desde {request.headers.get('Origin')}")
        if request.method == 'OPTIONS':
            origin = request.headers.get('Origin')
            allowed_origins = [
                "http://localhost:3000"
            ]
            if origin in allowed_origins:
                headers = {
                    'Access-Control-Allow-Origin': origin,
                    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
                    'Access-Control-Allow-Headers': 'Content-Type, Authorization',
                    'Access-Control-Allow-Credentials': 'true',
                }
                return Response(status=200, headers=headers)
            return Response(status=403, json_body={'error': 'Origin not allowed'})
        return handler(request)
    return cors_tween

def main(global_config, **settings):
    config = Configurator(settings=settings)
    print("Backend escuchando en http://localhost:5000")

    # Configuración de base de datos
    engine = engine_from_config(settings, 'sqlalchemy.')
    DBSession.configure(bind=engine)
    Base.metadata.bind = engine

    # Crear tablas en la base de datos si no existen
    Base.metadata.create_all(bind=engine)

    # Crear admin si no existe
    create_admin_user()

    # Sesiones con Redis
    session_factory = session_factory_from_settings(settings)
    config.set_session_factory(session_factory)

    config.include('pyramid_jinja2')
    config.include('cornice')
    config.include(routes_includeme)

    config.add_tween('backend.add_cors_tween')

    config.scan()
    return config.make_wsgi_app()
