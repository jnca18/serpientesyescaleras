package com.example.serpientesyescaleras.ui.theme

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * FirebaseService: Esta clase se encarga de la interacción con Firebase Realtime Database
 * para gestionar los datos del juego, como la información de los jugadores y el turno.
 *
 * Proporciona métodos para:
 * - Crear la estructura inicial de los datos en Firebase.
 * - Actualizar la posición y el estado de los jugadores.
 * - Escuchar cambios en los jugadores y el turno en tiempo real.
 */
class FirebaseService {

    // Instancia de la base de datos Firebase y referencia a la ubicación del juego
    private val database = FirebaseDatabase.getInstance()
    private val gameRef = database.getReference("games/gameId")  // gameId debe ser un identificador único para cada juego

    /**
     * Método para crear la estructura de los datos iniciales en Firebase.
     * Se establece la información inicial de los jugadores y el primer turno del juego.
     */
    fun createGameData() {
        // Estructura inicial de los datos
        val initialData = mapOf(
            "players" to mapOf(
                "player1" to mapOf("position" to 5, "consecutiveSixes" to 0),
                "player2" to mapOf("position" to 10, "consecutiveSixes" to 1)
            ),
            "turn" to "player1"  // El primer jugador en el turno es player1
        )

        // Establecer los datos en la referencia del juego en Firebase
        gameRef.setValue(initialData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Estructura creada exitosamente")
            } else {
                Log.e("Firebase", "Error al crear la estructura: ${task.exception?.message}")
            }
        }
    }

    /**
     * Método para actualizar la información de un jugador específico en Firebase.
     *
     * @param player El objeto jugador que contiene la información actualizada.
     */
    fun updatePlayer(player: Player) {
        // Actualiza los datos del jugador en la base de datos
        gameRef.child("players").child(player.id).setValue(player)
    }

    /**
     * Método para escuchar cambios en los jugadores de Firebase en tiempo real.
     *
     * @param onPlayersUpdated Función de callback que se ejecuta cuando los datos de los jugadores cambian.
     *                         Recibe una lista de objetos Player con la información actualizada de los jugadores.
     */
    fun listenForUpdates(onPlayersUpdated: (List<Player>) -> Unit) {
        // Escucha los cambios en la sección de "players" de la base de datos
        gameRef.child("players").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Convierte los datos del snapshot en una lista de objetos Player
                val players = snapshot.children.mapNotNull {
                    it.getValue(Player::class.java)  // Mapea cada hijo del snapshot a un objeto Player
                }
                // Llama al callback con la lista de jugadores actualizada
                onPlayersUpdated(players)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al escuchar: ${error.message}")
            }
        })
    }

    /**
     * Método para actualizar el turno del jugador en Firebase.
     *
     * @param nextPlayerId El ID del jugador que tiene el siguiente turno.
     */
    fun updateTurn(nextPlayerId: String) {
        // Establece el siguiente jugador con el turno en la base de datos
        gameRef.child("turn").setValue(nextPlayerId)
    }

    /**
     * Método para escuchar cambios en el turno del jugador en tiempo real.
     *
     * @param onTurnUpdated Función de callback que se ejecuta cuando el turno cambia.
     *                      Recibe el ID del jugador cuyo turno es el siguiente.
     */
    fun listenForTurn(onTurnUpdated: (String) -> Unit) {
        // Escucha los cambios en la sección "turn" de la base de datos
        gameRef.child("turn").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Obtiene el ID del jugador cuyo turno ha cambiado
                snapshot.getValue(String::class.java)?.let { turnPlayerId ->
                    // Llama al callback con el ID del jugador cuyo turno ha cambiado
                    onTurnUpdated(turnPlayerId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al escuchar turno: ${error.message}")
            }
        })
    }
}