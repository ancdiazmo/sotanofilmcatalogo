package com.sotanofilm.sotanofilmcatalogo.services;

import com.sotanofilm.sotanofilmcatalogo.dto.CategoriaFileSystemDTO;

import java.nio.file.Path;
import java.util.List;

public interface CatalogoService {
    List<Path> catalogo();
    Integer cantidadCatalogoBajaResolucion() throws Exception;
    Integer cantidadCatalogoAltaResolucion() throws Exception;
}
