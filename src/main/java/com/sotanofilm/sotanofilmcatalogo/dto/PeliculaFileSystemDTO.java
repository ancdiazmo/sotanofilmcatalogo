package com.sotanofilm.sotanofilmcatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeliculaFileSystemDTO {

    private Path rutaPeliculaMp4;
    private String tamanoPeliculaMP4;
    private Path rutaPeliculaMKV;
    private String tamanoPeliculaMkv;
    private Path rutaPeliculaAVI;
    private String tamanoPeliculaAVI;

    private String nombreSubCategoria;
    private String nombreSubCategoriaSeparado;
    private Path rutaImagen;
    private Path rutaDescripcionPelicula;
    private Path rutaDescripcionCortaPelicula;
    private String nombrePelicula;
    private String nombrePeliculaSeparado;
}
