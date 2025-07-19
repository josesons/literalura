package com.alura.literatura.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // No queremos autores repetidos con el mismo nombre
    private String nombre;

    private Integer anioNacimiento;
    private Integer anioFallecimiento;

    // Un autor puede tener muchos libros.
    // "mappedBy" le dice a JPA que la relación ya está definida en la clase Libro, en el campo "autor".
    // "cascade" significa que si guardamos/borramos un autor, sus libros también se ven afectados.
    // "fetch" EAGER carga los libros junto con el autor.
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> libros;

    // **IMPORTANTE**: Siempre se necesita un constructor vacío para que JPA funcione.
    public Autor() {}

    // Getters, Setters y toString() ...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAnioNacimiento() {
        return anioNacimiento;
    }

    public void setAnioNacimiento(Integer anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
    }

    public Integer getAnioFallecimiento() {
        return anioFallecimiento;
    }

    public void setAnioFallecimiento(Integer anioFallecimiento) {
        this.anioFallecimiento = anioFallecimiento;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    @Override
    public String toString() {
        return "Autor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", anioNacimiento=" + anioNacimiento +
                ", anioFallecimiento=" + anioFallecimiento +
                ", libros=" + libros +
                '}';
    }
}