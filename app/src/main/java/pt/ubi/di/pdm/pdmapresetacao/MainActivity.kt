package pt.ubi.di.pdm.pdmapresetacao

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pt.ubi.di.pdm.pdmapresetacao.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityMainBinding

    // Tamanhos de texto: pequeno, médio e grande
    private val tamanhosTexto = listOf(14f, 20f, 28f)

    // Definições guardadas
    private val prefs by lazy {
        getSharedPreferences("definicoes", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Recuperar o tema guardado
        AppCompatDelegate.setDefaultNightMode(
            if (prefs.getBoolean("escuro", false)) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Inicializar View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Margens das barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                dp(16),
                systemBars.top + dp(16),
                dp(16),
                systemBars.bottom + dp(16)
            )

            insets
        }

        // Botão tema
        binding.btnTema.setOnClickListener {
            alternarTema()
        }

        // Botão tamanho do texto
        binding.btnTexto.setOnClickListener {
            mudarTamanhoTexto()
        }

        // Botão layout
        binding.btnLayout.setOnClickListener {
            alternarLayout()
        }

        // Botão botões maiores
        binding.btnGrandes.setOnClickListener {
            alternarBotoesGrandes()
        }

        // Aplicar as definições guardadas
        aplicarDefinicoes()
    }


    // =========================================================
    // TEMA CLARO / ESCURO
    // =========================================================

    private fun alternarTema() {

        val escuro =
            !prefs.getBoolean("escuro", false)

        prefs.edit()
            .putBoolean("escuro", escuro)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (escuro) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


    // =========================================================
    // TAMANHO DO TEXTO
    // =========================================================

    private fun mudarTamanhoTexto() {

        val atual =
            prefs.getInt("tamanho", 1)

        val proximo =
            (atual + 1) % tamanhosTexto.size

        prefs.edit()
            .putInt("tamanho", proximo)
            .apply()

        aplicarDefinicoes()
    }


    // =========================================================
    // LAYOUT VERTICAL / HORIZONTAL
    // =========================================================

    private fun alternarLayout() {

        val horizontal =
            !prefs.getBoolean("horizontal", false)

        prefs.edit()
            .putBoolean("horizontal", horizontal)
            .apply()

        aplicarDefinicoes()
    }


    // =========================================================
    // BOTÕES MAIORES
    // =========================================================

    private fun alternarBotoesGrandes() {

        val grandes =
            !prefs.getBoolean("grandes", false)

        prefs.edit()
            .putBoolean("grandes", grandes)
            .apply()

        aplicarDefinicoes()
    }


    // =========================================================
    // APLICAR DEFINIÇÕES
    // =========================================================

    private fun aplicarDefinicoes() {

        val tamanhoIndex =
            prefs.getInt("tamanho", 1)

        val tamanho =
            tamanhosTexto[tamanhoIndex]

        val horizontal =
            prefs.getBoolean("horizontal", false)

        val grandes =
            prefs.getBoolean("grandes", false)


        // -----------------------------------------------------
        // Texto de exemplo
        // -----------------------------------------------------

        binding.txtExemplo.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            tamanho
        )


        // -----------------------------------------------------
        // Altura dos botões
        // -----------------------------------------------------

        val alturaBotoes = when {

            grandes ->
                dp(96)

            horizontal && tamanhoIndex == 2 ->
                dp(78)

            else ->
                dp(52)
        }


        // -----------------------------------------------------
        // Os quatro botões
        // -----------------------------------------------------

        val botoes = listOf(
            binding.btnTema,
            binding.btnTexto,
            binding.btnLayout,
            binding.btnGrandes
        )


        // Aplicar texto e tamanho
        for (botao in botoes) {

            botao.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                tamanho
            )

            botao.minHeight = alturaBotoes
            botao.minimumHeight = alturaBotoes

            botao.setPadding(
                dp(8),
                dp(4),
                dp(8),
                dp(4)
            )
        }


        // Organizar os botões
        organizarBotoes(
            horizontal,
            alturaBotoes
        )


    }


    // =========================================================
    // ORGANIZAR BOTÕES
    // =========================================================

    private fun organizarBotoes(
        horizontal: Boolean,
        alturaBotoes: Int
    ) {

        val contentor =
            binding.contentorBotoes

        val set =
            ConstraintSet()

        set.clone(contentor)


        val ids = intArrayOf(
            binding.btnTema.id,
            binding.btnTexto.id,
            binding.btnLayout.id,
            binding.btnGrandes.id
        )


        // Limpar todas as constraints antigas
        for (id in ids) {
            set.clear(id)
        }


        // Definir altura dos quatro botões
        for (id in ids) {

            set.constrainHeight(
                id,
                alturaBotoes
            )

            set.constrainWidth(
                id,
                ConstraintSet.MATCH_CONSTRAINT
            )
        }


        if (horizontal) {

            // =================================================
            // HORIZONTAL
            //
            // [ Tema ]        [ Texto ]
            //
            // [ Layout ]      [ Grandes ]
            // =================================================


            // -------------------------
            // BOTÃO TEMA
            // -------------------------

            set.connect(
                binding.btnTema.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )

            set.connect(
                binding.btnTema.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )

            set.connect(
                binding.btnTema.id,
                ConstraintSet.END,
                binding.btnTexto.id,
                ConstraintSet.START,
                dp(4)
            )


            // -------------------------
            // BOTÃO TEXTO
            // -------------------------

            set.connect(
                binding.btnTexto.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )

            set.connect(
                binding.btnTexto.id,
                ConstraintSet.START,
                binding.btnTema.id,
                ConstraintSet.END,
                dp(4)
            )

            set.connect(
                binding.btnTexto.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )


            // -------------------------
            // BOTÃO LAYOUT
            // -------------------------

            set.connect(
                binding.btnLayout.id,
                ConstraintSet.TOP,
                binding.btnTema.id,
                ConstraintSet.BOTTOM,
                dp(8)
            )

            set.connect(
                binding.btnLayout.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )

            set.connect(
                binding.btnLayout.id,
                ConstraintSet.END,
                binding.btnGrandes.id,
                ConstraintSet.START,
                dp(4)
            )


            // -------------------------
            // BOTÃO GRANDES
            // -------------------------

            set.connect(
                binding.btnGrandes.id,
                ConstraintSet.TOP,
                binding.btnTexto.id,
                ConstraintSet.BOTTOM,
                dp(8)
            )

            set.connect(
                binding.btnGrandes.id,
                ConstraintSet.START,
                binding.btnLayout.id,
                ConstraintSet.END,
                dp(4)
            )

            set.connect(
                binding.btnGrandes.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )


        } else {

            // =================================================
            // VERTICAL
            //
            // [ Tema ]
            // [ Texto ]
            // [ Layout ]
            // [ Grandes ]
            // =================================================


            // -------------------------
            // BOTÃO TEMA
            // -------------------------

            set.connect(
                binding.btnTema.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )

            set.connect(
                binding.btnTema.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )

            set.connect(
                binding.btnTema.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )


            // -------------------------
            // BOTÃO TEXTO
            // -------------------------

            set.connect(
                binding.btnTexto.id,
                ConstraintSet.TOP,
                binding.btnTema.id,
                ConstraintSet.BOTTOM,
                dp(8)
            )

            set.connect(
                binding.btnTexto.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )

            set.connect(
                binding.btnTexto.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )


            // -------------------------
            // BOTÃO LAYOUT
            // -------------------------

            set.connect(
                binding.btnLayout.id,
                ConstraintSet.TOP,
                binding.btnTexto.id,
                ConstraintSet.BOTTOM,
                dp(8)
            )

            set.connect(
                binding.btnLayout.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )

            set.connect(
                binding.btnLayout.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )


            // -------------------------
            // BOTÃO GRANDES
            // -------------------------

            set.connect(
                binding.btnGrandes.id,
                ConstraintSet.TOP,
                binding.btnLayout.id,
                ConstraintSet.BOTTOM,
                dp(8)
            )

            set.connect(
                binding.btnGrandes.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )

            set.connect(
                binding.btnGrandes.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
        }


        // Aplicar as novas constraints
        set.applyTo(contentor)
    }


    // =========================================================
    // DP
    // =========================================================

    private fun dp(valor: Int): Int {

        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }
}