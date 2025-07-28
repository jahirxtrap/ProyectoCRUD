from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Boolean
from sqlalchemy.orm import scoped_session, sessionmaker
from passlib.hash import bcrypt
import zope.sqlalchemy
import transaction

DBSession = scoped_session(sessionmaker())
zope.sqlalchemy.register(DBSession)

Base = declarative_base()

class User(Base):
    __tablename__ = 'users'
    id = Column(Integer, primary_key=True)
    username = Column(String, unique=True, nullable=False)
    password = Column(String, nullable=False)
    email = Column(String, unique=True, nullable=False)
    is_admin = Column(Boolean, default=False)

def create_admin_user():
    admin_username = 'admin'
    admin_email = 'admin@gmail.com'
    admin_password = '1234'

    admin = DBSession.query(User).filter(User.username == admin_username).first()
    if admin: return

    hashed_password = bcrypt.hash(admin_password)

    admin = User(
        username=admin_username,
        email=admin_email,
        password=hashed_password,
        is_admin=True
    )

    DBSession.add(admin)
    transaction.commit()
    print("Usuario admin creado.")
