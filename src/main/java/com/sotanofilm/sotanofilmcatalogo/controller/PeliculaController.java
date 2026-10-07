package com.sotanofilm.sotanofilmcatalogo.controller;

import com.sotanofilm.sotanofilmcatalogo.dto.PeliculaDTO;
import com.sotanofilm.sotanofilmcatalogo.services.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/PeliculaController")
public class PeliculaController {

    private PeliculaService peliculaService;

    @Autowired
    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    @GetMapping(value = "/obtenerTodasLasPeliculas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PeliculaDTO> obtenerTodasLasPeliculas () {
        List<PeliculaDTO> resultado = new ArrayList<>();
        resultado = this.peliculaService.obtenerTodasLasPeliculas();
        return resultado;
    }

    /**
     * Nota importante: este metodo hace borrado en cascada de los contenedores, las imagenes y los detalles del contenedor
     * ejecutar con cuidado, tener en cuenta que se debe de mirar el id de la pelicula, no el del contendor
     *
     * @param Integer idPelicula
     */
    @DeleteMapping(value = "/borrarPelicula", produces = MediaType.APPLICATION_JSON_VALUE)
    public void borrarPelicula (@RequestParam Integer idPelicula) {
        if (Objects.nonNull(idPelicula)) {
            this.peliculaService.borrarPelicula(idPelicula);
        }
    }
}
