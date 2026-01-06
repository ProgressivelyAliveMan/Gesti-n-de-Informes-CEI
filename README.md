# Sistema de Gestión de Informes CEI

> **Solución Full Stack para la digitalización, trazabilidad y almacenamiento seguro de informes técnicos de laboratorio.**

---

## Descripción del Proyecto
Este sistema empresarial aborda la problemática de la gestión de documentos físicos en entornos de laboratorio (Geotecnia, Metrología, etc.). La aplicación actúa como un repositorio centralizado que permite registrar metadatos técnicos, generar identificadores oficiales únicos y almacenar evidencias digitales en una estructura de servidor segura y organizada.

El proyecto implementa lógica de negocio crítica para garantizar la consistencia normativa de los identificadores y facilitar la auditoría de documentos históricos.

## Stack Tecnológico

### Backend (Core)
* **Java 21 (LTS):** Lenguaje base para aprovechar las últimas características de rendimiento y sintaxis.
* **Spring Boot 3.5.0:** Framework principal para la arquitectura de microservicios y REST API.
* **Spring Data JPA:** Abstracción para la persistencia de datos y consultas complejas.
* **Maven:** Gestión de dependencias y ciclo de vida del proyecto.

### Base de Datos & Almacenamiento
* **MySQL 8:** Motor de base de datos relacional para la integridad de los registros.
* **Java NIO (File Storage):** Implementación nativa para la gestión eficiente de archivos y directorios en el sistema operativo.

### Frontend (Cliente)
* **JavaScript (ES6+):** Lógica del lado del cliente sin dependencias pesadas (Vanilla JS).
* **HTML5 / CSS3:** Interfaz de usuario responsiva y ligera.
* **Fetch API:** Comunicación asíncrona con el backend RESTful.

---

## Funcionalidades Técnicas Destacadas

### 1. Generación Inteligente de Identificadores (Business Logic)
El sistema encapsula la lógica para construir el ID oficial del documento (`idInformeCEI`), garantizando unicidad y cumplimiento de estándares internos.
* **Formato:** `CEI-[COD_LAB]-[NUM_SOL]-[SECUENCIAL]-[AÑO]`
* **Implementación:** Gestionada en el `InformeController` antes de la persistencia.

### 2. Motor de Almacenamiento Jerárquico (`FileStorageService`)
A diferencia de un almacenamiento plano, este servicio organiza los archivos físicos dinámicamente en el servidor creando directorios al vuelo:
* **Ruta:** `/uploads/{AÑO}/{LABORATORIO}/{TIPO_SOLICITUD}/archivo.pdf`
* **Beneficio:** Facilita copias de seguridad segmentadas y orden manual si fuera necesario.

### 3. Búsqueda Avanzada con JPA Specifications
Implementación del patrón **Specification** para permitir filtrado dinámico multicriterio (por laboratorio, año, cliente, responsable) sin necesidad de concatenar cadenas SQL manuales, previniendo inyecciones SQL y mejorando la mantenibilidad.

### 4. Arquitectura Orientada a Servicios
* Separación clara de responsabilidades: `Controller` (API), `Service` (Lógica), `Repository` (Datos) y `Entity` (Modelo).
* API RESTful documentada implícitamente por la estructura de endpoints.

---

## Configuración e Instalación

### Prerrequisitos
* JDK 21 instalado.
* MySQL Server en ejecución.

### Pasos para Desplegar

1.  **Clonar el repositorio:**
    git clone [https://github.com/TU_USUARIO/gestion-informes-cei.git](https://github.com/TU_USUARIO/gestion-informes-cei.git)
    cd gestion-informes-cei

2.  **Configurar Base de Datos:**
    Abre el archivo `src/main/resources/application.properties` y actualiza tus credenciales:
    
    spring.datasource.url=jdbc:mysql://localhost:3306/cei_informesdb
    spring.datasource.username=TU_USUARIO_MYSQL
    spring.datasource.password=TU_CONTRASEÑA
    

3.  **Definir Directorio de Archivos:**
    En el mismo archivo `application.properties`, define dónde se guardarán los PDFs. Asegúrate de que la carpeta exista o que el usuario tenga permisos para crearla:
    
    # Ejemplo para Windows
    file.upload-dir=C:/cei_uploads/
    # Ejemplo para Linux/Mac
    file.upload-dir=/home/usuario/cei_uploads/
    

4.  **Ejecutar la Aplicación:**
    
    ./mvnw spring-boot:run
    

5.  **Acceso:**
    * Frontend: `http://localhost:8080/index.html`
    * API Endpoint: `http://localhost:8080/api/informes`

---

## 👤 Autor
**Jorge Herrera**
* Desarrollador Full Stack (Java/Spring Boot)
* Email: jorgeherrera1077@gmail.com

---
*Este proyecto fue desarrollado como parte de una solución de digitalización para procesos administrativos universitarios.*
