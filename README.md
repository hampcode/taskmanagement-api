# Task Management API 🧩

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)
![Maven](https://img.shields.io/badge/Maven-3.9+-orange)
![Status](https://img.shields.io/badge/Status-Stable-green)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

API REST desarrollada con **Spring Boot 3** para la gestión de tareas y developers, orientada a equipos de desarrollo de software. Este backend está diseñado para ser consumido por aplicaciones frontend web o móviles.

## ✨ Características

- CRUD completo para **Developers** y **Tasks**
- Validaciones robustas en DTOs usando `@Valid`
- Reglas de negocio aplicadas para control de asignaciones y estados
- Manejo centralizado de errores con `ProblemDetail` (Spring 3)
- Estructura limpia y separada por capas (Controller, Service, Repository, DTO, Mapper)
- Soporte para **paginación** y **búsqueda por rango de fechas**
- Preparado para integración con Frontend

## 🧱 Arquitectura del Proyecto

```bash
src/main/java/com/org/
├── controller/         # Endpoints REST
├── dto/                # Request/Response DTOs
├── exception/          # Manejo de excepciones y reglas de negocio
├── mapper/             # Conversión entre Entity y DTO
├── model/              # Entidades JPA
├── repository/         # Interfaces JPA
├── service/            # Lógica de negocio
└── TaskmanagmentApiApplication.java
```

## 🔄 Reglas de Negocio Aplicadas

- Un developer no puede tener más de **5 tareas activas** (`PENDING` o `IN_PROGRESS`).
- Solo se puede cambiar una tarea a `IN_PROGRESS` desde `PENDING`.
- Solo se puede marcar una tarea como `COMPLETED` si estuvo antes en `IN_PROGRESS`.
- No se puede registrar una tarea con **título duplicado**.
- Las tareas deben tener fechas planificadas (`startDate`, `endDate`).
- Validación de existencia de **developer** antes de asignar tareas.
- Solo se permite **reasignar una tarea en estado PENDING**.

## 📦 Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL (versión recomendada: 15+)


## ⚙️ Configuración de Base de Datos (`application.properties`)

```properties
# Nombre del proyecto
spring.application.name=taskmanagement-api

# Puerto del servidor
server.port=8080

# Ruta base para los endpoints REST (por ejemplo: /api/v1/developers)
server.servlet.context-path=/api/v1

# Configuración de conexión a PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement_db
spring.datasource.username=postgres
spring.datasource.password=adminadmin

# Dialecto y comportamiento de Hibernate (JPA)
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update        
spring.jpa.show-sql=true                    
spring.jpa.properties.hibernate.format_sql=true  # Formatea las consultas para mejor lectura

# Codificación por defecto de la conexión
spring.datasource.hikari.connection-init-sql=SET NAMES 'UTF8'
```

## 🚀 Endpoints Principales

### Developer

| Método | Endpoint             | Descripción                                      |
|--------|----------------------|--------------------------------------------------|
| POST   | `/developers`        | Registrar nuevo developer                        |
| GET    | `/developers`        | Listar todos los developers                      |
| GET    | `/developers/{id}`   | Buscar developer por ID                          |
| PUT    | `/developers/{id}`   | Actualizar nombre del developer                  |
| DELETE | `/developers/{id}`   | Eliminar developer (si no tiene tareas activas)  |

### Task

| Método | Endpoint                                              | Descripción                                              |
|--------|-------------------------------------------------------|----------------------------------------------------------|
| POST   | `/tasks`                                              | Registrar nueva tarea                                    |
| GET    | `/tasks`                                              | Listar tareas con paginación                             |
| GET    | `/tasks/{id}`                                         | Buscar tarea por ID                                      |
| PUT    | `/tasks/{id}`                                         | Actualizar tarea (solo si está en `PENDING`)             |
| DELETE | `/tasks/{id}`                                         | Eliminar tarea                                           |
| PATCH  | `/tasks/{id}/status?value=IN_PROGRESS`                | Cambiar estado de la tarea                               |
| GET    | `/tasks/date-range?start=2025-05-01&end=2025-05-10`   | Buscar tareas por rango de fechas                        |
| GET    | `/tasks/developer/{id}`                               | Listar tareas activas por developer                      |

## 🧪 Pruebas

Se incluye una colección de Postman para probar los endpoints.  
Usa la variable `{{base_url}}` para definir la URL base del entorno, por ejemplo:

```text
http://localhost:8080/api/v1
```

## 🏷️ Versionado

Este proyecto sigue la convención de versiones semánticas: `v[MAJOR].[MINOR].[PATCH]`.

- **MAJOR**: cambios incompatibles con versiones anteriores.
- **MINOR**: nuevas funcionalidades compatibles.
- **PATCH**: correcciones de errores menores o ajustes internos.

**Versión actual:** `v1.0.0`  
Incluye:

- CRUD completo de Developer y Task.
- Validaciones y reglas de negocio aplicadas.
- Manejo de errores con `ProblemDetail`.
- Listo para consumo por frontend o integración externa.

---

## 🤝 Autor

**Henry Mendoza (Hampcode)**  
🔗 [https://github.com/hampcode](https://github.com/hampcode)


