package com.sotanofilm.sotanofilmcatalogo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContenedorPeliculaSinPeliculaDTO {

    @NotNull private Integer id;
    private String nombre;
    private String extencion;
    private Integer tamanoKB;
    private List<DetalleContenedorSinContenedorDTO> detalles;
}
