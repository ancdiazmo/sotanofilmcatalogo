package com.sotanofilm.sotanofilmcatalogo.services;

import com.sotanofilm.sotanofilmcatalogo.dto.PeliculaDTO;

import java.util.List;

public interface PeliculaService {

    List<PeliculaDTO> obtenerTodasLasPeliculas();
    void borrarPelicula(Integer idPelicula);
}
