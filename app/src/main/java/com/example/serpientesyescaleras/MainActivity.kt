package com.example.serpientesyescaleras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.serpientesyescaleras.ui.theme.GameBoard
import com.example.serpientesyescaleras.ui.theme.Player
import com.example.serpientesyescaleras.ui.theme.SerpientesyescalerasTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Llamamos al Composable GameBoard
                GameBoardScreen()

            }
        }
    }
}





@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SerpientesyescalerasTheme {
        Greeting("Android")
    }
}
@Composable
fun GameBoardScreen() {
    var players by remember {
        mutableStateOf(
            listOf(
                Player("player1", 1),
                Player("player2", 1)
            )
        )
    }


    GameBoard(players = players) { updatedPlayer, diceValues ->
        players = players.map {
            if (it.id == updatedPlayer.id) updatedPlayer else it
        }
        println("Dados tirados: ${diceValues.first}, ${diceValues.second}")
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        GameBoardScreen()
    }
}



