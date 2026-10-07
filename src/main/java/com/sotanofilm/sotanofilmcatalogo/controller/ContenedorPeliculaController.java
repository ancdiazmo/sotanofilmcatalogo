package com.sotanofilm.sotanofilmcatalogo.controller;

import com.sotanofilm.sotanofilmcatalogo.dto.ContenedorPeliculaSinPeliculaDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.DetalleContenedorSinContenedorDTO;
import com.sotanofilm.sotanofilmcatalogo.services.ContenedorPeliculaService;
import com.sotanofilm.sotanofilmcatalogo.utiles.Utiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/ContenedorPeliculaController")
public class ContenedorPeliculaController {

    private final ContenedorPeliculaService contenedorPeliculaService;

    @Autowired
    public ContenedorPeliculaController(ContenedorPeliculaService contenedorPeliculaService) {
        this.contenedorPeliculaService = contenedorPeliculaService;
    }

    @GetMapping(value = "/obtenerTodos", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ContenedorPeliculaSinPeliculaDTO> obtenerTodos() {
        List<ContenedorPeliculaSinPeliculaDTO> resultado = new ArrayList<>();
        resultado = this.contenedorPeliculaService.obtenerTodos();
        return resultado;
    }

    @GetMapping(value = "/analizarPelicula", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DetalleContenedorSinContenedorDTO> analizarPelicula () throws Exception {
        List<DetalleContenedorSinContenedorDTO> resultado = new ArrayList<>();
        String rutaContenedor = "D:/SotanoFilmCatalogoDesarrollo/Catalogo-V8/1-AccionYAventura/9-IndianaJones_Coleccion/4-ReinoCalaberaCristal/ReinoCalaberaCristal-1080-Spanish-English-2008.mkv";
        if (Objects.nonNull(rutaContenedor) && !rutaContenedor.isEmpty()) {
            if (Utiles.esRutaPeliculaValida(rutaContenedor)) {
                resultado = this.contenedorPeliculaService.analizarPelicula(rutaContenedor);
            } else {
                throw new Exception("Ruta no valida!!!");
            }
        }
        return resultado;
    }

    /**
     * Este endpoint borra un contenedor de la BD, tenga en cuenta que el delete funciona en cascada
     * de modo que se borraran tambien los detalles del contenedor, ejecutar con mucho cuidado
     * */
    @DeleteMapping(value = "/eliminarContenedor", produces = MediaType.APPLICATION_JSON_VALUE)
    public void eliminarContenedor (@RequestParam Integer idContenedor) {
        if (Objects.nonNull(idContenedor) && idContenedor > 0) {
            this.contenedorPeliculaService.eliminarContenedor(idContenedor);
        }
    }
}
