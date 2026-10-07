package com.sotanofilm.sotanofilmcatalogo.utiles;

import com.sotanofilm.sotanofilmcatalogo.dto.CategoriaFileSystemDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.PeliculaFileSystemDTO;
import com.sotanofilm.sotanofilmcatalogo.entities.CategoriaPeliculaEntity;
import com.sotanofilm.sotanofilmcatalogo.repositories.CategoriaPeliculaRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
@Component
public class UtilesCatalogoGeneral {

    @Value("${sotanofilm.carpeta.catalogo}")
    private String carpetaCatalogo;

    private static final String JPG = ".jpg";
    private static final String JPEG = ".jpeg";
    private static final String PNG = ".png";
    private static final String WEBP = ".webp";

    private final CategoriaPeliculaRepository categoriaPeliculaRepository;

    @Autowired
    public UtilesCatalogoGeneral(CategoriaPeliculaRepository categoriaPeliculaRepository) {
        this.categoriaPeliculaRepository = categoriaPeliculaRepository;
    }

    public List<CategoriaFileSystemDTO> obtenerCatalogoXCategorias() throws Exception {
        List<CategoriaFileSystemDTO> categorias = new ArrayList<>();
        Path rutaCatalogo = Paths.get(carpetaCatalogo);
        Utiles.validarDirectorio(rutaCatalogo);
        categorias = this.obtenerCategoriasDTOs(rutaCatalogo);
        for (CategoriaFileSystemDTO categoria : categorias) {
            try (Stream<Path> peliculasPathsStream = Files.list(categoria.getRutaRaiz())) {
                List<Path> peliculasPaths = peliculasPathsStream.collect(Collectors.toList());
                this.ordernarLista(peliculasPaths);
                List<PeliculaFileSystemDTO> peliculasXCategoria = this.construirPeliculas(peliculasPaths);
                categoria.setPeliculas(peliculasXCategoria);
            }
        }
        return categorias;
    }

    private List<CategoriaFileSystemDTO> obtenerCategoriasDTOs(Path rutaCatalogo) throws IOException {
        List<CategoriaFileSystemDTO> categorias = new ArrayList<>();
        List<Path> categoriasPaths = new ArrayList<>();
        try (Stream<Path> stream = Files.list(rutaCatalogo)) {
            categoriasPaths = stream.collect(Collectors.toList());
            categoriasPaths = categoriasPaths.stream().filter(categoia -> Files.isDirectory(categoia)).collect(Collectors.toList());
            this.ordernarLista(categoriasPaths);

            List<CategoriaPeliculaEntity> categoriasEntities = this.categoriaPeliculaRepository.findAll();
            ModelMapper mapper = new ModelMapper();
            List<CategoriaFileSystemDTO> categoriasDTOs = categoriasEntities.stream().map(categoria ->
                    mapper.map(categoria, CategoriaFileSystemDTO.class )).collect(Collectors.toList());

            this.setteaCategorias(categorias, categoriasPaths, categoriasDTOs);
        } catch (Exception e) {
            log.error(e);
        }
        return categorias;
    }

    private void ordernarLista (List<Path> rutas) {
        Comparator<Path> comparatorInteger = (o1, o2) -> Integer.parseInt(o1.getFileName().toString().split("-")[0]) -
                Integer.parseInt(o2.getFileName().toString().split("-")[0]);
        rutas.sort(comparatorInteger);
    }

    private List<PeliculaFileSystemDTO> construirPeliculas (List<Path> peliculasPaths) throws Exception {
        List<PeliculaFileSystemDTO> peliculas = new ArrayList<>();
        for (Path peliculaPath : peliculasPaths) {
            if (!Utiles.contieneOtrosDirectorios(peliculaPath)) {
                PeliculaFileSystemDTO pelicula = this.construirPeliculaDesdePathWrapper(peliculaPath, "", "");
                peliculas.add(pelicula);
            } else {
                try (Stream<Path> peliculasSubCategoriaPathsStream = Files.list(peliculaPath)) {
                    List<Path> peliculasSubCategoriaPaths = peliculasSubCategoriaPathsStream.collect(Collectors.toList());
                    for (Path peliculaSubCategoriaPath : peliculasSubCategoriaPaths) {
                        String nombreSubCategoria = peliculaPath.getFileName().toString().split("-")[1]; //Sub categoria sin id
                        Path peliculaSubCategoriaSeparada = Paths.get(peliculaSubCategoriaPath + "/" + "subCategoriaSeparado.txt");
                        String nombreSubCategoriaSeparado = Files.readString(peliculaSubCategoriaSeparada);
                        PeliculaFileSystemDTO pelicula = this.construirPeliculaDesdePathWrapper(peliculaSubCategoriaPath, nombreSubCategoria, nombreSubCategoriaSeparado);
                        peliculas.add(pelicula);
                    }
                }
            }
        }
        return peliculas;
    }

    /**
     * El objetivo de este wrapper es encapsular el stream dentro del try with resources, para evitar gasto de memoria
     * */
    private PeliculaFileSystemDTO construirPeliculaDesdePathWrapper (Path peliculaPath, String nombreSubCategoria, String nombreSubCategoriaSeparado) throws Exception {
        PeliculaFileSystemDTO pelicula = new PeliculaFileSystemDTO();
        if (!Utiles.contieneOtrosDirectorios(peliculaPath)) {
            try (Stream<Path> stream = Files.list(peliculaPath))  {
                List<Path> archivosPelicula = stream.collect(Collectors.toList());
                pelicula = this.construirPeliculaDesdePath(peliculaPath, nombreSubCategoria, nombreSubCategoriaSeparado, archivosPelicula);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pelicula;
    }

    private PeliculaFileSystemDTO construirPeliculaDesdePath (Path peliculaPath, String nombreSubCategoria,
                                                              String nombreSubCategoriaSeparado, List<Path> archivosPelicula) throws IOException {
        PeliculaFileSystemDTO pelicula = new PeliculaFileSystemDTO();
        Path rutaImagen = archivosPelicula.stream().filter(archivo ->
                archivo.toString().contains(JPG) ||
                        archivo.toString().contains(JPEG) ||
                        archivo.toString().contains(PNG) ||
                        archivo.toString().contains(WEBP)
        ).findFirst().orElse(null);

        Path rutaPeliculaMKV = archivosPelicula.stream().filter(archivo -> archivo.toString().contains(".mkv")).findFirst().orElse(null);
        Path rutaPeliculaMP4 = archivosPelicula.stream().filter(archivo -> archivo.toString().contains(".mp4")).findFirst().orElse(null);
        Path rutaPeliculaAVI = archivosPelicula.stream().filter(archivo -> archivo.toString().contains(".avi")).findFirst().orElse(null);

        Path rutaDescripcionCortaPelicula = archivosPelicula.stream().filter(archivo ->
                archivo.toString().contains("descripcionCorta.txt")).findFirst().orElse(null);

        Path rutaDescripcionPelicula = archivosPelicula.stream().filter(archivo ->
                        archivo.toString().contains(".txt")
                                && !archivo.toString().contains("nombreSeparado")
                                && !archivo.toString().contains("subCategoriaSeparado")
                                && !archivo.toString().contains("descripcionCorta"))
                .findFirst().orElse(null);

        String nombreSepardo = this.settiarNombreSeparatdo(archivosPelicula);

        double tamanoPeliculaMP4 = Objects.nonNull(rutaPeliculaMP4) ? Files.size(rutaPeliculaMP4) : 0.0;
        tamanoPeliculaMP4 = tamanoPeliculaMP4 / 1073741824.0D;
        tamanoPeliculaMP4 = Math.round(tamanoPeliculaMP4 * 100.0) / 100.0;

        double tamanoPeliculaMKV = Objects.nonNull(rutaPeliculaMKV) ? Files.size(rutaPeliculaMKV) : 0.0;
        tamanoPeliculaMKV = tamanoPeliculaMKV / 1073741824.0D;
        tamanoPeliculaMKV = Math.round(tamanoPeliculaMKV * 100.0) / 100.0;

        double tamanoPeliculaAVI = Objects.nonNull(rutaPeliculaAVI) ? Files.size(rutaPeliculaAVI) : 0.0;
        tamanoPeliculaAVI = tamanoPeliculaAVI / 1073741824.0D;
        tamanoPeliculaAVI = Math.round(tamanoPeliculaAVI * 100.0) / 100.0;

        pelicula = this.crearPeliculaDTO(rutaPeliculaMP4, tamanoPeliculaMP4, rutaPeliculaMKV,
                tamanoPeliculaMKV, rutaPeliculaAVI, tamanoPeliculaAVI, rutaImagen,
                rutaDescripcionPelicula, peliculaPath, nombreSepardo, nombreSubCategoria,
                nombreSubCategoriaSeparado, rutaDescripcionCortaPelicula);

        return pelicula;
    }

    private String settiarNombreSeparatdo (List<Path> archivosPelicula) throws IOException {
        String nombreSepardo = "";
        Path rutaNombreSeparadoPelicula = archivosPelicula.stream().filter(archivo ->
                archivo.toString().contains("nombreSeparado.txt")).findFirst().orElse(null);

        if (Objects.nonNull(rutaNombreSeparadoPelicula)) {
            List<String> nombreSepardoLines = Files.readAllLines(rutaNombreSeparadoPelicula);

            if (!nombreSepardoLines.isEmpty())
                nombreSepardo = nombreSepardoLines.get(0);
        }
        return nombreSepardo;
    }

    private PeliculaFileSystemDTO crearPeliculaDTO (Path rutaPeliculaMP4, double tamanoPeliculaMP4, Path rutaPeliculaMKV,
                                                    double tamanoPeliculaMKV, Path rutaPeliculaAVI, double tamanoPeliculaAVI, Path rutaImagen,
                                                    Path rutaDescripcionPelicula, Path peliculaPath, String nombreSepardo, String nombreSubCategoria,
                                                    String nombreSubCategoriaSeparado, Path rutaDescripcionCortaPelicula) {
        return PeliculaFileSystemDTO.builder()
                .rutaPeliculaMp4(rutaPeliculaMP4)
                .tamanoPeliculaMP4(String.valueOf(tamanoPeliculaMP4) + "GB") //Dividimos para convertir a GB
                .rutaPeliculaMKV(rutaPeliculaMKV)
                .tamanoPeliculaMkv(String.valueOf(tamanoPeliculaMKV) + "GB") //Dividimos para convertir a GB
                .rutaPeliculaAVI(rutaPeliculaAVI)
                .tamanoPeliculaAVI(String.valueOf(tamanoPeliculaAVI) + "GB") //Dividimos para convertir a GB
                .rutaImagen(rutaImagen)
                .rutaDescripcionPelicula(rutaDescripcionPelicula)
                .rutaDescripcionCortaPelicula(rutaDescripcionCortaPelicula)
                .nombrePelicula(peliculaPath.getFileName().toString())
                .nombrePeliculaSeparado(nombreSepardo)
                .nombreSubCategoria(nombreSubCategoria)
                .nombreSubCategoriaSeparado(nombreSubCategoriaSeparado)
                .build();
    }

    private void setteaCategorias (List<CategoriaFileSystemDTO> categorias, List<Path> categoriasPaths, List<CategoriaFileSystemDTO> categoriasDTOs) {
        for (Path categoriaPath : categoriasPaths) {
            String nombre = categoriaPath.getFileName().toString();
            String nombreSeparado = "";
            CategoriaFileSystemDTO categoriaMatchDTO = categoriasDTOs.stream().filter(categoriasDTO
                    -> this.calcularSimilitud(nombre, categoriasDTO.getNombre()) > 0.7).findFirst().orElse(null);

            if (Objects.nonNull(categoriaMatchDTO))
                nombreSeparado = categoriaMatchDTO.getNombreSeparado();

            CategoriaFileSystemDTO categoriaDTO = CategoriaFileSystemDTO.builder()
                    .nombre(nombre)
                    .nombreSeparado(nombreSeparado)
                    .rutaRaiz(categoriaPath)
                    .build();
            categorias.add(categoriaDTO);
        }
    }

    /**
     * Este metodo hace un calculo de la similitud entre dos strings..
     *
     * @param valor1
     * @param valor2
     *
     * @return double (como valor de cuanto se parecen los dos strings)
     * */
    private double calcularSimilitud(String valor1, String valor2) {
        return new JaroWinklerSimilarity().apply(valor1, valor2);
    }
}
