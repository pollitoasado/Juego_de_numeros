package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Random;

public class Nivel3 extends AppCompatActivity {
    private JSONArray numeros_randoms;
    private int randomNumber = 15;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nivel3);
        random = new Random();

        puntaje = getIntent().getIntExtra("PUNTAJE_TOTAL", 0);

        imagenPregunta = findViewById(R.id.id_imagen_view);
        btn_regresar = findViewById(R.id.btn_regresar);
        respuesta = findViewById(R.id.id_respuesta_n3);

        btn_regresar.setOnClickListener(view -> {
            Intent intent = new Intent(Nivel3.this, MainActivity.class);
            intent.putExtra("PUNTAJE_TOTAL", puntaje);
            startActivity(intent);
            finish();
        });

        // Cargar las preguntas
        cargarPregunta();
        mostrarPregunta();

        // Configurar listener para verificar respuesta al presionar "Enter"
        respuesta.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                verificarRespuesta(respuesta.getText().toString(), respuestaCorrecta());
                respuesta.setText("");
                return true;
            }
            return false;
        });
    }
    private void cargarPregunta() {
        try {
            InputStream is = getAssets().open("imagenes_n3.json");
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

    private void mostrarPregunta(){
        if(contadorPreguntas >= MAX_PREGUNTAS) {
            // Mostrar el puntaje total y terminar el juego
            Intent intent = new Intent(Nivel3.this, Final_de_partida.class);
            intent.putExtra("PUNTAJE_TOTAL", puntaje); // Puntaje de la partida
            intent.putExtra("PUNTAJE_TOTAL_TOTAL", getIntent().getIntExtra("PUNTAJE_TOTAL", 0)); // Puntaje total del usuario
            startActivity(intent);
            finish();
            return;
        }

        randomNumber = random.nextInt(numeros_randoms.length());
        try {
            JSONObject preguntaObject = numeros_randoms.getJSONObject(randomNumber);
            String imagenRuta = preguntaObject.getString("imagen");
            String respuestaCorrecta = preguntaObject.getString("respuesta");

            int imagenId = getResources().getIdentifier(imagenRuta, "drawable", getPackageName());
            if (imagenId != 0) { // Verificar que la imagen se encontró
                imagenPregunta.setImageResource(imagenId);
            }

            intentos = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String respuestaCorrecta () {
        try {
            return numeros_randoms.getJSONObject(randomNumber).getString("respuesta");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void verificarRespuesta(String respuestaSeleccionada, String respuestaCorrecta)
    {
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
            }
            else {
                Toast.makeText(this, "Incorrecto, intenta de nuevo. Intentos restantes: " + (3 - intentos), Toast.LENGTH_SHORT).show();
            }
        }
    }
}