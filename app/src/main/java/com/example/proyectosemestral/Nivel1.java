package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class Nivel1 extends AppCompatActivity {
    private JSONArray numeros_randoms;
    private int randomNumber = 10;
    private TextView textoPregunta;
    private ImageButton btn_regresar;
    private Button btnOp1, btnOp2, btnOp3;
    private Random random;

    private int puntaje = 0;
    private int contadorPreguntas = 0;
    private int intentos = 0;
    private static final int MAX_PREGUNTAS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel1);
        random = new Random();

        // Obtener el puntaje actual de MainActivity
        puntaje = getIntent().getIntExtra("PUNTAJE_TOTAL", 0);

        textoPregunta = findViewById(R.id.id_pregunta);
        btn_regresar = findViewById(R.id.btn_regresar);
        btnOp1 = findViewById(R.id.id_respuesta_n2);
        btnOp2 = findViewById(R.id.btn_opcion2);
        btnOp3 = findViewById(R.id.btn_opcion3);

        btn_regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Nivel1.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cargarPreguntas();
        mostrarPregunta();

    }

    private void cargarPreguntas() {
        try {
            InputStream is = getAssets().open("numeros.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            numeros_randoms = jsonObject.getJSONArray("numeros");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarPregunta() {
        if (contadorPreguntas >= MAX_PREGUNTAS) {
            // Mostrar el puntaje total y terminar el juego
            Intent intent = new Intent(Nivel1.this, Final_de_partida.class);
            intent.putExtra("PUNTAJE_TOTAL", puntaje); // Puntaje de la partida
            intent.putExtra("PUNTAJE_TOTAL_TOTAL", getIntent().getIntExtra("PUNTAJE_TOTAL", 0)); // Puntaje total del usuario
            startActivity(intent);
            finish();
            return;
        }

        randomNumber = random.nextInt(numeros_randoms.length());
        intentos = 0;
        try {
            JSONObject preguntaObject = numeros_randoms.getJSONObject(randomNumber);
            String numero = preguntaObject.getString("numero");
            textoPregunta.setText(numero);

            // Todas las posibles opciones de palabras (números en texto)
            String[] palabrasNumeros = {"cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez"};

            // Obtener la respuesta correcta del JSON
            String respuestaCorrecta = preguntaObject.getString("respuesta");

            // Crear lista de opciones e incluir directamente la respuesta correcta en la primera posición
            ArrayList<String> opcionesList = new ArrayList<>();
            opcionesList.add(respuestaCorrecta);

            // Agregar opciones adicionales que no incluyan la respuesta correcta
            while (opcionesList.size() < 3) {
                String opcionAleatoria = palabrasNumeros[random.nextInt(palabrasNumeros.length)];
                if (!opcionesList.contains(opcionAleatoria)) {
                    opcionesList.add(opcionAleatoria);
                }
            }

            // Mezclar las opciones para una posición aleatoria de la respuesta correcta
            Collections.shuffle(opcionesList);

            // Asignar texto a los botones en base a las opciones mezcladas
            btnOp1.setText(opcionesList.get(0));
            btnOp2.setText(opcionesList.get(1));
            btnOp3.setText(opcionesList.get(2));

            // Configurar listeners para verificar la respuesta seleccionada
            btnOp1.setOnClickListener(v -> verificarRespuesta(btnOp1.getText().toString(), respuestaCorrecta));
            btnOp2.setOnClickListener(v -> verificarRespuesta(btnOp2.getText().toString(), respuestaCorrecta));
            btnOp3.setOnClickListener(v -> verificarRespuesta(btnOp3.getText().toString(), respuestaCorrecta));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verificarRespuesta(String respuestaSeleccionada, String respuestaCorrecta) {
        intentos++;
        if (respuestaSeleccionada.equals(respuestaCorrecta)) {
            // Respuesta correcta
            if (intentos == 1) {
                puntaje +=2;
            } else {
                puntaje +=1;
            }
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
            contadorPreguntas++;
            mostrarPregunta();
        } else {
            // Respuesta incorrecta
            if (intentos >= 3){
                Toast.makeText(this, "Incorrecto. Fin de intentos.", Toast.LENGTH_SHORT).show();
                mostrarPregunta();
            }
            else {
                Toast.makeText(this, "Incorrecto, intenta de nuevo. Intentos restantes: " + (3 - intentos), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
