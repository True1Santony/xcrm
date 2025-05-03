# 📞 XCRM - CRM Interacciones con Clientes

XCRM es una aplicación CRM (Customer Relationship Management) desarrollada con **Spring Boot** que permite gestionar campañas, clientes, interacciones y generar métricas y reportes. Diseñada para organizaciones que requieren una solución multi-tenant, genera una base de datos por organización al registrarse automáticamente.

## 🚀 Tecnologías utilizadas

- ⚙️ **Spring Boot** - Backend robusto y escalable
- 🛡️ **Spring Security** - Seguridad con autenticación y autorización
- 🖼️ **Thymeleaf** - Motor de plantillas para las vistas
- 🎨 **Bootstrap** - Estilos modernos y responsive
- 🐬 **MySQL** - Base de datos relacional por organización
- 📊 **JasperReports** - Generación de reportes y métricas en PDF
- 🧩 **JasperSoft Studio** - Editor visual de reportes `.jrxml`
- 🏗️ **Arquitectura por capas** - Separación clara entre capas (controller, service, repository)
- 🗃️ **Multi-tenancy** - Una base de datos por organización, creada automáticamente
- 🔐 **Encriptación de contraseñas** - Con `UUID` + hashing seguro
- 🌱 **Git Flow** - Flujo de trabajo colaborativo con ramas `feature/`, `develop`, `release/` y `main`

## 👨‍💻 Colaboradores

- **Espartaco** - [github.com/espartaco-dev](https://github.com/True1Santony)
- **Antonio** - [github.com/antonio-dev](https://github.com/GitAguila)
- **Daniel** - [github.com/daniel-dev](https://github.com/Nasty35)

## 📈 Características principales

- Registro de organizaciones con base de datos propia
- Gestión de campañas comerciales
- Asignación de clientes a campañas
- Registro de interacciones (tipo, estado, fecha, resultado)
- Generación de reportes PDF, EXCEL, HTML por campaña y cliente
- Métricas clave del rendimiento
- Acceso por roles: administrador y comercial

## 🧰 Requisitos

Antes de ejecutar la aplicación asegúrate de tener instalado:

- ☕ **Java 17** o superior
- 🐬 **MySQL** (o MariaDB compatible)
- 🧱 Crear una base de datos vacía llamada `mi_app`:
  
  ```sql
  CREATE DATABASE mi_app;

## 🛠️ Instalación y ejecución

1. Clona el repositorio  
   ```bash
   git clone https://github.com/True1Santony/xcrm.git
   cd xcrm
