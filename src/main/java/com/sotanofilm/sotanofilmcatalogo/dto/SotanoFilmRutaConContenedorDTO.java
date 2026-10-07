package com.sotanofilm.sotanofilmcatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SotanoFilmRutaConContenedorDTO {

    private Integer id;
    private String ruta;
    private ContenedorPeliculaSinPeliculaDTO contenedor;
}
