package com.alura.literalura.principal;

import com.alura.literalura.dto.DatosAutor; // Necesitamos los DTOs para manejar la respuesta de la API
import com.alura.literalura.dto.DatosLibro;
import com.alura.literalura.dto.DatosResultados; // El molde principal que contiene la lista de libros
import com.alura.literalura.model.Autor; // El modelo que guardaremos en la BD
import com.alura.literalura.model.Libro; // El modelo que guardaremos en la BD
import com.alura.literalura.repository.AutorRepository; // Repositorio para guardar autores
import com.alura.literalura.repository.LibroRepository; // Repositorio para guardar libros
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;

import java.util.*; // Para usar List, Scanner, etc.
import java.util.stream.Collectors; // Para usar Streams y Collectors

public class Principal {
    // Scanner para leer la entrada del usuario
    private Scanner teclado = new Scanner(System.in);

    // Instancias de las clases que usaremos
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();

    // URL base de la API y API Key (la pondremos en application.properties más adelante)
    private String urlBase = "https://gutendex.com/books/?search="; // La URL para buscar por título/autor

    // Necesitamos los repositorios para guardar los datos en la base
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    // Constructor para recibir los repositorios inyectados por Spring
    // Este constructor será llamado por Spring cuando inyecte las dependencias
    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void mostrarMenu() {
        int opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ----------------------------------------
                    Elija la opción a través de su número:
                    1- Buscar libro por título
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar libros por idioma
                    0- Salir
                    ----------------------------------------
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine(); // Consumir el salto de línea

                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;
                    case 2:
                        listarLibrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivosEnAnio();
                        break;
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                teclado.nextLine(); // Limpiar el buffer del scanner
                opcion = -1; // Resetear la opción para continuar el bucle
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
                opcion = -1; // Resetear la opción para continuar el bucle
            }
        }
    }

    // --- Implementación de las opciones del menú ---

    private void buscarLibroPorTitulo() {
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        var nombreLibro = teclado.nextLine();
        String url = urlBase + nombreLibro.replace(" ", "+"); // Construir la URL

        try {
            String json = consumoApi.obtenerDatos(url); // Obtener el JSON de la API
            DatosResultados datosResultados = conversor.obtenerDatos(json, DatosResultados.class); // Convertir el JSON a nuestro DTO principal

            if (datosResultados != null && !datosResultados.resultados().isEmpty()) {
                DatosLibro datosLibro = datosResultados.resultados().get(0); // Tomamos el primer resultado

                // Extraer Autor y Libro de los datos de la API
                Autor autor = null;
                if (datosLibro.autor() != null && !datosLibro.autor().isEmpty()) {
                    DatosAutor datosAutor = datosLibro.autor().get(0);
                    String nombreAutor = datosAutor.nombre();

                    // --- INICIO DE LA LÓGICA PARA EVITAR DUPLICADOS ---
                    // Buscamos el autor por nombre en la base de datos
                    Optional<Autor> autorExistente = autorRepository.findByNombreIgnoreCase(nombreAutor);

                    if (autorExistente.isPresent()) {
                        // Si el autor ya existe, lo obtenemos de la base de datos
                        autor = autorExistente.get();
                        System.out.println("Autor '" + nombreAutor + "' ya existe en la base de datos.");
                    } else {
                        // Si el autor no existe, lo creamos
                        autor = new Autor();
                        autor.setNombre(nombreAutor);
                        autor.setAnioNacimiento(datosAutor.anioNacimiento());
                        // No guardamos el año de fallecimiento si no está disponible o si no lo mapeamos en el DTO.
                        // autor.setAnioFallecimiento(datosAutor.anioFallecimiento());

                        // Guardamos el nuevo autor en la base de datos
                        try {
                            autorRepository.save(autor);
                            System.out.println("Autor '" + nombreAutor + "' guardado correctamente.");
                        } catch (Exception e) {
                            // Manejo de errores al guardar autor (aunque la verificación previa debería evitarlo)
                            System.out.println("Error al guardar el autor '" + nombreAutor + "': " + e.getMessage());
                            autor = null; // Aseguramos que autor sea null si falla el guardado
                        }
                    }
                    // --- FIN DE LA LÓGICA PARA EVITAR DUPLICADOS ---
                }

                Libro libro = new Libro();
                libro.setTitulo(datosLibro.titulo());
                libro.setDescargas(datosLibro.descargas());

                List<String> idiomas = datosLibro.idiomas() != null ? datosLibro.idiomas() : Collections.emptyList();
                if (!idiomas.isEmpty()) {
                    libro.setIdioma(idiomas.get(0));
                } else {
                    libro.setIdioma("No especificado");
                }

                // Asociamos el autor al libro (solo si el autor se encontró o se guardó correctamente)
                if (autor != null) {
                    libro.setAutor(autor);
                }


//                try {
//                    libroRepository.save(libro);
//                    System.out.println("Libro '" + libro.getTitulo() + "' guardado correctamente.");
//                } catch (Exception e) {
//                    System.out.println("Error al guardar el libro '" + libro.getTitulo() + "': " + e.getMessage());
//                 }
//
//                System.out.println("----- LIBRO -----");
//                System.out.println("Título: " + libro.getTitulo());
//                System.out.println("Autor: " + (autor != null ? autor.getNombre() : "Desconocido"));
//                System.out.println("Idioma: " + libro.getIdioma());
//                System.out.println("Número de descargas: " + libro.getDescargas());
//                System.out.println("-----------------");

                // ... (código anterior para obtener autor y libro) ...

// --- INICIO DE LA LÓGICA PARA EVITAR DUPLICADOS DE LIBROS ---
// Primero, verificamos si el libro ya existe en la base de datos por su título
                Optional<Libro> libroExistente = libroRepository.findByTituloIgnoreCase(datosLibro.titulo());

                if (libroExistente.isPresent()) {
                    // Si el libro ya existe, mostramos un mensaje y no hacemos nada más con este libro
                    System.out.println("El libro '" + datosLibro.titulo() + "' ya está registrado en la base de datos.");
                } else {
                    // Si el libro no existe, procedemos a guardarlo (y su autor si es nuevo)

                    // Guardamos el autor si es nuevo
                    if (autor != null) {
                        // Una mejor práctica sería buscar el autor por nombre antes de guardarlo
                        // Si no existe, lo guardamos. Si existe, usamos la instancia que ya está en la BD.
                        // Para simplificar ahora, lo guardamos directamente.
                        autorRepository.save(autor);
                        libro.setAutor(autor);
                    }

                    // Guardamos el libro
                    try {
                        libroRepository.save(libro);
                        System.out.println("Libro '" + libro.getTitulo() + "' guardado correctamente.");
                    } catch (Exception e) {
                        System.out.println("Error al guardar el libro '" + libro.getTitulo() + "': " + e.getMessage());
                    }
                }
// --- FIN DE LA LÓGICA PARA EVITAR DUPLICADOS DE LIBROS ---

// Mostrar información del libro (si fue procesado)
                if (libro.getId() != null) { // Solo mostramos si el libro fue guardado o ya existía
                    System.out.println("----- LIBRO -----");
                    System.out.println("Título: " + libro.getTitulo());
                    System.out.println("Autor: " + (autor != null ? autor.getNombre() : "Desconocido"));
                    System.out.println("Idioma: " + libro.getIdioma());
                    System.out.println("Número de descargas: " + libro.getDescargas());
                    System.out.println("-----------------");
                }

            } else {
                System.out.println("Libro no encontrado.");
            }
        } catch (RuntimeException e) {
            // Capturar errores de la API o conversión
            System.out.println("Error al buscar el libro: " + e.getMessage());
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll(); // Obtiene todos los libros de la BD
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados aún.");
        } else {
            System.out.println("----- LIBROS REGISTRADOS -----");
            libros.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                System.out.println("Idioma: " + libro.getIdioma());
                System.out.println("Número de descargas: " + libro.getDescargas());
                System.out.println("----------------------------");
            });
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll(); // Obtiene todos los autores de la BD
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados aún.");
        } else {
            System.out.println("----- AUTORES REGISTRADOS -----");
            autores.forEach(autor -> {
                System.out.println("Autor: " + autor.getNombre());
                System.out.println("Fecha de nacimiento: " + (autor.getAnioNacimiento() != null ? autor.getAnioNacimiento() : "Desconocida"));
                // Asumiendo que el autor puede tener libros, listamos los títulos si existen
                if (autor.getLibros() != null && !autor.getLibros().isEmpty()) {
                    String librosTitulo = autor.getLibros().stream().map(Libro::getTitulo).collect(Collectors.joining(", "));
                    System.out.println("Libros: [" + librosTitulo + "]");
                } else {
                    System.out.println("Libros: []");
                }
                System.out.println("-------------------------------");
            });
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingrese el año para buscar autores vivos:");
        try {
            var anio = teclado.nextInt();
            teclado.nextLine(); // Consumir el salto de línea

            List<Autor> autoresVivos = autorRepository.findAutoresVivosEnAnio(anio); // Usa la consulta JPQL que definimos

            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + anio);
            } else {
                System.out.println("----- AUTORES VIVOS EN EL AÑO " + anio + " -----");
                autoresVivos.forEach(autor -> {
                    System.out.println("Autor: " + autor.getNombre());
                    System.out.println("Fecha de nacimiento: " + (autor.getAnioNacimiento() != null ? autor.getAnioNacimiento() : "Desconocida"));
                    System.out.println("Fecha de fallecimiento: " + (autor.getAnioFallecimiento() != null ? autor.getAnioFallecimiento() : "Aún vivo"));
                    if (autor.getLibros() != null && !autor.getLibros().isEmpty()) {
                        String librosTitulo = autor.getLibros().stream().map(Libro::getTitulo).collect(Collectors.joining(", "));
                        System.out.println("Libros: [" + librosTitulo + "]");
                    } else {
                        System.out.println("Libros: []");
                    }
                    System.out.println("-----------------------------------");
                });
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un año válido.");
            teclado.nextLine();
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma para buscar los libros (ej: es, en, fr, pt):");
        var idioma = teclado.nextLine();

        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma); // Usa la Derived Query

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma '" + idioma + "'.");
        } else {
            System.out.println("----- LIBROS EN IDIOMA: " + idioma.toUpperCase() + " -----");
            librosPorIdioma.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                System.out.println("----------------------------");
            });
        }
    }
}