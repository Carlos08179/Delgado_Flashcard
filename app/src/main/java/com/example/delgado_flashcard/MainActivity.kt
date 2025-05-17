package com.example.delgado_flashcard

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

data class Pregunta(
    val textoPregunta: String,
    val opciones: List<String>,
    val respuestaCorrecta: String,
    val feedbackRespuestaCorrecta: String = "La respuesta correcta es: "
)

class MainActivity : AppCompatActivity() {

    // Vistas del cuestionario
    private lateinit var tvTituloTest: TextView
    private lateinit var tvPregunta: TextView
    private lateinit var rgOpciones: RadioGroup
    private lateinit var rbOpcion1: RadioButton
    private lateinit var rbOpcion2: RadioButton
    private lateinit var rbOpcion3: RadioButton
    private lateinit var rbOpcion4: RadioButton
    private lateinit var btnSiguiente: Button
    private lateinit var tvFeedback: TextView
    private lateinit var ivFeedbackGif: ImageView
    private lateinit var cronometro: Chronometer

    // Vistas de resultados
    private lateinit var layoutResultados: LinearLayout
    private lateinit var tvResultadoTitulo: TextView
    private lateinit var tvResultadoDetalle: TextView
    private lateinit var btnReiniciar: Button

    // Datos del cuestionario
    private val listaPreguntas = listOf(
        Pregunta("¿Qué lenguaje se usa principalmente en Android Studio para desarrollo nativo?",
            listOf("Java", "Kotlin", "Dart", "Swift"), "Kotlin"),

        Pregunta("¿Cuál es el método llamado cuando una Activity se crea por primera vez?",
            listOf("onStart()", "onResume()", "onCreate()", "onPause()"), "onCreate()"),

        Pregunta("¿Qué componente de Android se usa para mostrar una lista scrollable de elementos?",
            listOf("ListView", "ScrollView", "RecyclerView", "GridView"), "RecyclerView"),

        Pregunta("¿Qué archivo define los recursos de strings en un proyecto Android?",
            listOf("styles.xml", "AndroidManifest.xml", "strings.xml", "colors.xml"), "strings.xml"),

        Pregunta("¿Qué atributo en un LinearLayout define la orientación de los elementos hijos?",
            listOf("android:gravity", "android:layout_gravity", "android:orientation", "android:weight"), "android:orientation"),

        Pregunta("¿Qué clase se usa para hacer peticiones HTTP en Android?",
            listOf("HttpURLConnection", "Socket", "WebView", "Intent"), "HttpURLConnection"),

        Pregunta("¿Qué es un Intent en Android?",
            listOf("Un tipo de dato primitivo", "Un mensaje entre componentes", "Una interfaz de usuario", "Un archivo de configuración"), "Un mensaje entre componentes"),

        Pregunta("¿Qué gradiente de ViewGroup permite posicionar elementos de forma relativa entre sí?",
            listOf("LinearLayout", "FrameLayout", "RelativeLayout", "ConstraintLayout"), "ConstraintLayout"),

        Pregunta("¿Qué anotación evita que un campo sea removido por ProGuard?",
            listOf("@Nullable", "@Keep", "@SuppressWarnings", "@Override"), "@Keep"),

        Pregunta("¿Qué arquitectura recomienda Google para aplicaciones Android?",
            listOf("MVC", "MVP", "MVVM", "Clean Architecture"), "MVVM"),

        Pregunta("¿Qué componente ejecuta operaciones en segundo plano sin interfaz de usuario?",
            listOf("Activity", "Service", "BroadcastReceiver", "ContentProvider"), "Service"),

        Pregunta("¿Qué herramienta de Android Studio analiza el rendimiento de una app?",
            listOf("Logcat", "Profiler", "Gradle", "Layout Inspector"), "Profiler"),

        Pregunta("¿Qué patrón de diseño se usa comúnmente con LiveData?",
            listOf("Singleton", "Observer", "Factory", "Decorator"), "Observer"),

        Pregunta("¿Qué extensión de archivo tiene un layout en Android?",
            listOf(".kt", ".java", ".xml", ".gradle"), ".xml"),

        Pregunta("¿Qué función Kotlin permite ejecutar código solo si un objeto no es null?",
            listOf("with()", "apply()", "let()", "run()"), "let()"),

        Pregunta("¿Qué componente permite compartir datos entre aplicaciones?",
            listOf("SharedPreferences", "ContentProvider", "Room", "ViewModel"), "ContentProvider"),

        Pregunta("¿Qué atributo en un Button define la acción al hacer click?",
            listOf("android:onClick", "android:clickable", "android:action", "android:touch"), "android:onClick"),

        Pregunta("¿Qué biblioteca de Google se usa para inyección de dependencias?",
            listOf("Retrofit", "Glide", "Hilt", "WorkManager"), "Hilt"),

        Pregunta("¿Qué método se llama cuando un usuario presiona el botón 'Atrás'?",
            listOf("onBackPressed()", "onDestroy()", "onStop()", "onPause()"), "onBackPressed()"),

        Pregunta("¿Qué clase representa un hilo de interfaz de usuario en Android?",
            listOf("Thread", "Coroutine", "Handler", "Looper"), "Handler")
    )

    private var indicePreguntaActual = 0
    private var respuestasCorrectas = 0
    private var respuestasIncorrectas = 0
    private var tiempoInicio: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas del cuestionario
        tvTituloTest = findViewById(R.id.tvTituloTest)
        tvPregunta = findViewById(R.id.tvPregunta)
        rgOpciones = findViewById(R.id.rgOpciones)
        rbOpcion1 = findViewById(R.id.rbOpcion1)
        rbOpcion2 = findViewById(R.id.rbOpcion2)
        rbOpcion3 = findViewById(R.id.rbOpcion3)
        rbOpcion4 = findViewById(R.id.rbOpcion4)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        tvFeedback = findViewById(R.id.tvFeedback)
        ivFeedbackGif = findViewById(R.id.ivFeedbackGif)
        cronometro = findViewById(R.id.cronometro)

        // Inicializar vistas de resultados
        layoutResultados = findViewById(R.id.layoutResultados)
        tvResultadoTitulo = findViewById(R.id.tvResultadoTitulo)
        tvResultadoDetalle = findViewById(R.id.tvResultadoDetalle)
        btnReiniciar = findViewById(R.id.btnReiniciar)

        // Configuración inicial
        mostrarPreguntaActual()
        iniciarCronometro()

        btnSiguiente.setOnClickListener { procesarRespuesta() }
        btnReiniciar.setOnClickListener { reiniciarTest() }
    }

    private fun iniciarCronometro() {
        tiempoInicio = SystemClock.elapsedRealtime()
        cronometro.base = tiempoInicio
        cronometro.start()
    }

    private fun mostrarPreguntaActual() {
        if (indicePreguntaActual < listaPreguntas.size) {
            val pregunta = listaPreguntas[indicePreguntaActual]
            tvPregunta.text = pregunta.textoPregunta

            // Asignar opciones
            val opciones = pregunta.opciones
            rbOpcion1.text = opciones.getOrNull(0) ?: ""
            rbOpcion2.text = opciones.getOrNull(1) ?: ""
            rbOpcion3.text = opciones.getOrNull(2) ?: ""
            rbOpcion4.text = opciones.getOrNull(3) ?: ""

            // Resetear UI
            rgOpciones.clearCheck()
            tvFeedback.visibility = View.GONE
            ivFeedbackGif.visibility = View.GONE
            btnSiguiente.text = "Siguiente Pregunta"
            habilitarOpciones(true)
        } else {
            mostrarResultados()
        }
    }

    private fun procesarRespuesta() {
        val idSeleccionado = rgOpciones.checkedRadioButtonId
        if (idSeleccionado == -1) {
            tvFeedback.text = "¡Selecciona una opción!"
            tvFeedback.setTextColor(Color.DKGRAY)
            tvFeedback.visibility = View.VISIBLE
            return
        }

        habilitarOpciones(false)
        val respuestaUsuario = findViewById<RadioButton>(idSeleccionado).text.toString()
        val preguntaActual = listaPreguntas[indicePreguntaActual]

        if (respuestaUsuario == preguntaActual.respuestaCorrecta) {
            respuestasCorrectas++
            mostrarFeedbackCorrecto()
        } else {
            respuestasIncorrectas++
            mostrarFeedbackIncorrecto(preguntaActual)
        }

        configurarBotonSiguiente()
    }

    private fun mostrarFeedbackCorrecto() {
        tvFeedback.text = "¡Correcto! ✅"
        tvFeedback.setTextColor(Color.GREEN)
        tvFeedback.visibility = View.VISIBLE
        Glide.with(this).load(R.drawable.gif_correcto).into(ivFeedbackGif)
        ivFeedbackGif.visibility = View.VISIBLE
    }

    private fun mostrarFeedbackIncorrecto(pregunta: Pregunta) {
        tvFeedback.text = "${pregunta.feedbackRespuestaCorrecta}${pregunta.respuestaCorrecta}"
        tvFeedback.setTextColor(Color.RED)
        tvFeedback.visibility = View.VISIBLE
        Glide.with(this).load(R.drawable.gif_incorrecto).into(ivFeedbackGif)
        ivFeedbackGif.visibility = View.VISIBLE
    }

    private fun configurarBotonSiguiente() {
        btnSiguiente.text = if (indicePreguntaActual < listaPreguntas.size - 1) {
            "Siguiente Pregunta"
        } else {
            "Ver Resultados"
        }

        btnSiguiente.setOnClickListener {
            if (indicePreguntaActual < listaPreguntas.size - 1) {
                indicePreguntaActual++
                mostrarPreguntaActual()
            } else {
                mostrarResultados()
            }
            // Restaurar listener original
            btnSiguiente.setOnClickListener { procesarRespuesta() }
        }
    }

    private fun mostrarResultados() {
        // Ocultar elementos del cuestionario
        listOf(tvTituloTest, tvPregunta, rgOpciones, btnSiguiente, tvFeedback, ivFeedbackGif, cronometro).forEach {
            it.visibility = View.GONE
        }

        // Mostrar contenedor de resultados
        layoutResultados.visibility = View.VISIBLE

        // Calcular resultados
        val tiempoTranscurridoMs = SystemClock.elapsedRealtime() - cronometro.base
        val tiempoFormateado = String.format("%02d:%02d:%02d",
            tiempoTranscurridoMs / 3600000,
            (tiempoTranscurridoMs % 3600000) / 60000,
            (tiempoTranscurridoMs % 60000) / 1000)

        val notaSobre20 = respuestasCorrectas * (20.0 / listaPreguntas.size)
        val notaFormateada = String.format("%.1f", notaSobre20)

        // Mostrar resultados
        tvResultadoTitulo.text = "🏆 Test Completado 🏆"
        tvResultadoDetalle.text = """
        Nota: $notaFormateada/20
        ✅ Correctas: $respuestasCorrectas
        ❌ Incorrectas: $respuestasIncorrectas
        ⏱ Tiempo: $tiempoFormateado
        """.trimIndent()
    }

    private fun reiniciarTest() {
        // Resetear datos
        indicePreguntaActual = 0
        respuestasCorrectas = 0
        respuestasIncorrectas = 0

        tvTituloTest.visibility = View.VISIBLE

        // Ocultar resultados y mostrar cuestionario
        layoutResultados.visibility = View.GONE
        listOf(tvPregunta, rgOpciones, btnSiguiente, cronometro).forEach {
            it.visibility = View.VISIBLE
        }

        // Reiniciar cronómetro y mostrar primera pregunta
        iniciarCronometro()
        mostrarPreguntaActual()
    }

    private fun habilitarOpciones(habilitado: Boolean) {
        rbOpcion1.isEnabled = habilitado
        rbOpcion2.isEnabled = habilitado
        rbOpcion3.isEnabled = habilitado
        rbOpcion4.isEnabled = habilitado
    }
}