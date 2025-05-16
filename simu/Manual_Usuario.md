# Manual de Usuario Detallado

## 1. Menú Principal
Al iniciar el programa, verás un menú con botones para cada algoritmo:
- RUEDA (Clock)
- LRU
- NRU
- FIFO

Haz clic en el algoritmo que deseas explorar.

---

## 2. RUEDA (Clock/Segunda Oportunidad)

### Interfaz
- **Memoria Física:** Lista de procesos actualmente en memoria.
- **Memoria Virtual:** Lista de procesos que han sido reemplazados.
- **Rueda Visual:** Círculos para cada proceso, bit R, puntero rojo.
- **Controles:**
  - Campo para nombre de proceso.
  - Botón "Agregar Proceso".
  - Campo para tamaño de memoria física.
  - Botón "Limpiar".
  - Botón "Regresar".

### Pasos de Uso
1. Escribe el nombre de un proceso (ejemplo: A) y haz clic en "Agregar Proceso".
2. Si la memoria física está llena, el algoritmo buscará una página con R=0 para reemplazarla.
3. Haz doble clic en un proceso de la lista de memoria física para simular un acceso (R=1).
4. Cambia el tamaño de la memoria física editando el campo y presionando Enter.
5. Haz clic en "Limpiar" para reiniciar la simulación.
6. Observa la rueda: el puntero rojo indica la próxima página candidata a reemplazo.

### Leyenda de Colores
- **Rojo:** Página bajo el puntero.
- **Verde:** Página con R=1.
- **Azul:** Página con R=0.

---

## 3. LRU (Least Recently Used)

### Interfaz
- Lista de memoria física con contador de acceso.
- Resaltado de la próxima página a reemplazar.
- Controles similares a RUEDA.

### Pasos de Uso
1. Agrega procesos como en RUEDA.
2. Haz doble clic para simular acceso y actualizar el contador.
3. Observa cuál será la próxima página reemplazada (la menos usada).

---

## 4. NRU (Not Recently Used)

### Interfaz
- Lista de memoria física con bits R y M.
- Visualización de clases NRU.
- Temporizador para limpiar R.

### Pasos de Uso
1. Agrega procesos.
2. Haz doble clic para simular acceso (R=1).
3. Observa cómo se agrupan las páginas en clases según R y M.
4. El temporizador limpia el bit R periódicamente.

---

## 5. FIFO (First In, First Out)

### Interfaz
- Lista de memoria física mostrando el orden de llegada.

### Pasos de Uso
1. Agrega procesos.
2. Observa cómo se reemplaza siempre la página más antigua.

---

## 6. Consejos Generales
- Puedes experimentar con diferentes nombres y tamaños de memoria.
- Usa doble clic para simular accesos y ver el efecto en los bits o contadores.
- El botón "Regresar" te lleva al menú principal.

---

# Preguntas Frecuentes

**¿Qué pasa si agrego un proceso que ya está en memoria física?**
- Se simula un acceso y se actualiza el bit R (RUEDA/NRU) o el contador (LRU).

**¿Puedo cambiar el tamaño de la memoria física en cualquier momento?**
- Sí, pero si reduces el tamaño, los procesos sobrantes se mueven a memoria virtual.

**¿Cómo puedo modificar la visualización?**
- En RUEDA, puedes cambiar el tamaño de los círculos editando los valores en el método `paintComponent` de la clase interna `ClockPanel`.

---

# Créditos
Desarrollado por [Tu Nombre].
Fecha: 16 de mayo de 2025.
