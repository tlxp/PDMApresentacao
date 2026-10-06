package pt.ubi.di.pdm.pdmapresetacao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Tamanhos de texto possíveis (em sp): pequeno, médio, grande
    private final float[] tamanhosTexto = {14f, 20f, 28f};

    // SharedPreferences guarda as escolhas, para não se perderem quando a Activity é recriada
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("definicoes", Context.MODE_PRIVATE);

        // Reaplica o tema guardado (o Android não o lembra quando a app é fechada)
        AppCompatDelegate.setDefaultNightMode(
                prefs.getBoolean("escuro", false)
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cada botão chama uma função simples
        findViewById(R.id.btnTema).setOnClickListener(v -> alternarTema());
        findViewById(R.id.btnTexto).setOnClickListener(v -> mudarTamanhoTexto());
        findViewById(R.id.btnLayout).setOnClickListener(v -> alternarLayout());
        findViewById(R.id.btnGrandes).setOnClickListener(v -> alternarBotoesGrandes());

        // Aplica as definições guardadas ao abrir a app
        aplicarDefinicoes();
    }

    // 1) Modo claro/escuro: troca e guarda. O Android recria a Activity sozinho com o novo tema.
    private void alternarTema() {
        boolean escuro = !prefs.getBoolean("escuro", false);
        prefs.edit().putBoolean("escuro", escuro).apply();
        AppCompatDelegate.setDefaultNightMode(
                escuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    // 2) Tamanho do texto: avança 0 -> 1 -> 2 -> 0 (pequeno, médio, grande)
    private void mudarTamanhoTexto() {
        int proximo = (prefs.getInt("tamanho", 1) + 1) % tamanhosTexto.length;
        prefs.edit().putInt("tamanho", proximo).apply();
        aplicarDefinicoes();
    }

    // 3) Layout: alterna os botões entre coluna (vertical) e linha (horizontal)
    private void alternarLayout() {
        prefs.edit().putBoolean("horizontal", !prefs.getBoolean("horizontal", false)).apply();
        aplicarDefinicoes();
    }

    // 4) Botões maiores: liga/desliga a altura aumentada dos botões
    private void alternarBotoesGrandes() {
        prefs.edit().putBoolean("grandes", !prefs.getBoolean("grandes", false)).apply();
        aplicarDefinicoes();
    }

    // Lê as definições guardadas e aplica-as ao ecrã
    private void aplicarDefinicoes() {
        float tamanho = tamanhosTexto[prefs.getInt("tamanho", 1)];
        boolean horizontal = prefs.getBoolean("horizontal", false);
        boolean grandes = prefs.getBoolean("grandes", false);

        // Tamanho do texto: aplica ao texto de exemplo e aos botões
        ((TextView) findViewById(R.id.txtExemplo)).setTextSize(TypedValue.COMPLEX_UNIT_SP, tamanho);

        // Disposição dos botões: 1 coluna (vertical) ou 2 colunas (horizontal, 2x2 para o texto caber)
        GridLayout contentor = findViewById(R.id.contentorBotoes);
        contentor.setColumnCount(horizontal ? 2 : 1);

        // Altura mínima dos botões: normal (48dp) ou grande (96dp). Se o texto precisar de mais espaço, o botão cresce.
        int alturaMinima = grandes ? dp(96) : dp(48);

        for (int i = 0; i < contentor.getChildCount(); i++) {
            Button botao = (Button) contentor.getChildAt(i);
            botao.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamanho);
            botao.setMinHeight(alturaMinima);
            // Largura 0 com peso 1: as colunas dividem a largura do ecrã por igual
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f));
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            botao.setLayoutParams(params);
        }
    }

    // Converte dp em pixels
    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }
}
