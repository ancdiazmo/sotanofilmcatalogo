package com.sotanofilm.sotanofilmcatalogo.controller;

import com.sotanofilm.sotanofilmcatalogo.dto.CategoriaFileSystemDTO;
import com.sotanofilm.sotanofilmcatalogo.services.CatalogoService;
import com.sotanofilm.sotanofilmcatalogo.services.implementaciones.CatalogoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/CatalogoController")
public class CatalogoController {

    private final CatalogoService catalogoService;

    @Autowired
    public CatalogoController (CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping(value = "/obtenerCatalogoXCategorias", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CategoriaFileSystemDTO> obtenerCatalogoXCategorias () throws Exception {
        return this.catalogoService.obtenerCatalogoXCategorias();
    }

    @GetMapping(value = "/catalogo", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Path> catalogo () {
        return this.catalogoService.catalogo();
    }

    @GetMapping(value = "/catalogoBajaResolucion", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer catalogoBajaResolucion () throws Exception {
        return this.catalogoService.cantidadCatalogoBajaResolucion();
    }

    @GetMapping(value = "/catalogoAltaResolucion", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer catalogoAltaResolucion () throws Exception {
        return this.catalogoService.cantidadCatalogoAltaResolucion();
    }

}
