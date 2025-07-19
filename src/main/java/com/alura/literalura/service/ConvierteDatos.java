package com.alura.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos implements IConvierteDatos {
    // ObjectMapper es la herramienta de Jackson para convertir JSON a objetos Java y viceversa.
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            // Lee el JSON y lo convierte al tipo de clase que le pasamos
            return objectMapper.readValue(json, clase);
        } catch (JsonProcessingException e) {
            // Si hay un error en la conversión, lanzamos una excepción personalizada
            throw new RuntimeException("Error al convertir JSON a objeto: " + e.getMessage());
        }
    }
}
