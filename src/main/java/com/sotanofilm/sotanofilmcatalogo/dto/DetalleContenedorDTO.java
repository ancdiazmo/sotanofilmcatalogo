package com.sotanofilm.sotanofilmcatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleContenedorDTO {

    private Integer id;
    private String codec;
    private String codecCompleto;
    private String codecType;
    private Integer width;
    private Integer heigth;
    private String resolucion;
    private Float fps;
    private Integer duracionMin;
    private Float bitrateKbps;
    private Integer tamanoKB;
    private String aspectoRatio;
    private ContenedorPeliculaSinPeliculaDTO contenedor;
}
