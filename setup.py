from setuptools import setup

requires = [
    'pyramid==1.9',
    'cornice==1.2.1',
    'sqlalchemy==1.2.19',
    'pyramid-redis-sessions==1.0.1',
    'redis==3.5.3',
    'psycopg2-binary',
    'waitress',
    'zope.sqlalchemy',
    'pyramid_jinja2',
    'passlib',
]

setup(
    name='Proyecto_CRUD',
    version='1.0',
    description='CRUD básico con Pyramid, AngularJS y PostgreSQL',
    author='jahirtrap',
    install_requires=requires,
    include_package_data=True,
    entry_points={
        'paste.app_factory': [
            'main = backend:main',
        ],
    },
)
