package com.sotanofilm.sotanofilmcatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColeccionPeliculaDTO {

    private Integer id;
    private Integer consecutivoFolder;
    private String ruta;
    private String nombreColeccion;
    private String nombre;
    private String nombreSeparado;
    private CategoriaPeliculaDTO categoria;
}
