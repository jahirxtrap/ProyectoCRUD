from pyramid.config import Configurator
from pyramid.response import Response
from sqlalchemy import engine_from_config
from .models import DBSession, Base, create_admin_user
from .routes import includeme as routes_includeme


def add_cors_tween(handler, registry):
    def cors_tween(request):
        allowed_origins = [
            "http://localhost:3000",
        ]
        origin = request.headers.get("Origin")

        def add_cors_headers(response):
            if origin in allowed_origins:
                response.headers.update({
                    "Access-Control-Allow-Origin": origin,
                    "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
                    "Access-Control-Allow-Headers": "Content-Type, Authorization",
                    "Access-Control-Allow-Credentials": "true",
                })
            return response

        if request.method == "OPTIONS":
            if origin in allowed_origins:
                return add_cors_headers(Response(status=200))
            else:
                return Response({"error": "Origen no permitido"}, status=403)

        response = handler(request)
        return add_cors_headers(response)
    return cors_tween

def main(global_config, **settings):
    config = Configurator(settings=settings)
    print("Backend escuchando en http://localhost:5000")

    # Configuración de base de datos
    engine = engine_from_config(settings, "sqlalchemy.")
    DBSession.configure(bind=engine)
    Base.metadata.bind = engine

    # Crear tablas en la base de datos si no existen
    Base.metadata.create_all(bind=engine)

    # Crear admin si no existe
    create_admin_user()

    # Sesiones con Redis
    config.include("pyramid_redis_sessions")

    config.include("pyramid_jinja2")
    config.include("cornice")
    config.include(routes_includeme)

    config.add_tween("backend.add_cors_tween")

    config.scan()
    return config.make_wsgi_app()
