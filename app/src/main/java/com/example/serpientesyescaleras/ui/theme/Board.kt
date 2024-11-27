package com.example.serpientesyescaleras.ui.theme

/**
 * Representa el tablero de serpientes y escaleras.
 *
 * @property size Tamaño total del tablero (número de casillas).
 * @property snakes Un mapa que asocia la cabeza de una serpiente con su cola.
 * @property ladders Un mapa que asocia la base de una escalera con su cima.
 */
data class Board(
    val size: Int = 100, // Tamaño predeterminado del tablero: 100 casillas
    val snakes: Map<Int, Int>, // Serpientes: casilla de inicio -> casilla de destino
    val ladders: Map<Int, Int> // Escaleras: casilla de inicio -> casilla de destino
){

    /**
     * Verifica si una casilla tiene una serpiente.
     *
     * @param position La posición a verificar.
     * @return `true` si hay una serpiente en esta casilla, `false` de lo contrario.
     */
    fun hasSnake(position: Int): Boolean {
        return snakes.containsKey(position)
    }

    /**
     * Verifica si una casilla tiene una escalera.
     *
     * @param position La posición a verificar.
     * @return `true` si hay una escalera en esta casilla, `false` de lo contrario.
     */
    fun hasLadder(position: Int): Boolean {
        return ladders.containsKey(position)
    }

    /**
     * Obtiene la posición final después de caer en una serpiente o escalera.
     *
     * @param position La posición actual.
     * @return La nueva posición después de aplicar las reglas del tablero.
     */
    fun getFinalPosition(position: Int): Int {
        return snakes[position] ?: ladders[position] ?: position
    }
}

// Crear un tablero con serpientes y escaleras predefinidas
val board = Board(
    size = 100,
    snakes = mapOf(
        98 to 78, // Cabeza en 98, cola en 78
        62 to 18, // Cabeza en 62, cola en 18
        36 to 6   // Cabeza en 36, cola en 6
    ),
    ladders = mapOf(
        3 to 22,  // Base en 3, cima en 22
        5 to 8,   // Base en 5, cima en 8
        20 to 29  // Base en 20, cima en 29
    )
)