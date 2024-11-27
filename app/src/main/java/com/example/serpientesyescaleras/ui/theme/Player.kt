    package com.example.serpientesyescaleras.ui.theme

    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.Button
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.unit.dp
    import com.example.serpientesyescaleras.ui.theme.GameLogic.movePlayer
    import com.example.serpientesyescaleras.ui.theme.GameLogic.rollDice


    data class Player(
        val id: String,               // ID único (por ejemplo, UUID o Firebase)
        var position: Int = 1,        // Casilla actual
        var consecutiveSixes: Int = 0 // Contador de 6 consecutivos
    )


    @Composable
    fun DiceDisplay(diceValues: Pair<Int, Int>) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = diceValues.first.toString(), style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = diceValues.second.toString(), style = MaterialTheme.typography.headlineMedium)
            }
        }
    }

    @Composable
    fun GameScreen(firebaseService: FirebaseService) {


        // Lista mutable de jugadores
        val players = remember { mutableListOf<Player>() }

        // Estado para el turno actual
        val currentTurn = remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            // Escucha cambios en los jugadores desde Firebase
            firebaseService.listenForUpdates { updatedPlayers ->
                players.clear() // Limpia la lista de jugadores
                players.addAll(updatedPlayers) // Agrega los jugadores actualizados
            }

            // Escucha el turno actual desde Firebase
            firebaseService.listenForTurn { turnPlayerId ->
                currentTurn.value = turnPlayerId // Actualiza el turno actual
            }
        }

        // Mapa de serpientes y escaleras
        val snakeAndLadders = remember {
            mapOf(
                // Serpientes
                16 to 6,
                47 to 26,
                49 to 11,
                56 to 53,
                62 to 19,
                64 to 60,
                87 to 24,
                93 to 73,
                95 to 75,
                98 to 78,
                // Escaleras
                1 to 38,
                4 to 14,
                9 to 31,
                21 to 42,
                28 to 84,
                36 to 44,
                51 to 67,
                71 to 91,
                80 to 100
            )
        }

        // Función para obtener el siguiente jugador
        fun getNextPlayer(currentPlayerId: String, players: List<Player>): Player {
            val currentPlayerIndex = players.indexOfFirst { it.id == currentPlayerId }
            val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
            return players[nextPlayerIndex]
        }

        // Función para cambiar el turno después de que el jugador mueva
        fun changeTurn() {
            // Obtén el siguiente jugador
            val nextPlayer = getNextPlayer(currentTurn.value, players)

            // Actualiza el turno en Firebase o en el estado global
            currentTurn.value = nextPlayer.id
        }

        // Función para verificar serpientes/escaleras
        fun handleSnakeOrLadder(position: Int): Int {
            // Si la casilla tiene una serpiente o escalera, actualiza la posición
            return snakeAndLadders[position] ?: position
        }

        // Lógica del botón de "Tirar Dados"
        Button(onClick = {
            // Lanza los dados
            val diceValues = rollDice()

            // Encuentra el jugador actual
            val currentPlayer = players.first { it.id == currentTurn.value }

            // Calcula la nueva posición sumando el resultado de los dados
            val newPosition = currentPlayer.position + (diceValues.first + diceValues.second)

            // Verifica si la nueva posición tiene una serpiente o escalera
            val finalPosition = handleSnakeOrLadder(newPosition)

            // Actualiza la posición del jugador
            val updatedPlayer = currentPlayer.copy(position = finalPosition)

            // Aquí actualizarías el estado global de los jugadores (Firebase, por ejemplo)
            // firebaseService.updatePlayer(updatedPlayer)

            // Cambia el turno
            changeTurn()

            // Verifica si el jugador ha ganado
            if (updatedPlayer.position >= 100) {
                println("${updatedPlayer.id} ha ganado!")
            }
        }) {
            Text("Tirar Dados")
        }

        // Mostrar el nombre del jugador actual
        Text("Turno de: ${currentTurn.value}")
    }


    @Composable
    fun GameBoard(players: List<Player>, onPlayerMove: (Player, Pair<Int, Int>) -> Unit) {
        val boardSize = 100
        val snakeAndLadders = remember {
            mapOf(
                // Serpientes
                16 to 6,
                47 to 26,
                49 to 11,
                56 to 53,
                62 to 19,
                64 to 60,
                87 to 24,
                93 to 73,
                95 to 75,
                98 to 78,
                // Escaleras
                1 to 38,
                4 to 14,
                9 to 31,
                21 to 42,
                28 to 84,
                36 to 44,
                51 to 67,
                71 to 91,
                80 to 100
            )
        }
        var diceValues = remember { mutableStateOf(Pair(1, 1)) }



        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tablero de Juego", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar las casillas del tablero en 10 filas y 10 columnas
            for (row in 0 until 10) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    for (col in 0 until 10) {
                        val position = row * 10 + col + 1
                        val playerOnCell = players.firstOrNull { it.position == position }

                        // Verificamos si la casilla tiene una serpiente o escalera
                        val newPosition = snakeAndLadders[position] ?: position
                        val playerOnCellUpdated = players.firstOrNull { it.position == newPosition }

                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(2.dp)
                                .background(
                                    when {
                                        playerOnCell != null -> Color.Blue // Jugador en la casilla
                                        snakeAndLadders.containsKey(position) -> Color.Red // Serpiente
                                        snakeAndLadders.containsValue(position) -> Color.Green // Escalera
                                        else -> Color.Gray // Casilla vacía
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = position.toString(),
                                color = when {
                                    playerOnCell != null -> Color.White // Jugador en la casilla
                                    snakeAndLadders.containsKey(position) -> Color.White // Serpiente
                                    snakeAndLadders.containsValue(position) -> Color.White // Escalera
                                    else -> Color.Black // Casilla vacía
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar los dados
            DiceDisplay(diceValues.value) // Correcto: Accedes al valor del estado mutable



            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    diceValues.value = rollDice() // Actualizar estado de los dados
                    val currentPlayer = players.firstOrNull { it.id == "player1" }
                        ?: return@Button // Salimos si no hay jugador

                    //  Calculamos la nueva posición basada en los dados
                    val diceSum = diceValues.value.first + diceValues.value.second
                    val newPosition = currentPlayer.position + diceSum
                    val playerUpdated = currentPlayer.copy(position = newPosition)

                    // Verificar serpientes/escaleras
                    val finalPosition = snakeAndLadders[newPosition] ?: newPosition
                    val updatedPlayer = currentPlayer.copy(position = finalPosition)

                    // Movemos al jugador
                    onPlayerMove(updatedPlayer, diceValues.value)

                    // Verificamos si el jugador ha ganado
                    if (updatedPlayer.position == 100) {
                        println("${updatedPlayer.id} ha ganado!")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Tirar Dados")
            }


        @Composable
        fun DiceDisplay(diceValues: Pair<Int, Int>) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = diceValues.first.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = diceValues.second.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }



    fun rollDice(): Pair<Int, Int> {
        val dice1 = (1..6).random()
        val dice2 = (1..6).random()
        return Pair(dice1, dice2)
    }



    fun movePlayer(player: Player, diceValues: Pair<Int, Int>): Int {
        val diceRoll = diceValues.first + diceValues.second
        val newPosition = player.position + diceRoll
        return if (newPosition > 100) 100 else newPosition
    }


    fun getNextPlayer(currentPlayerId: String, players: List<Player>): Player {
        // Obtener el siguiente jugador basado en el ID del jugador actual
        val currentPlayerIndex = players.indexOfFirst { it.id == currentPlayerId }
        val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
        return players[nextPlayerIndex]
    }

    }
    }


