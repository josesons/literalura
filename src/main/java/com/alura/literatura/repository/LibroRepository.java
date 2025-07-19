package com.alura.literatura.repository;

import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Para la opción 5 del desafío: "Listar libros por idioma"
    // Spring es tan inteligente que si escribes "findByIdioma", entiende que
    // debe buscar en la columna "idioma" de la entidad Libro. ¡Es automático!
    List<Libro> findByIdioma(String idioma);
}