package com.sotanofilm.sotanofilmcatalogo.services;

import com.sotanofilm.sotanofilmcatalogo.dto.ContenedorPeliculaSinPeliculaDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.CrearDetalleContenedorDTO;
import com.sotanofilm.sotanofilmcatalogo.entities.DetalleContenedorEntity;

import java.util.List;

public interface DetalleContenedorService {

    CrearDetalleContenedorDTO crear(CrearDetalleContenedorDTO contenedorDetalleDTO);
    boolean settiarContenedor(List<DetalleContenedorEntity> detalleEntity, ContenedorPeliculaSinPeliculaDTO contenedorDTO);
}
