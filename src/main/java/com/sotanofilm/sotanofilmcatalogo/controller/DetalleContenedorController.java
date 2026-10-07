package com.sotanofilm.sotanofilmcatalogo.controller;

import com.sotanofilm.sotanofilmcatalogo.dto.CrearDetalleContenedorDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.DetalleContenedorDTO;
import com.sotanofilm.sotanofilmcatalogo.services.DetalleContenedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/DetalleContenedorController")
public class DetalleContenedorController {

    private final DetalleContenedorService detalleContenedorService;

    @Autowired
    public DetalleContenedorController(DetalleContenedorService contenedorService) {
        this.detalleContenedorService = contenedorService;
    }

    @GetMapping(value = "/obtenerTodos", produces =  MediaType.APPLICATION_JSON_VALUE)
    public List<DetalleContenedorDTO> obtenerTodos() {
        List<DetalleContenedorDTO> resultado = new ArrayList<>();
        //resultado = this.contenedorDetalleService.obtenerTodos();
        return resultado;
    }

    @PostMapping(value = "/crear", produces = MediaType.APPLICATION_JSON_VALUE)
    public CrearDetalleContenedorDTO crear (@Valid @RequestBody CrearDetalleContenedorDTO contenedorDetalleDTO) {
        CrearDetalleContenedorDTO resultado = null;
        resultado = this.detalleContenedorService.crear(contenedorDetalleDTO);
        return resultado;
    }
}
