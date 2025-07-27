from cornice import Service
from pyramid.response import Response
from .models import DBSession, User
from sqlalchemy.exc import IntegrityError
import logging
import transaction
import json
from passlib.hash import bcrypt

login_service = Service(name='login', path='/api/login', description='Login Service')
users_service = Service(name='users', path='/api/users', description='Users Service')
user_detail = Service(name='user_detail', path='/api/users/{id}', description='User Detail Service')
register_service = Service(name='register', path='/api/register', description='Registro de usuario')
logout_service = Service(name='logout', path='/api/logout', description='Logout Service')
profile_service = Service(name='profile', path='/api/profile', description='Profile Service')

logger = logging.getLogger(__name__)

def create_response(data, status_code):
    response = Response(json.dumps(data), content_type="application/json; charset=utf-8", status=status_code)
    response.headers.update({
        "Access-Control-Allow-Origin": "http://localhost:3000",
        "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type, Authorization",
        "Access-Control-Allow-Credentials": "true"
    })
    return response

# Permite listar usuarios (solo admin)
@users_service.get()
def get_users(request):
    userid = request.session.get('userid')
    if not userid:
        return create_response({"error": "No autenticado"}, 401)
    
    user = DBSession.query(User).filter_by(id=userid).first()
    if not user or not user.is_admin:
        return create_response({"error": "Permiso denegado"}, 403)

    users = DBSession.query(User).all()
    result = [{
        "id": u.id,
        "username": u.username,
        "email": u.email,
        "is_admin": u.is_admin,
    } for u in users]
    return create_response({"users": result}, 200)

# Crear usuario (solo admin)
@users_service.post()
def create_user(request):
    userid = request.session.get('userid')
    if not userid:
        return create_response({"error": "No autenticado"}, 401)

    user = DBSession.query(User).filter_by(id=userid).first()
    if not user or not user.is_admin:
        return create_response({"error": "Permiso denegado"}, 403)

    data = request.json_body
    username = data.get('username')
    email = data.get('email')
    password = data.get('password')
    is_admin = data.get('is_admin', False)

    if not username or not email or not password:
        return create_response({"error": "Faltan campos obligatorios"}, 400)

    existing_user = DBSession.query(User).filter((User.username == username) | (User.email == email)).first()
    if existing_user:
        return create_response({"error": "Usuario o email ya existen"}, 409)

    hashed_pw = bcrypt.hash(password)
    new_user = User(username=username, email=email, password=hashed_pw, is_admin=is_admin)
    with transaction.manager:
        DBSession.add(new_user)
        DBSession.flush()
        user_id = new_user.id

    return create_response({"message": "Usuario creado", "user_id": user_id}, 200)

@user_detail.get()
def get_user(request):
    userid = request.session.get('userid')
    if not userid:
        return create_response({"error": "No autenticado"}, 401)

    target_id = int(request.matchdict['id'])
    current_user = DBSession.query(User).filter_by(id=userid).first()
    target_user = DBSession.query(User).filter_by(id=target_id).first()
    if not target_user:
        return create_response({"error": "Usuario no encontrado"}, 404)

    # Solo admin o el mismo usuario puede ver
    if not current_user.is_admin and current_user.id != target_user.id:
        return create_response({"error": "Permiso denegado"}, 403)

    return create_response({
        "id": target_user.id,
        "username": target_user.username,
        "email": target_user.email,
        "is_admin": target_user.is_admin,
    }, 200)

@user_detail.put()
def update_user(request):
    userid = request.session.get('userid')
    if not userid:
        return create_response({"error": "No autenticado"}, 401)

    target_id = int(request.matchdict['id'])
    current_user = DBSession.query(User).filter_by(id=userid).first()
    target_user = DBSession.query(User).filter_by(id=target_id).first()
    if not target_user:
        return create_response({"error": "Usuario no encontrado"}, 404)

    # Solo admin o el mismo usuario puede editar
    if not current_user.is_admin and current_user.id != target_user.id:
        return create_response({"error": "Permiso denegado"}, 403)

    data = request.json_body
    # Admin puede editar todo, usuario normal solo puede editar email y password
    if current_user.is_admin:
        target_user.username = data.get('username', target_user.username)
        target_user.email = data.get('email', target_user.email)
        if data.get('password'):
            target_user.password = bcrypt.hash(data['password'])
        target_user.is_admin = data.get('is_admin', target_user.is_admin)
    else:
        target_user.email = data.get('email', target_user.email)
        if data.get('password'):
            target_user.password = bcrypt.hash(data['password'])

    with transaction.manager:
        DBSession.flush()
    return create_response({"message": "Usuario actualizado"}, 200)

@user_detail.delete()
def delete_user(request):
    userid = request.session.get('userid')
    if not userid:
        return create_response({"error": "No autenticado"}, 401)

    current_user = DBSession.query(User).filter_by(id=userid).first()
    if not current_user or not current_user.is_admin:
        return create_response({"error": "Permiso denegado"}, 403)

    target_id = int(request.matchdict['id'])
    target_user = DBSession.query(User).filter_by(id=target_id).first()
    if not target_user:
        return create_response({"error": "Usuario no encontrado"}, 404)

    with transaction.manager:
        DBSession.delete(target_user)
        DBSession.flush()
    return create_response({"message": "Usuario eliminado"}, 200)

@login_service.post()
def login(request):
    data = request.json_body
    username = data.get('username')
    password = data.get('password')

    user = DBSession.query(User).filter_by(username=username).first()
    if not user:
        return create_response({"error": "Usuario no encontrado"}, 404)

    if not bcrypt.verify(password, user.password):
        return create_response({"error": "Contraseña incorrecta"}, 401)

    # Guardar usuario en sesión
    request.session['userid'] = user.id
    return create_response({"message": "Login exitoso", "user_id": user.id, "is_admin": user.is_admin}, 200)

@logout_service.post()
def logout(request):
    request.session.invalidate()
    return create_response({"message": "Logout exitoso"}, 200)

@register_service.post()
def register_user(request):
    data = request.json_body
    username = data.get('username')
    email = data.get('email')
    password = data.get('password')

    if not all([username, email, password]):
        return create_response({'error': 'Faltan campos obligatorios'}, 400)

    user = User(username=username, email=email, is_admin=False)
    user.password = bcrypt.hash(password)

    try:
        with transaction.manager:
            DBSession.add(user)
            DBSession.flush()
        return create_response({'message': 'Usuario creado con éxito'}, 200)
    except IntegrityError:
        return create_response({'error': 'Usuario o email ya registrado'}, 409)
    except Exception as e:
        logger.error(f"Error en registro: {e}")
        return create_response({'error': 'Error interno'}, 500)

@profile_service.get()
def profile(request):
    user_id = request.session.get('userid')
    if not user_id:
        return create_response({'error': 'No autenticado'}, 401)

    user = DBSession.query(User).filter_by(id=user_id).first()
    if not user:
        request.session.invalidate()
        return create_response({'error': 'Usuario no encontrado'}, 404)

    return create_response({
        'user_id':   user.id,
        'username':  user.username,
        'email':     user.email,
        'is_admin':  user.is_admin,
    }, 200)
