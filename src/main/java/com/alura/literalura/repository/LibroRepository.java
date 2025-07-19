package com.alura.literalura.repository;

import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Para la opción 5 del desafío: "Listar libros por idioma"
    // Spring es tan inteligente que si escribes "findByIdioma", entiende que
    // debe buscar en la columna "idioma" de la entidad Libro. ¡Es automático!
    List<Libro> findByIdioma(String idioma);

    // En la interfaz LibroRepository
    // Busca un libro por su título, ignorando mayúsculas y minúsculas.
    // Devuelve un Optional<Libro> porque el libro podría no existir.
    Optional<Libro> findByTituloIgnoreCase(String titulo);
}