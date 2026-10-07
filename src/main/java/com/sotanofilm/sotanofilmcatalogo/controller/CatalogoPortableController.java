package com.sotanofilm.sotanofilmcatalogo.controller;

import com.sotanofilm.sotanofilmcatalogo.services.CatalogoPortableService;
import com.sotanofilm.sotanofilmcatalogo.services.CatalogoWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;

@RestController
@RequestMapping("/CatalogoPortableController")
public class CatalogoPortableController {

    private final CatalogoPortableService catalogoPortableService;
    private final CatalogoWordService catalogoWordService;

    @Autowired
    public CatalogoPortableController(CatalogoPortableService catalogoPortableService,
                                      CatalogoWordService catalogoWordService) {
        this.catalogoPortableService = catalogoPortableService;
        this.catalogoWordService = catalogoWordService;
    }

    /**
     * Este metodo utiliza ffmpeg, para crear la version portable a partir de una pelicula que se le envia
     * */
    @PostMapping(value = "/crearCatalogoPortable720", produces = MediaType.APPLICATION_JSON_VALUE)
    public void crearCatalogoPortable720 () throws Exception {
        //this.catalogoPortableService.crearCatalogoPortable720(); validar funcionalidad antes de volver a ejecutar ya que al modelo de datos se le agrego la tabla de rutas
    }

    /**
     * Este metodo utiliza ffmpeg, para crear la version portable a partir de una pelicula que se le envia
     * */
    @PostMapping(value = "/crearCatalogoPortable480", produces = MediaType.APPLICATION_JSON_VALUE)
    public void crearCatalogoPortable480 () throws Exception {
        this.catalogoPortableService.crearCatalogoPortable480();
    }

    @PostMapping(value = "/crearCatalogoWord", produces = MediaType.APPLICATION_JSON_VALUE)
    public void crearCatalogoWord () throws Exception {
        this.catalogoWordService.crearCatalogoWordWrapper();
    }

    @PostMapping(value = "/organizarNombresNo1080Si720", produces = MediaType.APPLICATION_JSON_VALUE)
    public void organizarNombresNo1080Si720 () {
        this.catalogoPortableService.organizarNombresNo1080Si720();
    }

    @PostMapping(value = "/organizarNombresCatalogoPortable", produces = MediaType.APPLICATION_JSON_VALUE)
    public void organizarNombresCatalogoPortable () {
        this.catalogoPortableService.organizarNombresCatalogoPortable();
    }

    /**
     * tipoUSB, busca el tipo en la base de datos en la tabla tipo_usb, en la columna tipo, ejemplo 'tipo1'
     * unidadUSB, es el nombre del disco en el sistema, ejemplo C, F, D
     * */
    @PostMapping(value = "/crearUsb", produces = MediaType.APPLICATION_JSON_VALUE)
    public void crearUsb (@RequestParam String tipoUSB, @RequestParam String unidadUSB) {
        if (Objects.nonNull(tipoUSB) && !tipoUSB.isEmpty()
            && Objects.nonNull(unidadUSB) && !unidadUSB.isEmpty()) {
            this.catalogoPortableService.crearUsb(tipoUSB, unidadUSB);
        }
    }
}
