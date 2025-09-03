# Sistema de Asistencia SENATI-TACNA 📚

Este es un proyecto académico desarrollado para SENATI-Tacna que implementa un sistema de control de asistencia usando NestJS como backend y PostgreSQL como base de datos.

## Tecnologías Utilizadas

- **Backend**: NestJS (Node.js + TypeScript)

  - **Base de Datos**: PostgreSQL
  - **Autenticación**: JWT (JSON Web Tokens)
  - **ORM**: TypeORM
  - **Contenedores**: Docker & Docker Compose
  - **Validación**: Class Validator

- **App Movil**: Android (Kotlin)

## Configuración del Proyecto

### Requisitos Previos

- Node.js 18+
- Docker & Docker Compose
- Git

### Variables de Entorno

Crear un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
# Base de Datos PostgreSQL
POSTGRES_HOST=localhost
POSTGRES_PORT=5433
POSTGRES_DB=SENATI_REGISTRO_INGRESO
POSTGRES_USER=SENATI_USER
POSTGRES_PASSWORD=SENATI_PASSWORD

# Configuración del Servidor
JWT_SECRET_KEY=superSecretKeyXD
PORT=3000
```

### Instalación y Ejecución

1. **Clonar el repositorio**:

   ```bash
   git clone <url-del-repositorio>
   cd senatiasistencia
   ```

2. **Levantar la base de datos con Docker**:

   ```bash
   docker-compose up -d
   ```

3. **Instalar dependencias del servidor**:

   ```bash
   cd server
   npm install
   ```

4. **Ejecutar el servidor en modo desarrollo**:
   ```bash
   npm run start:dev
   ```

El servidor estará disponible en: `http://localhost:3000`

## 🛠️ API Endpoints

### **Base URL**: `http://localhost:3000`

---

## 🔐 Autenticación (`/auth`)

### **POST** `/auth/login`

Iniciar sesión en el sistema.

**Headers:**

```
Content-Type: application/json
```

**Body:**

```json
{
  "email": "usuario@senati.pe",
  "password": "contraseña123"
}
```

**Respuesta exitosa (200):**

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "dni": 12345678,
    "email": "usuario@senati.pe",
    "name": "Juan",
    "lastname": "Pérez",
    "rol": "estudiante"
  }
}
```

**Errores posibles:**

- **401 Unauthorized**: Credenciales inválidas
- **401 Unauthorized**: Usuario inactivo
- **400 Bad Request**: Datos de entrada inválidos

---

### **PATCH** `/auth/change-password`

Cambiar la contraseña del usuario autenticado.

**Headers:**

```
Content-Type: application/json
Authorization: Bearer <access_token>
```

**Body:**

```json
{
  "currentPassword": "contraseñaActual123",
  "newPassword": "nuevaContraseña456"
}
```

**Respuesta exitosa (200):**

```json
{
  "message": "Contraseña actualizada correctamente"
}
```

**Errores posibles:**

- **401 Unauthorized**: Token inválido o expirado
- **401 Unauthorized**: La contraseña actual es incorrecta
- **400 Bad Request**: Usuario no encontrado
- **400 Bad Request**: La nueva contraseña debe ser diferente a la actual
- **400 Bad Request**: Datos de entrada inválidos (contraseña debe tener al menos 6 caracteres)

---

### **GET** `/auth/profile`

Obtener información del perfil del usuario autenticado.

**Headers:**

```
Authorization: Bearer <access_token>
```

**Respuesta exitosa (200):**

```json
{
  "dni": 12345678,
  "email": "usuario@senati.pe",
  "rol": "estudiante"
}
```

**Errores posibles:**

- **401 Unauthorized**: Token inválido o expirado

---

## 📋 Validaciones de Datos

### LoginDto

- **email**: Debe ser un email válido
- **password**: Mínimo 6 caracteres

### ChangePasswordDto

- **currentPassword**: Mínimo 6 caracteres
- **newPassword**: Mínimo 6 caracteres

---
