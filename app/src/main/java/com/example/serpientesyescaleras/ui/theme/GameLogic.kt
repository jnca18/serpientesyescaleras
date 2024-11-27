package com.example.serpientesyescaleras.ui.theme

/**
 * GameLogic: Esta clase contiene la lógica principal del juego, como el lanzamiento de los dados,
 * el manejo de los 6s consecutivos, el movimiento de los jugadores y la comprobación del ganador.
 */
// Lógica del juego
object GameLogic {
    // Función para lanzar los dados
    fun rollDice(): Pair<Int, Int> {
        val dice1 = (1..6).random()
        val dice2 = (1..6).random()
        return Pair(dice1, dice2)
    }

    // Función para mover al jugador
    fun movePlayer(player: Player, dice: Pair<Int, Int>, snakeAndLadders: Map<Int, Int>): Player {
        val newPosition = player.position + dice.first + dice.second
        val finalPosition = snakeAndLadders[newPosition] ?: newPosition
        return player.copy(position = finalPosition)
    }

    // Función para obtener el siguiente jugador
    fun getNextPlayer(currentPlayerId: String, players: List<Player>): Player {
        // Obtener el índice del jugador actual
        val currentPlayerIndex = players.indexOfFirst { it.id == currentPlayerId }

        // Calcular el índice del siguiente jugador (circular)
        val nextPlayerIndex = (currentPlayerIndex + 1) % players.size

        // Retornar el siguiente jugador
        return players[nextPlayerIndex]
    }
}