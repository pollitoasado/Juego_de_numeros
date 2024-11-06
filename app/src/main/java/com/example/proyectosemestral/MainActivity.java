package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnPuntaje;
    private int totalPuntaje = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        totalPuntaje = getIntent().getIntExtra("PUNTAJE_TOTAL", 0);

        btnPuntaje = findViewById(R.id.id_puntaje);
        btnPuntaje.setText(String.valueOf(totalPuntaje));

        Button Btn_N1 = findViewById(R.id.boton_n1);
        Button Btn_N2 = findViewById(R.id.boton_n2);
        Button Btn_N3 = findViewById(R.id.boton_n3);

        //Nivel 1: Siempre disponible
        Btn_N1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Nivel1.class);
            intent.putExtra("PUNTAJE_TOTAL", totalPuntaje);
            startActivity(intent);
        });
        //Nivel 2: Solo disponible si el puntaje es mayor o igual a 15 puntos
        Btn_N2.setEnabled(totalPuntaje >= 15);
        Btn_N2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Nivel2.class);
            intent.putExtra("PUNTAJE_TOTAL", totalPuntaje);
            startActivity(intent);
        });
        //Nivel 3: Solo disponible si el puntaje es mayor o igual a 25 puntos
        Btn_N3.setEnabled(totalPuntaje >= 25);
        Btn_N3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Nivel3.class);
            intent.putExtra("PUNTAJE_TOTAL", totalPuntaje);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Cuando se regrese a MainActivity, actualizar el puntaje
        totalPuntaje = getIntent().getIntExtra("PUNTAJE_TOTAL", totalPuntaje);
        btnPuntaje.setText(String.valueOf(totalPuntaje));
    }
}
