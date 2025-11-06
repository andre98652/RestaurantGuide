# RestaurantGuide 

### Proyecto Universitario - IDNP 2025B (Entregable 2)

**Integrantes:**  
- Delgado Allpan, Andree David  
- Gordillo Mendoza, Jose Alonzo  
- Escobedo Ocaña, Jorge Luis  
- Hilacondo Begazo, Andre Jimmy  
- Roque Quispe, William Isaias  

---

## Descripción General
RestaurantGuide es una aplicación desarrollada en Kotlin con Jetpack Compose, que permite explorar restaurantes por categoría, visualizar detalles, administrar favoritos, leer avisos/promociones y gestionar el perfil del usuario.

Este entregable (PROY2) corresponde a la implementación funcional de interfaces y lógica básica, cumpliendo con la navegación básica, persistencia local y almacenamiento de datos mediante Room y DataStore.
prueba

---

## Tecnologías y librerías usadas
- Kotlin / Android Studio 
- **Jetpack Compose** (UI basada en declaración)
- **Room** (almacenamiento local de restaurantes y avisos)
- **DataStore Preferences** (almacenamiento de perfil de usuario)
- **Material 3** (componentes visuales)
- **Navigation Compose** (gestión de rutas y pantallas)
- **Coil** (carga de imágenes desde red)

---

## Estructura del Proyecto
```
com.example.restaurantguide
├── data
│   ├── dao/                 → DAOs de Room (RestaurantDao, NoticeDao)
│   ├── model/               → Entidades (Restaurant, Notice, UserProfile)
│   ├── database/            → AppDatabase.kt
│   └── prefs/               → UserPreferences.kt (DataStore)
│
├── repository/              → Repositorios para Room y DataStore
├── viewmodel/               → Lógica de negocio (RestaurantVM, NoticeVM, ProfileVM)
├── ui/
│   ├── components/          → BottomBar, TopBar, Chips, Cards
│   ├── screens/             → Home, Detalle, Categoría, Avisos, Favoritos, Perfil
│   ├── theme/               → Colores y estilos (RedPrimary, OnPrimary...)
│   └── AppNav.kt / AppScaffold.kt → Navegación principal y estructura general
```

---

## Pantallas Implementadas

### Home
- Buscador de restaurantes por nombre o tipo.
- Chips de categorías (Peruana, Italiana, Japonesa, Parrillas, etc.) con **LazyRow**.
- Cards con imagen, dirección, precio y rating.
- Navegación a la pantalla de detalle de cada restaurante.

### Detalle
- Imagen principal + **carrusel de fotos (LazyRow)**.
- Información completa del restaurante (tipo, precio, descripción).
- Botones: **Favoritos** (toggle) y **Ver ubicación** (stub para PROY3 como futura mejora).
- Actualización directa del estado de favoritos.

### Favoritos
- Lista de restaurantes marcados.
- Filtrado y actualización automática.
- Persistencia mediante Room.

### Avisos
- **LazyColumn** de avisos con tipo (Promoción, Novedad, Evento).
- **Filtros** mediante chips (LazyRow) y búsqueda.
- Acción: marcar como leído o abrir detalle si tiene restaurante.
- Botón: "Marcar todo como leído".

### Perfil
- Datos guardados con **DataStore** (nombre y email persistentes).
- Usuario por defecto: *Juan Pérez - juanperez@gmail.com*.
- Botón **Editar** (modifica datos en DataStore).
- Botón **Cerrar sesión** (borra datos almacenados).

---

## Persistencia
- **Room:** almacenamiento de restaurantes y avisos de forma local.
- **DataStore:** almacenamiento persistente del perfil del usuario entre sesiones.

---

## Flujo de Navegación
```
Home => Detalle
Home => Categoría
Detalle => Favoritos
Avisos => Detalle (si es que aplica)
BottomBar => (Home / Favoritos / Avisos / Perfil)
```

---

## Instalación y Ejecución
- Para ejecutar el proyecto en **Android Studio**:

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/andre98652/RestaurantGuide.git
2. Abrir Android Studio y seleccionar "Open an Existing Project", luego buscar la carpeta del proyecto clonado.
3.  Asegurarse de que Gradle sincronice todas las dependencias.
4. Conectar un dispositivo físico o usar el emulador de Android Studio
5. Ejecutar la aplicación dando al botón Run (Shift + F10).

---

## Mejoras futuras (para Entregable 3)
- Integrar **Maps Intent / GPS** en “Ver ubicación”.
- Agregar **Foreground y Background Services** (notificaciones y sincronización).
- Integrar login real con **DataStore** o **Firebase Auth**.
- Mejorar experiencia visual (animaciones, transiciones y modo oscuro).

---

## Conclusiones
El proyecto cumple con todos los requerimientos del **PROY2**:
- Estructura modular (MVVM + Room + DataStore).
- 70%+ de pantallas implementadas.
- Comportamiento funcional y persistente.
- Navegación completa y diseño según mockups de Figma diseñados previamente para primer entregable.


