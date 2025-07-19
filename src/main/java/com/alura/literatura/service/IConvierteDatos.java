package com.alura.literatura.service;

public interface IConvierteDatos {
    // Usamos Generics <T> para que el método sirva para cualquier tipo de objeto
    <T> T obtenerDatos(String json, Class<T> clase);
}
