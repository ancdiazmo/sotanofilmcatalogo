package com.sotanofilm.sotanofilmcatalogo.services.implementaciones;

import com.sotanofilm.sotanofilmcatalogo.dto.CategoriaFileSystemDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.PeliculaFileSystemDTO;
import com.sotanofilm.sotanofilmcatalogo.entities.CategoriaPeliculaEntity;
import com.sotanofilm.sotanofilmcatalogo.repositories.CategoriaPeliculaRepository;
import com.sotanofilm.sotanofilmcatalogo.services.CatalogoService;
import com.sotanofilm.sotanofilmcatalogo.utiles.Utiles;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class CatalogoServiceImpl implements CatalogoService {

    @Value("${sotanofilm.carpeta.catalogo}")
    private String carpetaCatalogo;

    private final CategoriaPeliculaRepository categoriaRepository;

    @Autowired
    public CatalogoServiceImpl(CategoriaPeliculaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Path> catalogo() {
        List<Path> resultado = new ArrayList<>();
        Path rutaCatalogo = Paths.get(this.carpetaCatalogo);
        try (Stream<Path> stream = Files.walk(rutaCatalogo)) {
            resultado = stream.collect(Collectors.toList());
            resultado = resultado.stream().filter(ruta -> ruta.toString().contains(".mp4") ||
                    ruta.toString().contains(".mkv")).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error al tratar de obtener el catalogo metodo catalogo, clase ReproductorVideoServiceImpl");
            log.error(e);
        }
        return resultado;
    }

    @Override
    public Integer cantidadCatalogoBajaResolucion() throws Exception {
        Integer cuantos = 0;
        Path rutaCatalogo = Paths.get(carpetaCatalogo);
        Utiles.validarDirectorio(rutaCatalogo);
        try (Stream<Path> stream = Files.walk(rutaCatalogo)) {
            List<Path> archivos = stream.collect(Collectors.toList());
            cuantos = archivos.stream().filter(archivo -> !archivo.toAbsolutePath().toString()
                            .contains("1080") && (archivo.toAbsolutePath().toString().contains(".mkv")
                            || archivo.toAbsolutePath().toString().contains(".mp4"))
                            || archivo.toAbsolutePath().toString().contains(".avi"))
                    .collect(Collectors.toList()).size();
        } catch (Exception e) {
            log.error(e);
        }
        return cuantos;
    }

    @Override
    public Integer cantidadCatalogoAltaResolucion() throws Exception {
        Integer cuantos = 0;
        Path rutaCatalogo = Paths.get(carpetaCatalogo);
        Utiles.validarDirectorio(rutaCatalogo);
        try (Stream<Path> stream = Files.walk(rutaCatalogo)) {
            List<Path> archivos = stream.collect(Collectors.toList());
            cuantos = archivos.stream().filter(archivo -> archivo.toAbsolutePath().toString()
                            .contains("1080") && (archivo.toAbsolutePath().toString().contains(".mkv")
                            || archivo.toAbsolutePath().toString().contains(".mp4"))
                            || archivo.toAbsolutePath().toString().contains(".avi"))
                    .collect(Collectors.toList()).size();
        } catch (Exception e) {
            log.error(e);
        }
        return cuantos;
    }
}
