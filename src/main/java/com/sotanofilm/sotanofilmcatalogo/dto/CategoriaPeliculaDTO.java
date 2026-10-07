package com.sotanofilm.sotanofilmcatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaPeliculaDTO {

    private Integer id;
    private Integer concecutivoFolder;
    private String nombre;
    private String nombreSeparado;
}
