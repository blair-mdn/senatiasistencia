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

---

## 🔒 Autorización y Roles

### Roles de Usuario

El sistema maneja los siguientes roles:

- **estudiante**: Acceso básico al sistema, puede cambiar su contraseña y ver su perfil
- **guardia**: Acceso completo a las funcionalidades de registro de asistencia

### Guards de Seguridad

- **JwtAuthGuard**: Valida que el token JWT sea válido
- **GuardiaGuard**: Extiende JwtAuthGuard y además verifica que el usuario tenga rol "guardia"

---

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

### **Local URL**: `http://localhost:3000`

---

## 🔐 Autenticación (`/auth`)

> **ℹ️ NOTA**: El sistema maneja diferentes roles de usuario. Los usuarios con rol "guardia" tienen acceso especial a las rutas de registro de asistencia, mientras que otros roles tienen acceso limitado a sus propias funcionalidades.

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

### **PATCH** `/auth/changePassword`

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

## 🔐 Registro de Asistencia (`/registro`)

> **⚠️ IMPORTANTE**: Todas las rutas del módulo de registro requieren un **access_token de un usuario con rol "guardia"** para acceder a los recursos. Solo los usuarios con rol de guardia pueden gestionar registros de asistencia.

### **GET** `/registro/all`

Obtener todos los registros de asistencia.

**Headers:**

```
Authorization: Bearer <access_token_guardia>
```

**Respuesta exitosa (200):**

```json
[
  {
    "id": "uuid-registro-1",
    "userDni": 12345678,
    "fecha": "2025-09-04T00:00:00.000Z",
    "horaEntrada": "08:30:00",
    "horaSalida": "17:00:00",
    "verificadoPorDni": 87654321,
    "ubicacion": "Entrada Principal",
    "isActive": true
  }
]
```

**Errores posibles:**

- **401 Unauthorized**: Token inválido o expirado
- **403 Forbidden**: Solo los guardias pueden acceder a este recurso

---

### **POST** `/registro/create`

Crear un nuevo registro de asistencia (entrada).

**Headers:**

```
Content-Type: application/json
Authorization: Bearer <access_token_guardia>
```

**Body:**

```json
{
  "userDni": 12345678,
  "fecha": "2025-09-04",
  "horaEntrada": "08:30:00",
  "verificadoPorDni": 87654321,
  "ubicacion": "Entrada Principal"
}
```

**Respuesta exitosa (201):**

```json
{
  "id": "uuid-registro-generado",
  "userDni": 12345678,
  "fecha": "2025-09-04",
  "horaEntrada": "08:30:00",
  "horaSalida": null,
  "verificadoPorDni": 87654321,
  "ubicacion": "Entrada Principal",
  "isActive": true
}
```

**Errores posibles:**

- **401 Unauthorized**: Token inválido o expirado
- **403 Forbidden**: Solo los guardias pueden acceder a este recurso
- **400 Bad Request**: Datos de entrada inválidos

---

### **PATCH** `/registro/updateSalida`

Actualizar la hora de salida de un registro existente.

**Headers:**

```
Content-Type: application/json
Authorization: Bearer <access_token_guardia>
```

**Body:**

```json
{
  "userDni": 12345678,
  "horaSalida": "17:00:00"
}
```

**Respuesta exitosa (200):**

```json
{
  "message": "Hora de salida actualizada correctamente"
}
```

**Errores posibles:**

- **401 Unauthorized**: Token inválido o expirado
- **403 Forbidden**: Solo los guardias pueden acceder a este recurso
- **400 Bad Request**: Datos de entrada inválidos
- **404 Not Found**: No se encontró un registro activo para el DNI especificado

---
