# Proyecto CRUD

Aplicación web y móvil para la gestión de usuarios, desarrollada con **Pyramid**, **AngularJS**, **PostgreSQL** y **Kotlin**. El sistema utiliza una API REST que permite operaciones `POST`, `GET`, `PUT` y `DELETE` consumida tanto por el frontend web como por la aplicación móvil.

## Capturas de pantalla

<table>
  <thead>
    <tr>
      <th style="text-align: center;">Web</th>
      <th style="text-align: center;">Móvil</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="width: 75%; text-align: center;"><img src="images/web.png"/></td>
      <td style="width: 25%; text-align: center;"><img src="images/mobile.png"/></td>
    </tr>
  </tbody>
</table>

## Configuración inicial

### Base de datos

1. Crear una base de datos vacía en PostgreSQL.
2. Configurar la URL de conexión en el archivo `development.ini`:

```ini
sqlalchemy.url = postgresql://usuario:contraseña@hostname:puerto/nombre_base_de_datos
```

<details>
  <summary><strong>Problemas comunes (Haz clic para ver)</strong></summary>

  Si tienes problemas al conectar, crear servidores o usuarios en PostgreSQL en Linux, prueba lo siguiente:

  ```bash
  psql --version
  sudo nano /etc/postgresql/<version>/main/pg_hba.conf
  ```

  En el archivo `pg_hba.conf` busca líneas con `trust` o `peer` y cámbialas por `md5`, por ejemplo:

  ```
  host    all             all             127.0.0.1/32            md5
  ```

  Luego reinicia PostgreSQL para aplicar cambios:

  ```bash
  sudo systemctl restart postgresql
  ```

</details>

### Backend

1. Crear un entorno virtual en la raíz del proyecto:

```bash
python3 -m venv .venv
```

2. Activar el entorno e instalar las dependencias:

```bash
source .venv/bin/activate
pip install -e .
```

### Frontend

1. Instalar las dependencias en la carpeta `frontend`:

```bash
cd frontend
npm install
```

## Ejecución del proyecto

### Backend

```bash
pserve development.ini --reload
```

### Frontend

```bash
node frontend/server.js
```

### Aplicación móvil

1. Abrir el proyecto ubicado en la carpeta `mobile` utilizando **Android Studio**.
2. Conectar un dispositivo físico o iniciar un emulador.
3. Compilar y ejecutar la app.