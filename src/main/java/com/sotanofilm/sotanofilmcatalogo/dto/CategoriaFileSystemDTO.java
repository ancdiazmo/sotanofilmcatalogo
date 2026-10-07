package com.sotanofilm.sotanofilmcatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaFileSystemDTO {

    private Integer id;
    private Integer concecutivoFolder;
    private String nombre;
    private String nombreSeparado;
    private Path rutaRaiz;
    @Builder.Default
    private List<PeliculaFileSystemDTO> peliculas = new ArrayList<>();
}
