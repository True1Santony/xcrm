# 📞 XCRM - CRM Interacciones con Clientes

XCRM es una aplicación CRM (Customer Relationship Management) desarrollada con **Spring Boot** que permite gestionar campañas, clientes, interacciones y generar métricas y reportes. Diseñada para organizaciones que requieren una solución multi-tenant, genera una base de datos por organización al registrarse automáticamente.
---

## 🖼️ Capturas de Pantalla

### 🔐 Inicio de Sesión
![Pantalla de inicio](doc/index.png)  
*Pantalla principal sin autenticación.*

---

### 📋 Gestión de Campañas
![Campañas](doc/campañas.png)  
*Vista de creación y edicion de campañas*

---

### 📊 Visualización de Ventas
![Ventas - Vista 1](doc/ventas1.png)  
*Ventas.*

![Ventas - Vista 2](doc/ventas2.png)  

---

### 🧾 Reportes Generados
![Reporte 1](doc/reportes.png)  
*Reporte PDF generado con JasperReports.*

![Reporte 2](doc/reportes1.png)  
*Visualización previa del reporte detallado por cliente.*

---

### ⚙️ Configuración y Seguridad
![Configuración](doc/config.png)  
*Pantalla de configuración para usuarios con rol de administrador.*

---

### 🧪 IDE
![Stack Tecnológico](doc/tech.png)  
*Un vistazo al proyecto.*

---
## 📁 Estructura del Proyecto
```
xcrm/
├── .idea/
├── .mvn/
├── db/
├── logs/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/
│ │ │ └── xcrm/
│ │ │ ├── configuration/
│ │ │ ├── controller/
│ │ │ │ ├── rest/
│ │ │ │ └── web/
│ │ │ ├── dto/
│ │ │ ├── model/
│ │ │ ├── repository/
│ │ │ ├── service/
│ │ │ └── utils/
│ │ ├── resources/
│ │ │ ├── db/
│ │ │ ├── reports/
│ │ │ ├── static/
│ │ │ │ ├── css/
│ │ │ │ ├── images/
│ │ │ │ ├── JavaScript/
│ │ │ │ └── uploads/
│ │ │ ├── templates/
│ │ │ ├── application.properties
│ │ │ ├── application-prod.properties
│ │ │ └── logback-spring.xml
│ └── test/
├── target/
├── .gitignore
├── esquema_v1.0.1.png
├── Documentation_XCRM_Despliegue_EC2.pdf
└── pom.xml
```

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
- 🧪 **Pruebas unitarias** - Desarrolladas con JUnit 5 (Jupiter) y Mockito para mocks y simulación de comportamientos
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
