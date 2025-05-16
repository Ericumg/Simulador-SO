# Informe General del Simulador de Algoritmos de Reemplazo de Páginas

## Descripción General
Este simulador permite experimentar y visualizar el funcionamiento de los principales algoritmos de reemplazo de páginas en memoria: RUEDA (Clock/Segunda Oportunidad), LRU (Least Recently Used), NRU (Not Recently Used) y FIFO (First In, First Out). Está desarrollado en Java con Swing, ofreciendo una interfaz gráfica interactiva y didáctica.

## Objetivo
Facilitar la comprensión de la teoría y la práctica de los algoritmos de reemplazo de páginas, permitiendo a los usuarios observar el estado interno de la memoria, los bits de control y el proceso de reemplazo en tiempo real.

## Algoritmos Simulados
- **RUEDA (Clock):** Implementa la segunda oportunidad, visualizando la rueda, el puntero y el bit R de cada página.
- **LRU:** Reemplaza la página menos recientemente usada, mostrando el contador de acceso.
- **NRU:** Clasifica páginas según los bits R y M, con temporizador para limpiar R.
- **FIFO:** Reemplaza la página más antigua en memoria.

## Público Objetivo
- Estudiantes de sistemas operativos.
- Docentes que deseen mostrar la teoría de manera visual.
- Programadores interesados en la simulación de memoria.

## Beneficios
- Visualización clara y didáctica.
- Interactividad (simulación de accesos, cambios de tamaño, etc).
- Código abierto y modificable.

---

# Manual de Usuario

## 1. Inicio y Navegación
- Ejecuta el programa principal (`MainMenu.java`).
- Selecciona el algoritmo que deseas simular (RUEDA, LRU, NRU, FIFO).

## 2. Funcionalidades Comunes
- **Agregar Proceso:** Escribe el nombre y haz clic en "Agregar Proceso".
- **Tamaño de Memoria Física:** Cambia el valor y presiona Enter para ajustar el número de marcos.
- **Limpiar:** Borra toda la memoria física y virtual.
- **Regresar:** Vuelve al menú principal.

## 3. Simulador RUEDA (Clock)
- **Visualización:** En el centro verás una rueda con círculos para cada proceso, el bit R y el puntero rojo.
- **Doble clic en la lista de memoria física:** Simula un acceso (pone R=1).
- **Reemplazo:** Cuando la memoria está llena, el puntero avanza buscando una página con R=0 para reemplazarla.
- **Colores:**
  - Rojo: Página bajo el puntero.
  - Verde: Página con R=1.
  - Azul: Página con R=0.

## 4. Simulador LRU
- **Visualización:** Muestra el número de acceso de cada página y resalta la próxima a reemplazar.
- **Doble clic:** Simula acceso y actualiza el contador.

## 5. Simulador NRU
- **Visualización:** Muestra bits R y M, clases NRU y temporizador para limpiar R.
- **Doble clic:** Simula acceso (R=1).
- **Configuración:** Puedes ajustar el temporizador de limpieza de R.

## 6. Simulador FIFO
- **Visualización:** Muestra el orden de llegada de los procesos.

## 7. Consejos
- Experimenta agregando procesos y observa cómo cada algoritmo gestiona el reemplazo.
- Cambia el tamaño de la memoria física para ver el efecto.
- Usa doble clic para simular accesos y ver el efecto en los bits o contadores.

---

# Manual Técnico

## Estructura del Proyecto
- simu/RUEDA/RuedaMemorySimulation.java: RUEDA (Clock)
- simu/LRU/LRUMemorySimulation.java: LRU
- simu/NRU/NRUMemorySimulation.java: NRU
- simu/FIFO/MemorySimulation.java: FIFO
- simu/MENU/MainMenu.java: Menú principal

## Clases y Lógica Principal

### RUEDA
- **Clase interna `Page`:** Guarda nombre, dirección y bit R.
- **Lista `physicalPages`:** Páginas en memoria física.
- **Puntero `pointer`:** Indica la posición actual en la rueda.
- **Método `addProcess(String process)`:** Añade proceso y aplica la lógica de reemplazo.
- **Clase interna `ClockPanel`:** Dibuja la rueda, procesos, puntero y bits R.
- **Ajuste visual:** Cambia el tamaño de los círculos en `paintComponent` modificando los valores de `g.fillOval(px - 20, py - 20, 35, 35);`.

### LRU
- **Clase `PageLRU`:** Incluye campo `lastUsed` (contador de acceso).
- **Reemplazo:** Página con menor `lastUsed`.

### NRU
- **Clase interna `Page`:** Incluye bits R y M.
- **Temporizador:** Limpia el bit R periódicamente.
- **Clasificación:** Agrupa páginas según R y M.

### FIFO
- **Cola:** Estructura FIFO para reemplazo.

## Personalización
- Modifica el tamaño de los círculos en RUEDA cambiando los valores en `ClockPanel.paintComponent`.
- Para agregar nuevos algoritmos, crea una clase similar y enlázala desde el menú principal.

## Compilación y Ejecución
- Compila todos los `.java` en la carpeta `simu/`.
- Ejecuta `MainMenu.java` para iniciar el simulador.

## Ejemplo de Uso (RUEDA)
1. Ejecuta el simulador y selecciona RUEDA.
2. Agrega procesos (ejemplo: A, B, C, D, E).
3. Observa cómo el puntero avanza y reemplaza páginas según el bit R.
4. Haz doble clic en un proceso para simular acceso (R=1).
5. Cambia el tamaño de la memoria física y observa el efecto.

## Recomendaciones
- Lee y modifica el código para experimentar con la lógica de los algoritmos.
- Usa la visualización para explicar la teoría en clases o presentaciones.

---

# Créditos
Desarrollado por [Tu Nombre].
Fecha: 16 de mayo de 2025.
