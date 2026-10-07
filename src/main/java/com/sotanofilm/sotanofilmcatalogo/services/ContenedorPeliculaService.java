package com.sotanofilm.sotanofilmcatalogo.services;

import com.sotanofilm.sotanofilmcatalogo.dto.ContenedorPeliculaSinPeliculaDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.DetalleContenedorSinContenedorDTO;

import java.io.IOException;
import java.util.List;

public interface ContenedorPeliculaService {
    List<ContenedorPeliculaSinPeliculaDTO> obtenerTodos();
    List<DetalleContenedorSinContenedorDTO> analizarPelicula(String rutaContenedor) throws IOException;
    void eliminarContenedor(Integer idContenedor);
}
