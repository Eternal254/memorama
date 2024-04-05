package mx.edu.utch.mdapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import mx.edu.utch.mdapp.databinding.ActivityMainBinding
import java.util.Collections

class MainActivity : AppCompatActivity() {
    private var turno: Boolean = true
    private var puntosJugadorUno: Int = 0
    private var puntosJugadorDos: Int = 0
    private var PrimeraCarta: ImageView? = null
    private var primeraImagen: Int = 0
    private var clicked: Boolean = true
    private var tiempo: Long = 2000

    private var baraja = ArrayList<Int>(
        listOf(
            R.drawable.cloud,
            R.drawable.day,
            R.drawable.moon,
            R.drawable.night,
            R.drawable.rain,
            R.drawable.rainbow,
            R.drawable.cloud,
            R.drawable.day,
            R.drawable.moon,
            R.drawable.night,
            R.drawable.rain,
            R.drawable.rainbow
        )
    )

    private var imageView: ArrayList<ImageView>? = null
    private var binding: ActivityMainBinding? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        imageView = ArrayList(listOf(
            binding!!.lytCartas.im11, binding!!.lytCartas.im12,
            binding!!.lytCartas.im13, binding!!.lytCartas.im21,
            binding!!.lytCartas.im22, binding!!.lytCartas.im23,
            binding!!.lytCartas.im31, binding!!.lytCartas.im32,
            binding!!.lytCartas.im33, binding!!.lytCartas.im41, binding!!.lytCartas.im42,
            binding!!.lytCartas.im43
        ))

        binding!!.fabPrincipal.setOnClickListener {
            Toast.makeText(this, "Gone", Toast.LENGTH_LONG).show()
        }

        binding!!.fabPrincipal.setOnClickListener {
            mostrarDialogoFinJuego()
        }


        setSupportActionBar(binding!!.mainBottomAppBar)
        setContentView(binding!!.root)
        Collections.shuffle(baraja)
        StartOn()
        ClickOn()
    }

    private fun ClickOn() {
        for (i in (0 until imageView!!.size)) {
            imageView!![i].setOnClickListener {
                imageView!![i].setImageResource(baraja[i])
                guardaClick(imageView!![i], baraja[i])
            }
        }
    }

    private fun guardaClick(imageView: ImageView, i: Int) {
        if (clicked) {
            PrimeraCarta = imageView
            primeraImagen = i
            PrimeraCarta!!.isEnabled = false
            clicked = !clicked
        } else {
            xctivar(false)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if (primeraImagen == i) {
                    PrimeraCarta!!.visibility = View.INVISIBLE
                    imageView.visibility = View.INVISIBLE
                    if (turno) {
                        puntosJugadorUno += 1
                    } else {
                        puntosJugadorDos += 1
                    }
                    actualizarMarcador()
                    verificarVictoria()
                } else {
                    PrimeraCarta!!.setImageResource(R.drawable.reverso)
                    imageView.setImageResource(R.drawable.reverso)
                    turno = !turno
                    PrimeraCarta!!.isEnabled = true
                    StartOn()
                }
                xctivar(true)
            }, tiempo)
            clicked = !clicked
        }
    }

    private fun xctivar(b: Boolean) {
        for (i in (0 until imageView!!.size - 1)) {
            imageView!![i].isEnabled = b
        }
    }

    private fun StartOn() {
        if (turno) {
            binding!!.lytMarcador.mainActivityTvPlayer1.setTextColor(Color.MAGENTA)
            binding!!.lytMarcador.mainActivityTvPlayer2.setTextColor(Color.GREEN)
        } else {
            binding!!.lytMarcador.mainActivityTvPlayer1.setTextColor(Color.GREEN)
            binding!!.lytMarcador.mainActivityTvPlayer2.setTextColor(Color.MAGENTA)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_1 -> {
                newGame()
                true
            }
            R.id.option_2 -> {
                mostrarDialogoPuntuacionFinal()
                true
            }
            R.id.option_3 -> {
                salirDeLaAplicacion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun mostrarDialogoPuntuacionFinal() {
        val mensaje = "Puntuación Final:\n" +
                "Jugador 1: $puntosJugadorUno puntos\n" +
                "Jugador 2: $puntosJugadorDos puntos"

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(mensaje)
            .setCancelable(false)
            .setPositiveButton("Nuevo Juego") { _, _ ->
                newGame()
            }
            .setNegativeButton("Terminar") { _, _ ->

            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun salirDeLaAplicacion() {
        finish()
    }


    private fun mostrarDialogoSalir() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("¿Desea salir de la aplicación?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    private fun showHelp() {
        Toast.makeText(this, "Help menu clicked!", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarMarcador() {
        binding!!.lytMarcador.mainActivityTvPlayer1.text = "Player 1: $puntosJugadorUno "
        binding!!.lytMarcador.mainActivityTvPlayer2.text = "Player 2: $puntosJugadorDos "
    }

    private fun verificarVictoria() {
        if (puntosJugadorUno + puntosJugadorDos == baraja.size / 2) {
            val ganador = when {
                puntosJugadorUno > puntosJugadorDos -> "Player 1"
                puntosJugadorDos > puntosJugadorUno -> "Player 2"
                else -> "Esto es un Empate"
            }
            mostrarDialogoFinJuego(ganador)
        }
    }

    private fun mostrarDialogoFinJuego(ganador: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Fin del juego! $ganador wins!")
            .setCancelable(false)
            .setPositiveButton("Continue") { _, _ ->
                newGame()
            }
            .setNegativeButton("End Game") { _, _ ->
                finish()
            }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun newGame() {
        puntosJugadorUno = 0
        puntosJugadorDos = 0
        turno = true
        clicked = true
        tiempo = 2000
        Collections.shuffle(baraja)
        StartOn()
        actualizarMarcador()
        reiniciarCartas()
    }

    private fun mostrarDialogoFinJuego() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("¿Desea continuar en la aplicación o iniciar un nuevo juego?")
            .setCancelable(false)
            .setPositiveButton("Continuar") { _, _ ->
                // Acción al continuar en la aplicación
            }
            .setNegativeButton("Nuevo Juego") { _, _ ->
                newGame()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    private fun reiniciarCartas() {
        for (i in 0 until imageView!!.size) {
            imageView!![i].setImageResource(R.drawable.reverso)
            imageView!![i].visibility = View.VISIBLE
            imageView!![i].isEnabled = true
        }
    }


}

