package pt.ubi.di.pdm.pdmapresetacao

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Tamanhos de texto possíveis (em sp): pequeno, médio, grande
    private val tamanhosTexto = listOf(14f, 20f, 28f)

    // SharedPreferences guarda as escolhas, para não se perderem quando a Activity é recriada
    private val prefs by lazy { getSharedPreferences("definicoes", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Reaplica o tema guardado (o Android não o lembra quando a app é fechada)
        AppCompatDelegate.setDefaultNightMode(
            if (prefs.getBoolean("escuro", false)) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cada botão chama uma função simples
        findViewById<Button>(R.id.btnTema).setOnClickListener { alternarTema() }
        findViewById<Button>(R.id.btnTexto).setOnClickListener { mudarTamanhoTexto() }
        findViewById<Button>(R.id.btnLayout).setOnClickListener { alternarLayout() }
        findViewById<Button>(R.id.btnGrandes).setOnClickListener { alternarBotoesGrandes() }

        // Aplica as definições guardadas ao abrir a app
        aplicarDefinicoes()
    }

    // 1) Modo claro/escuro: troca e guarda. O Android recria a Activity sozinho com o novo tema.
    private fun alternarTema() {
        val escuro = !prefs.getBoolean("escuro", false)
        prefs.edit().putBoolean("escuro", escuro).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (escuro) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // 2) Tamanho do texto: avança 0 -> 1 -> 2 -> 0 (pequeno, médio, grande)
    private fun mudarTamanhoTexto() {
        val proximo = (prefs.getInt("tamanho", 1) + 1) % tamanhosTexto.size
        prefs.edit().putInt("tamanho", proximo).apply()
        aplicarDefinicoes()
    }

    // 3) Layout: alterna os botões entre coluna (vertical) e linha (horizontal)
    private fun alternarLayout() {
        prefs.edit().putBoolean("horizontal", !prefs.getBoolean("horizontal", false)).apply()
        aplicarDefinicoes()
    }

    // 4) Botões maiores: liga/desliga a altura aumentada dos botões
    private fun alternarBotoesGrandes() {
        prefs.edit().putBoolean("grandes", !prefs.getBoolean("grandes", false)).apply()
        aplicarDefinicoes()
    }

    // Lê as definições guardadas e aplica-as ao ecrã
    private fun aplicarDefinicoes() {
        val tamanho = tamanhosTexto[prefs.getInt("tamanho", 1)]
        val horizontal = prefs.getBoolean("horizontal", false)
        val grandes = prefs.getBoolean("grandes", false)

        // Tamanho do texto: aplica ao texto de exemplo e aos botões
        findViewById<TextView>(R.id.txtExemplo).setTextSize(TypedValue.COMPLEX_UNIT_SP, tamanho)

        // Disposição dos botões: 1 coluna (vertical) ou 2 colunas (horizontal, 2x2 para o texto caber)
        val contentor = findViewById<GridLayout>(R.id.contentorBotoes)
        contentor.columnCount = if (horizontal) 2 else 1

        // Altura mínima dos botões: normal (48dp) ou grande (96dp). Se o texto precisar de mais espaço, o botão cresce.
        val alturaMinima = if (grandes) dp(96) else dp(48)

        for (i in 0 until contentor.childCount) {
            val botao = contentor.getChildAt(i) as Button
            botao.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamanho)
            botao.minHeight = alturaMinima
            // Largura 0 com peso 1: as colunas dividem a largura do ecrã por igual
            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            )
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            botao.layoutParams = params
        }
    }

    // Converte dp em pixels
    private fun dp(valor: Int): Int = (valor * resources.displayMetrics.density).toInt()
}
