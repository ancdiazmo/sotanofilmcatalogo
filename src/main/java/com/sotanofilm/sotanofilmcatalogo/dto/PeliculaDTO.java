package com.sotanofilm.sotanofilmcatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeliculaDTO {

    private Integer id;
    private Integer concecutivoFolder;
    private String nombre;
    private String nombreSeparado;
    private String descripcion;
    private String decripcionCorta;
    private String anio;
    private CategoriaPeliculaDTO categoria;
    private ColeccionPeliculaDTO coleccion;
    private List<PeliculaImagenSinPeliculaDTO> imagenes = new ArrayList<>();
    private List<ContenedorPeliculaSinPeliculaDTO> contenedores = new ArrayList<>();
}
