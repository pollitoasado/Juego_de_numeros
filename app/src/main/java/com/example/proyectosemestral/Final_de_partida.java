package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class Final_de_partida extends AppCompatActivity {
    private JSONArray imagenesPuntos;
    private TextView textoPuntaje;
    private ImageView imagenResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_de_partida);

        Button Btn_Inicio = findViewById(R.id.id_btn_inicio);
        textoPuntaje = findViewById(R.id.id_puntaje_partida);
        TextView textoPuntajeTotal = findViewById(R.id.id_puntaje_total);
        imagenResultado = findViewById(R.id.imageView2);  // Asegúrate de tener un ImageView en tu layout

        int puntajePartida = getIntent().getIntExtra("PUNTAJE_TOTAL", 0);
        textoPuntaje.setText("Nuevo Puntaje: " + puntajePartida);

        int puntajeTotal = getIntent().getIntExtra("PUNTAJE_TOTAL_TOTAL", 0); // Cambia la clave si es necesario
        textoPuntajeTotal.setText("Puntaje anterior: " + puntajeTotal);

        // Al hacer clic en el botón de inicio, volvemos al MainActivity
        Btn_Inicio.setOnClickListener(v -> {
            Intent intent = new Intent(Final_de_partida.this, MainActivity.class);
            intent.putExtra("PUNTAJE_TOTAL", puntajePartida);
            intent.putExtra("PUNTAJE_TOTAL_TOTAL", puntajeTotal + puntajePartida);
            startActivity(intent);
            finish();
        });

        // Obtener el puntaje de la partida desde el Intent
        int puntaje = getIntent().getIntExtra("PUNTAJE_TOTAL", 0);
        textoPuntaje.setText("Puntaje: " + puntaje);

        // Cargar las imágenes y puntajes
        cargarImagenesYMostrar(puntaje);
    }

    private void cargarImagenesYMostrar(int puntaje) {
        try {
            InputStream is = getAssets().open("puntaje.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            imagenesPuntos = jsonObject.getJSONArray("imagen_puntos");

            // Buscar la imagen correspondiente al puntaje
            for (int i = 0; i < imagenesPuntos.length(); i++) {
                JSONObject imagenPunto = imagenesPuntos.getJSONObject(i);
                JSONObject rangoPuntos = imagenPunto.getJSONObject("puntos");
                int minPuntos = rangoPuntos.getInt("min");
                int maxPuntos = rangoPuntos.getInt("max");

                // Verificamos si el puntaje está dentro del rango
                if (puntaje >= minPuntos && puntaje <= maxPuntos) {
                    String imagenNombre = imagenPunto.getString("imagen");
                    // Usamos el nombre de la imagen para cargarla
                    int resId = getResources().getIdentifier(imagenNombre, "drawable", getPackageName());
                    if (resId != 0) {
                        imagenResultado.setImageResource(resId);
                    }
                    break;  // Si encontramos la imagen, no necesitamos seguir buscando
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
