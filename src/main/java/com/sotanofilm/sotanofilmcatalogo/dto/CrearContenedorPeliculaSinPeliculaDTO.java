package com.sotanofilm.sotanofilmcatalogo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrearContenedorPeliculaSinPeliculaDTO {

    @NotNull private Integer id;
    private String nombre;
    private String extencion;
    private Integer tamanoKB;
    private String ruta;
}
