package com.sotanofilm.sotanofilmcatalogo.services.implementaciones;

import com.sotanofilm.sotanofilmcatalogo.dto.CategoriaFileSystemDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.PeliculaFileSystemDTO;
import com.sotanofilm.sotanofilmcatalogo.entities.CategoriaPeliculaEntity;
import com.sotanofilm.sotanofilmcatalogo.repositories.CategoriaPeliculaRepository;
import com.sotanofilm.sotanofilmcatalogo.services.CatalogoWordService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.modelmapper.ModelMapper;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBackground;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class CatalogoWordServiceImpl implements CatalogoWordService {

    private String rgbColorTable = "000000";
    private static final String JPG = ".jpg";
    private static final String JPEG = ".jpeg";
    private static final String PNG = ".png";
    private static final String WEBP = ".webp";
    private static final String NOMBRE_WORD_CATALOGO = "CatalogoSotanoFilmWord.docx";

    @Value ("${sotanofilm.portable.logo}")
    private String sotanoFilmPortableLogo;
    @Value ("${sotanofilm.portable.width.logo}")
    private int widthLogoPortable;
    @Value ("${sotanofilm.portable.heigth.logo}")
    private int heigthLogoPortable;
    @Value("${sotanofilm.portable.catalogo.word}")
    private String rutaCatalogoWordPortable;
    @Value ("${sotanofilm.portable.tamanoCellIzquiedaCatalogo}")
    private String tamanoCellIzquiedaCatalogo;
    @Value ("${sotanofilm.portable.tamanoCellDerechaCatalogo}")
    private String tamanoCellDerechaCatalogo;
    @Value ("${sotanofilm.portable.titulo.word.parrafo1}")
    private String tituloWordParrafo1;
    @Value ("${sotanofilm.portable.titulo.word.parrafo2}")
    private String tituloWordParrafo2;
    @Value ("${sotanofilm.portable.width.imagen}")
    private int widthImagen;
    @Value ("${sotanofilm.portable.heigth.imagen}")
    private int heigthImagen;
    @Value("${sotanofilm.carpeta.catalogo}")
    private String carpetaCatalogo;

    private final CategoriaPeliculaRepository categoriaRepository;

    @Autowired
    public CatalogoWordServiceImpl(CategoriaPeliculaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void crearCatalogoWordWrapper() throws Exception {
        List<CategoriaFileSystemDTO> categoriasDTOs = this.obtenerCatalogoXCategorias();
        try (XWPFDocument documentoCatalogoWord = new XWPFDocument (); FileOutputStream fileOutputStream = new FileOutputStream(this.rutaCatalogoWordPortable + "/" + NOMBRE_WORD_CATALOGO);) {
            CTDocument1 ctDocument = documentoCatalogoWord.getDocument();
            CTBackground background = ctDocument.isSetBackground() ? ctDocument.getBackground() : ctDocument.addNewBackground();
            background.setColor("15191C");
            this.crearHeaderWrapper(documentoCatalogoWord);
            documentoCatalogoWord.createParagraph(); //paragrafo separador
            this.crearCatalogoWord(categoriasDTOs, documentoCatalogoWord);
            documentoCatalogoWord.write(fileOutputStream);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void crearHeaderWrapper(XWPFDocument documento) {
        XWPFParagraph logoParagraph = documento.createParagraph();
        logoParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun runLogo = logoParagraph.createRun();

        Path rutaLogo = Paths.get(this.rutaCatalogoWordPortable + "/" + this.sotanoFilmPortableLogo);
        try (InputStream pictureData = Files.newInputStream(rutaLogo)) {
            PictureType pictureType = this.obtenerTipoImagen(rutaLogo);
            runLogo.addPicture(pictureData, pictureType, rutaLogo.getFileName().toString(),
                    Units.toEMU(this.widthLogoPortable), Units.toEMU(this.heigthLogoPortable));
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * Este metodo hace el supuesto de que el catalogo biene con sus Id, tipo 1-HistoriasCruzadas
     * */
    private void crearCatalogoWord (List<CategoriaFileSystemDTO> categoriasDTOs, XWPFDocument documento) {
        for (CategoriaFileSystemDTO categoriaDTO : categoriasDTOs) {
            this.crearCategoriaWord(documento, categoriaDTO.getNombreSeparado());
            List<PeliculaFileSystemDTO> peliculasDTOs = categoriaDTO.getPeliculas();
            XWPFTable tablaCategoria = this.crearTablaWord(documento);
            boolean esPrimeraFila = true;
            for (PeliculaFileSystemDTO peliculaDTO : peliculasDTOs) {
                this.crearFilaPeliculaWord(tablaCategoria, peliculaDTO, esPrimeraFila);
                esPrimeraFila = false;
            }
        }
    }

    private PictureType obtenerTipoImagen (Path ruta) {
        PictureType pictureType = null;
        String nombreImagen = ruta.getFileName().toString();
        if (nombreImagen.contains(JPG)) {
            pictureType = PictureType.JPEG;
        } else if (nombreImagen.contains(JPEG)) {
            pictureType = PictureType.JPEG;
        } else if (nombreImagen.contains(PNG)) {
            pictureType = PictureType.PNG;
        } else if (nombreImagen.contains(WEBP)) {
            pictureType = PictureType.JPEG;
        }
        return pictureType;
    }

    private void crearCategoriaWord (XWPFDocument documento, String nombreCategoria) {
        XWPFParagraph tituloParagraph = documento.createParagraph();
        tituloParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun runTitulo = tituloParagraph.createRun();
        runTitulo.setText(nombreCategoria.toUpperCase());
        runTitulo.setBold(true);
        runTitulo.setFontSize(22);
        runTitulo.addBreak();
    }

    private void crearFilaPeliculaWord(XWPFTable tabla, PeliculaFileSystemDTO peliculaDTO, boolean esPrimeraFila) {
        if (esPrimeraFila) {
            XWPFTableRow fila = tabla.getRow(0);
            if (Objects.nonNull(peliculaDTO.getRutaDescripcionPelicula()) &&
                    Objects.nonNull(peliculaDTO.getRutaImagen())) {

                XWPFTableCell celdaIzquierda = fila.getCell(0);
                celdaIzquierda.setWidth(this.tamanoCellIzquiedaCatalogo);
                this.agregarTexto(celdaIzquierda, peliculaDTO);

                XWPFTableCell celdaDerecha = fila.createCell();
                celdaDerecha.setWidth(this.tamanoCellDerechaCatalogo);
                this.agregarImagen(celdaDerecha, peliculaDTO);
            }
        } else {
            XWPFTableRow fila = tabla.createRow();
            if (Objects.nonNull(peliculaDTO.getRutaDescripcionPelicula()) &&
                    Objects.nonNull(peliculaDTO.getRutaImagen())) {

                XWPFTableCell celdaIzquierda = fila.getCell(0);
                celdaIzquierda.setWidth(this.tamanoCellIzquiedaCatalogo);
                this.agregarTexto(celdaIzquierda, peliculaDTO);

                XWPFTableCell celdaDerecha = fila.getCell(1);
                celdaDerecha.setWidth(this.tamanoCellDerechaCatalogo);
                this.agregarImagen(celdaDerecha, peliculaDTO);
            }
        }
    }

    private XWPFTable crearTablaWord(XWPFDocument documento) {
        XWPFTable tabla = documento.createTable();
        tabla.setTableAlignment(TableRowAlign.CENTER);
        tabla.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, rgbColorTable);
        tabla.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, rgbColorTable);
        tabla.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, rgbColorTable);
        tabla.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, rgbColorTable);
        tabla.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, rgbColorTable);
        tabla.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, rgbColorTable);
        return tabla;
    }

    private void agregarTexto (XWPFTableCell celdaIzquierda, PeliculaFileSystemDTO peliculaDTO) {
        String nombrePeliculaSeparado = this.obtenerNombreDeLaPelicula(peliculaDTO);
        XWPFParagraph paragrafoCelda = celdaIzquierda.getParagraphs().get(0);
        paragrafoCelda.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun runParagrafoTitulo = paragrafoCelda.createRun();
        runParagrafoTitulo.setText(StringUtils.capitalize(nombrePeliculaSeparado));
        runParagrafoTitulo.setBold(true);
        runParagrafoTitulo.setColor("EBC57C"); //Color dorado
        runParagrafoTitulo.setFontSize(18);
        runParagrafoTitulo.addBreak();

        XWPFRun runParagrafoDescripcion = paragrafoCelda.createRun();
        try {
            String descripcion = this.obtenerDescripcionWrapper(peliculaDTO);
            runParagrafoDescripcion.setText(descripcion);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        runParagrafoDescripcion.setBold(false);
        runParagrafoDescripcion.setFontSize(14);
        runParagrafoDescripcion.addBreak();
        runParagrafoDescripcion.addBreak();
    }

    private void agregarImagen (XWPFTableCell celdaDerecha, PeliculaFileSystemDTO peliculaDTO) {
        celdaDerecha.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER); //Hacemos alineacion verticual del contenido de la celda al centro, en nuestro caso la imagen
        XWPFParagraph paragrafoImagen = celdaDerecha.getParagraphs().get(0);
        paragrafoImagen.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun runImagenPelicula = paragrafoImagen.createRun();
        Path rutaImagen = peliculaDTO.getRutaImagen();
        if (Objects.nonNull(rutaImagen)) {
            try (InputStream inputStream = Files.newInputStream(rutaImagen)) {
                PictureType pictureType = this.obtenerTipoImagen(peliculaDTO);
                runImagenPelicula.addPicture(inputStream, pictureType, peliculaDTO.getNombrePelicula(),
                        Units.toEMU(this.widthImagen), Units.toEMU(this.heigthImagen));
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    private String obtenerDescripcionWrapper (PeliculaFileSystemDTO peliculaDTO) throws IOException {
        String resultado = "";
        resultado = this.obtenerDescripcionCorta(peliculaDTO);
        if (resultado.isEmpty()) {
            resultado = this.obtenerDescripcion(peliculaDTO);
        }
        return resultado;
    }

    private String obtenerDescripcionCorta (PeliculaFileSystemDTO peliculaDTO) throws IOException {
        String descripcion = "";
        if (Objects.nonNull(peliculaDTO) && Objects.nonNull(peliculaDTO.getRutaDescripcionCortaPelicula())) {
            List<String> descripciones = Files.readAllLines(peliculaDTO.getRutaDescripcionCortaPelicula());
            int contadorDeLineas = 0;
            for (String lineaDeDescripcion : descripciones) {
                if ("".equals(lineaDeDescripcion) && contadorDeLineas != 0) {
                    break;
                }
                descripcion = descripcion + lineaDeDescripcion;
                contadorDeLineas++;
            }
        }
        return descripcion;
    }

    private String obtenerDescripcion (PeliculaFileSystemDTO peliculaDTO) throws IOException {
        String descripcion = "";
        if (Objects.nonNull(peliculaDTO.getRutaDescripcionPelicula())) {
            List<String> descripciones = Files.readAllLines(peliculaDTO.getRutaDescripcionPelicula());
            int contadorDeLineas = 0;
            for (String lineaDeDescripcion : descripciones) {
                if ("".equals(lineaDeDescripcion) && contadorDeLineas != 0) {
                    break;
                }
                descripcion = descripcion + lineaDeDescripcion;
                contadorDeLineas++;
            }
        }
        return descripcion;
    }

    private String obtenerNombreDeLaPelicula (PeliculaFileSystemDTO peliculaDTO) {
        String resultado = "";
        String nombreSubCategoria = peliculaDTO.getNombreSubCategoria();
        String nombreSubCategoriaSeparada = peliculaDTO.getNombreSubCategoriaSeparado();
        if ("".equals(nombreSubCategoria) || Objects.isNull(nombreSubCategoria) || "".equals(nombreSubCategoriaSeparada)
                || Objects.isNull(nombreSubCategoriaSeparada)) {
            resultado = peliculaDTO.getNombrePeliculaSeparado();
        } else {
            String nombrePeliculaSeparada = peliculaDTO.getNombrePeliculaSeparado();
            double parecido = this.calcularSimilitud(nombreSubCategoriaSeparada, nombrePeliculaSeparada);
            if (parecido <= 0.62) { //Si son muy diferentes
                resultado = nombreSubCategoriaSeparada + ": " + nombrePeliculaSeparada;
            } else {
                resultado = nombrePeliculaSeparada;
            }
        }
        return resultado;
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

    private PictureType obtenerTipoImagen (PeliculaFileSystemDTO peliculaDTO) {
        PictureType pictureType = null;
        String nombreImagen = peliculaDTO.getRutaImagen().getFileName().toString();
        if (nombreImagen.contains(JPG)) {
            pictureType = PictureType.JPEG;
        } else if (nombreImagen.contains(JPEG)) {
            pictureType = PictureType.JPEG;
        } else if (nombreImagen.contains(PNG)) {
            pictureType = PictureType.PNG;
        } else if (nombreImagen.contains(WEBP)) {
            pictureType = PictureType.JPEG;
        }
        return pictureType;
    }

    private List<CategoriaFileSystemDTO> obtenerCatalogoXCategorias() throws Exception {
        List<CategoriaFileSystemDTO> categorias = new ArrayList<>();
        Path rutaCatalogo = Paths.get(carpetaCatalogo);
        this.validarDirectorio(rutaCatalogo);
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

    /**
     * Valida que un directorio sea valido
     *
     * @param directorio
     * @throws IllegalArgumentException
     */
    private void validarDirectorio(Path directorio) throws Exception {
        if (directorio == null) {
            throw new IllegalArgumentException("La ruta no puede ser null.");
        }
        if (!Files.isDirectory(directorio)) {
            throw new IllegalArgumentException("La ruta indicada no corresponde a una carpeta.");
        }
    }

    private List<CategoriaFileSystemDTO> obtenerCategoriasDTOs(Path rutaCatalogo) throws IOException {
        List<CategoriaFileSystemDTO> categorias = new ArrayList<>();
        List<Path> categoriasPaths = new ArrayList<>();
        try (Stream<Path> stream = Files.list(rutaCatalogo)) {
            categoriasPaths = stream.collect(Collectors.toList());
            categoriasPaths = categoriasPaths.stream().filter(categoia -> Files.isDirectory(categoia)).collect(Collectors.toList());
            this.ordernarLista(categoriasPaths);

            List<CategoriaPeliculaEntity> categoriasEntities = this.categoriaRepository.findAll();
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

    private List<PeliculaFileSystemDTO> construirPeliculas (List<Path> peliculasPaths) throws Exception {
        List<PeliculaFileSystemDTO> peliculas = new ArrayList<>();
        for (Path peliculaPath : peliculasPaths) {
            if (!this.contieneOtrosDirectorios(peliculaPath)) {
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

    private boolean contieneOtrosDirectorios (Path ruta) throws Exception {
        boolean resultado = false;
        try (Stream<Path> stream = Files.list(ruta)) {
            List<Path> subRutas = stream.collect(Collectors.toList());
            for (Path subRuta : subRutas) {
                if (Files.isDirectory(subRuta)) {
                    resultado = true;
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return resultado;
    }

    /**
     * El objetivo de este wrapper es encapsular el stream dentro del try with resources, para evitar gasto de memoria
     * */
    private PeliculaFileSystemDTO construirPeliculaDesdePathWrapper (Path peliculaPath, String nombreSubCategoria, String nombreSubCategoriaSeparado) throws Exception {
        PeliculaFileSystemDTO pelicula = new PeliculaFileSystemDTO();
        if (!this.contieneOtrosDirectorios(peliculaPath)) {
            try (Stream<Path> stream = Files.list(peliculaPath))  {
                List<Path> archivosPelicula = stream.collect(Collectors.toList());
                pelicula = this.construirPeliculaDesdePath(peliculaPath, nombreSubCategoria, nombreSubCategoriaSeparado, archivosPelicula);
            } catch (Exception e) {
                log.error(e);
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
}
