package com.sotanofilm.sotanofilmcatalogo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrearDetalleContenedorDTO {

    private String codec;
    private Integer width;
    private Integer heigth;
    private String resolucion;
    private Float fps;
    private Integer duracionMin;
    private Float bitrateVideoKbps;
    @NotNull @Valid private CrearContenedorPeliculaSinPeliculaDTO contenedor;
}
