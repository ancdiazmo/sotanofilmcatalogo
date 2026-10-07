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

    @PostMapping(value = "/crearCatalogoWord", produces = MediaType.APPLICATION_JSON_VALUE)
    public void crearCatalogoWord () throws Exception {
        this.catalogoWordService.crearCatalogoWordWrapper();
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
