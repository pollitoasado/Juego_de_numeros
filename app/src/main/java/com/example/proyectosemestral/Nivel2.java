package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class Nivel2 extends AppCompatActivity {
    private JSONArray numeros_randoms;
    private int randomNumber = 13;
    private ImageView imagenPregunta;
    private ImageButton btn_regresar;
    private EditText respuesta;
    private Random random;
    private int puntaje = 0;

    private int contadorPreguntas = 0;
    private int intentos = 0;
    private static final int MAX_PREGUNTAS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel2);
        random = new Random();

        puntaje = getIntent().getIntExtra("PUNTAJE_TOTAL", 0);

        imagenPregunta = findViewById(R.id.id_imagen_view);
        btn_regresar = findViewById(R.id.btn_regresar);
        respuesta = findViewById(R.id.id_respuesta_n2);

        btn_regresar.setOnClickListener(view -> {
            Intent intent = new Intent(Nivel2.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Cargar las preguntas
        cargarPreguntas();
        mostrarPregunta();

        // Configurar listener para verificar respuesta al presionar "Enter"
        respuesta.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                verificarRespuesta(respuesta.getText().toString(), getRespuestaCorrecta());
                respuesta.setText("");
                return true;
            }
            return false;
        });
    }

    private void cargarPreguntas() {
        try {
            InputStream is = getAssets().open("imagenes_n2.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            numeros_randoms = jsonObject.getJSONArray("imagenes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarPregunta() {
        if (contadorPreguntas >= MAX_PREGUNTAS) {
            // Mostrar el puntaje total y terminar el juego
            Intent intent = new Intent(Nivel2.this, Final_de_partida.class);
            intent.putExtra("PUNTAJE_TOTAL", puntaje); // Puntaje de la partida
            intent.putExtra("PUNTAJE_TOTAL_TOTAL", getIntent().getIntExtra("PUNTAJE_TOTAL", 0)); // Puntaje total del usuario
            startActivity(intent);
            finish(); // Cerrar la actividad o redirigir a otra pantalla
            return;
        }

        randomNumber = random.nextInt(numeros_randoms.length());
        try {
            JSONObject preguntaObject = numeros_randoms.getJSONObject(randomNumber);
            String imagenRuta = preguntaObject.getString("imagen");
            String respuestaCorrecta = preguntaObject.getString("respuesta");
            // Cargar la imagen en el ImageView
            int imagenId = getResources().getIdentifier(imagenRuta, "drawable", getPackageName());
            imagenPregunta.setImageResource(imagenId);

            intentos = 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRespuestaCorrecta() {
        try {
            return numeros_randoms.getJSONObject(randomNumber).getString("respuesta");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void verificarRespuesta(String respuestaSeleccionada, String respuestaCorrecta) {
        intentos++;
        if (respuestaSeleccionada.equals(respuestaCorrecta)) {
            puntaje += (intentos == 1) ? 2 : 1;
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
            contadorPreguntas++;
            mostrarPregunta();
        } else {
            // Respuesta incorrecta
            if (intentos >= 3){
                Toast.makeText(this, "Incorrecto. Fin de intentos.", Toast.LENGTH_SHORT).show();
                contadorPreguntas++;
                mostrarPregunta();
            }
            else {
                Toast.makeText(this, "Incorrecto, intenta de nuevo. Intentos restantes: " + (3 - intentos), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
