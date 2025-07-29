from pyramid.config import Configurator
from pyramid.response import Response
from sqlalchemy import engine_from_config
from .models import DBSession, Base, create_admin_user
from .routes import includeme as routes_includeme
import configparser
import os

def add_cors_tween(handler, registry):
    def cors_tween(request):
        origin = request.headers.get("Origin")

        def add_cors_headers(response):
            response.headers.update({
                "Access-Control-Allow-Origin": origin if origin is not None else "*",
                "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
                "Access-Control-Allow-Headers": "Content-Type, Authorization",
                "Access-Control-Allow-Credentials": "true",
            })
            return response

        if request.method == "OPTIONS":
            return add_cors_headers(Response(status=200))

        print(f"CORS tween ejecutado para {request.method} desde {origin}")
        response = handler(request)
        return add_cors_headers(response)
    return cors_tween

def main(global_config, **settings):
    secret = os.environ.get('REDIS_SESSION_SECRET', 'fallback-secret')
    settings['redis.sessions.secret'] = secret

    config = Configurator(settings=settings)

    parser = configparser.ConfigParser()
    parser.read(global_config['__file__'])
    print(f"Backend escuchando en http://{parser['server:main']['listen']}")

    # Configuración de base de datos
    engine = engine_from_config(settings, "sqlalchemy.")
    DBSession.configure(bind=engine)
    Base.metadata.bind = engine

    # Crear tablas en la base de datos si no existen
    Base.metadata.create_all(bind=engine)
    create_admin_user()

    config.include("pyramid_redis_sessions")

    config.include("pyramid_jinja2")
    config.include("cornice")
    config.include(routes_includeme)

    config.add_tween("backend.add_cors_tween")

    config.scan()
    return config.make_wsgi_app()
