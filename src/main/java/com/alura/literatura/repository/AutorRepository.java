package com.alura.literatura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    // Para la opción 4 del desafío: "Listar autores vivos en un determinado año"
    // A veces, la consulta es muy compleja para el método automático.
    // Aquí le escribimos la consulta en JPQL (parecido a SQL pero con objetos Java).
    // Le decimos: "Selecciona un autor 'a' DONDE su año de nacimiento sea menor o igual al año que te paso
    // Y (su año de fallecimiento sea nulo O su año de fallecimiento sea mayor o igual al que te paso)"
    @Query("SELECT a FROM Autor a WHERE a.anioNacimiento <= :anio AND (a.anioFallecimiento IS NULL OR a.anioFallecimiento >= :anio)")
    List<Autor> findAutoresVivosEnAnio(Integer anio);
}