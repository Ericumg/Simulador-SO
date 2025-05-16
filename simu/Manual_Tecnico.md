# Manual Técnico del Simulador de Algoritmos de Reemplazo de Páginas

## 1. Estructura del Proyecto

- simu/RUEDA/RuedaMemorySimulation.java: Simulador RUEDA (Clock)
- simu/LRU/LRUMemorySimulation.java: Simulador LRU
- simu/NRU/NRUMemorySimulation.java: Simulador NRU
- simu/FIFO/MemorySimulation.java: Simulador FIFO
- simu/MENU/MainMenu.java: Menú principal

## 2. Descripción de Clases y Componentes

### RUEDA
- **Clase principal:** RuedaMemorySimulation (extiende JFrame)
- **Clase interna `Page`:** Guarda nombre, dirección y bit R.
- **Lista `physicalPages`:** Páginas en memoria física.
- **Puntero `pointer`:** Indica la posición actual en la rueda.
- **Método `addProcess(String process)`:** Añade proceso y aplica la lógica de reemplazo.
- **Clase interna `ClockPanel`:** Dibuja la rueda, procesos, puntero y bits R.
- **Ajuste visual:** Cambia el tamaño de los círculos en `paintComponent` modificando los valores de `g.fillOval(px - 20, py - 20, 35, 35);`.

### LRU
- **Clase principal:** LRUMemorySimulation (extiende JFrame)
- **Clase interna `PageLRU`:** Incluye campo `lastUsed` (contador de acceso).
- **Reemplazo:** Página con menor `lastUsed`.

### NRU
- **Clase principal:** NRUMemorySimulation (extiende JFrame)
- **Clase interna `Page`:** Incluye bits R y M.
- **Temporizador:** Limpia el bit R periódicamente.
- **Clasificación:** Agrupa páginas según R y M.

### FIFO
- **Clase principal:** MemorySimulation (extiende JFrame)
- **Cola:** Estructura FIFO para reemplazo.

### Menú Principal
- **Clase principal:** MainMenu (extiende JFrame)
- Permite seleccionar el simulador a ejecutar.

## 3. Lógica de Reemplazo (Resumen)

- **RUEDA:** El puntero recorre las páginas. Si encuentra R=0, reemplaza; si R=1, pone R=0 y avanza.
- **LRU:** Se reemplaza la página con menor contador de acceso.
- **NRU:** Se agrupan páginas en clases según R y M; se reemplaza de la clase más baja.
- **FIFO:** Se reemplaza la página más antigua.

## 4. Personalización y Expansión

- **Visualización RUEDA:** Cambia el tamaño de los círculos en `ClockPanel.paintComponent`.
- **Agregar Algoritmos:** Crea una nueva clase similar y enlázala desde el menú principal.
- **Modificar lógica:** Edita los métodos de reemplazo en cada clase.

## 5. Compilación y Ejecución

1. Abre una terminal en la carpeta `simu/`.
2. Compila todos los archivos Java:
   ```
   javac MENU/MainMenu.java RUEDA/RuedaMemorySimulation.java LRU/LRUMemorySimulation.java NRU/NRUMemorySimulation.java FIFO/MemorySimulation.java
   ```
3. Ejecuta el menú principal:
   ```
   java MENU.MainMenu
   ```

## 6. Ejemplo de Modificación (RUEDA)

Para cambiar el tamaño de los círculos en la rueda:
- Abre `RuedaMemorySimulation.java`.
- Busca el método `paintComponent` en la clase interna `ClockPanel`.
- Modifica los valores de `g.fillOval(px - 20, py - 20, 35, 35);` para ajustar el tamaño.

## 7. Notas y Buenas Prácticas
- Usa nombres claros para los procesos.
- No reduzcas el tamaño de la memoria física a menos de 1.
- Si modificas la lógica, prueba cada simulador por separado.

---

# Créditos
Desarrollado por [Tu Nombre].
Fecha: 16 de mayo de 2025.
