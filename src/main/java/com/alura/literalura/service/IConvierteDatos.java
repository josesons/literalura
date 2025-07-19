package com.alura.literalura.service;

public interface IConvierteDatos {
    // Usamos Generics <T> para que el método sirva para cualquier tipo de objeto
    <T> T obtenerDatos(String json, Class<T> clase);
}
