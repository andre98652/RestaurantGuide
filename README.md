# RestaurantGuide

**Proyecto Universitario - IDNP 2025B (Entregable 3 - Final)**

## Integrantes:
- Delgado Allpan, Andree David
- Gordillo Mendoza, Jose Alonzo
- Escobedo Ocaña, Jorge Luis
- Hilacondo Begazo, Andre Jimmy
- Roque Quispe, William Isaias

## Descripción General

RestaurantGuide es una aplicación Android desarrollada en Kotlin con Jetpack Compose, diseñada para conectar comensales con experiencias gastronómicas mediante una arquitectura conectada a la nube. En esta etapa final (Entregable 3), la aplicación evolucionó de un modelo local a una arquitectura Cloud-First, integrando Firebase para autenticación, base de datos en tiempo real y almacenamiento de imágenes. Además que incorpora servicios del sistema como geolocalización, cámara y notificaciones.

## Tecnologías y Librerías

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Nube (Backend as a Service):** Firebase
  - **Authentication:** Login y registro de usuarios segurizados.
  - **Cloud Firestore:** Base de datos NoSQL en tiempo real.
  - **Firebase Storage:** Almacenamiento de imágenes de restaurantes.
- **Servicios del Sistema:**
  - **Google Maps Intents:** Navegación ligera sin SDK pesado.
  - **FusedLocationProviderClient:** Obtención precisa de coordenadas GPS.
  - **CameraX / ActivityResultContracts:** Captura y selección segura de fotos.
  - **NotificationManager:** Alertas locales reactivas.
- **Inyección de Dependencias:** Hilt (o Manual DI según implementación).
- **Carga de Imágenes:** Coil.

## Estructura del Proyecto
```
com.example.restaurantguide
├── data
│   ├── model/          → Data Classes (Restaurant, Notice, Review, User)
│   ├── network/        → Servicios de Firebase (StorageService, FirestoreService)
│   ├── repository/     → Repositorios que abstraen la fuente de datos
│   └── prefs/          → DataStore para sesión local
│
├── viewmodel/          → Lógica de presentación y Corrutinas (RestaurantVM, AuthVM)
├── ui/
│   ├── components/     → Elementos reusables (Cards, Chips, Inputs)
│   ├── screens/        → Pantallas (Home, Detalle, Login, Agregar Restaurante)
│   ├── theme/          → Sistema de diseño (Tipografía, Colores)
│   └── navigation/     → AppNavigation con Safe Args
│
└── utils/              → NotificationHelper, LocationUtils, Constantes
```

## Pantallas y Funcionalidades Clave

### 1. Autenticación y Perfil
- **Login/Registro:** Validación de credenciales contra Firebase Auth.
- **Roles:** Diferenciación entre usuarios Clientes (solo lectura/reseñas) y Dueños (gestión de locales).

### 2. Home y Exploración
- **Feed en Tiempo Real:** Los restaurantes aparecen instantáneamente al ser creados.
- **Filtros Inteligentes:** Búsqueda por nombre y categorías.
- **Diseño Adaptable:** Cards con imágenes cargadas asíncronamente vía Coil.

### 3. Detalle del Restaurante
- **Información Rica:** Carrusel de imágenes, descripción, precio y horario.
- **Reseñas:** Sistema de calificación con estrellas y comentarios en tiempo real.
- **Navegación GPS:** Botón "Ver ubicación" que abre la app nativa de mapas con la ruta trazada.

### 4. Gestión (Solo Dueños)
- **Agregar Restaurante:** Formulario avanzado con validaciones.
- **Cámara y Galería:** Integración nativa para subir fotos del local.
- **Geolocalización Automática:** Detecta la ubicación exacta del negocio al crearlo.

### 5. Notificaciones y Favoritos
- **Alertas Reactivas:** Si un restaurante marcado como "Favorito" publica un aviso, el usuario recibe una notificación de sistema en la barra de estado, incluso si la app está en segundo plano.

## Instalación y Ejecución

1. Clonar el repositorio:
```bash
git clone https://github.com/andre98652/RestaurantGuide.git
```

2. Abrir en Android Studio Ladybug (o superior).
3. Sincronizar proyecto con Gradle.
4. Conectar un dispositivo físico o usar emulador.
5. Ejecutar (Shift + F10).

**Nota:** El proyecto ya incluye el archivo `google-services.json` configurado para el entorno de pruebas académico.

## Conclusiones del Grupo

El desarrollo de RestaurantGuide demostró la implementación de una arquitectura que integra Firebase y servicios nativos de Android (GPS, cámara, notificaciones).

Los principales logros fueron manejo correcto del hilo principal para operaciones asíncronas, uso de Intents implícitos en lugar del SDK completo de Maps para optimizar recursos e implementación de PickVisualMedia respetando la privacidad del usuario.

La metodología combinó IA para acelerar el desarrollo de UI repetitiva con validación  mediante documentación oficial para la lógica crítica y permisos de sistema.
