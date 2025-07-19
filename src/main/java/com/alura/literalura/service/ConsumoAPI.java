package com.alura.literalura.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumoAPI {
    public String obtenerDatos(String url) {
        HttpClient client = HttpClient.newHttpClient(); // Cliente para hacer peticiones HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // Crea la URI a partir de la URL
                .build();
        HttpResponse<String> response = null;
        try {
            // Envía la petición y espera la respuesta (el cuerpo como String)
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            // Si hay un error durante la petición, lanzamos una excepción personalizada
            throw new RuntimeException("Error al obtener datos de la API: " + e.getMessage());
        }
        // Devuelve el cuerpo de la respuesta, que es el JSON como texto
        return response.body();
    }
}
