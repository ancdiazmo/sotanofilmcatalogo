package com.sotanofilm.sotanofilmcatalogo.services;

public interface CatalogoPortableService {

    void crearCatalogoPortable720() throws Exception;
    void crearCatalogoPortable480() throws Exception;
    void organizarNombresNo1080Si720();
    void crearUsb(String tipoUSB, String unidadUSB);
    void organizarNombresCatalogoPortable();
}
