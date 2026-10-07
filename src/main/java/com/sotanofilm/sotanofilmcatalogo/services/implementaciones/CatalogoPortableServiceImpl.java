package com.sotanofilm.sotanofilmcatalogo.services.implementaciones;

import com.sotanofilm.sotanofilmcatalogo.dto.ContenedorPeliculaSinPeliculaDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.PeliculaFileSystemDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.SotanoFilmRutaConContenedorDTO;
import com.sotanofilm.sotanofilmcatalogo.entities.*;
import com.sotanofilm.sotanofilmcatalogo.repositories.ContenedorPeliculaRepository;
import com.sotanofilm.sotanofilmcatalogo.repositories.SotanoFilmRutaRepository;
import com.sotanofilm.sotanofilmcatalogo.repositories.TipoUsbRepository;
import com.sotanofilm.sotanofilmcatalogo.services.CatalogoPortableService;
import com.sotanofilm.sotanofilmcatalogo.utiles.Utiles;
import lombok.extern.log4j.Log4j2;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class CatalogoPortableServiceImpl implements CatalogoPortableService {

    @Value("${sotanofilm.ffmpeg}")
    private String ffmpegRutaEjecutable;
    @Value("${sotanofilm.ffprobe}")
    private String ffprobeRutaEjecutable;
    @Value("${sotanofilm.portable.catalogo}")
    private String rutaCatalogoPortable;

    @Value("${sotanofilm.portable.crecion.codec}")
    private String portableCreacionCodec;
    @Value("${sotanofilm.portable.crecion.video.filter.720}")
    private String portableVideoFilter720;
    @Value("${sotanofilm.portable.crecion.video.filter.480}")
    private String portableVideoFilter480;
    @Value("${sotanofilm.portable.crecion.calidad.constante}")
    private String portableCalidadConstante;
    @Value("${sotanofilm.portable.crecion.optimizado.web}")
    private String portableOptimizadoWeb;

    private final ContenedorPeliculaRepository contenedorPeliculaRepository;
    private final SotanoFilmRutaRepository sotanoFilmRutaRepository;
    private final TipoUsbRepository tipoUsbRepository;

    @Autowired
    public CatalogoPortableServiceImpl(ContenedorPeliculaRepository contenedorPeliculaRepository,
                                       SotanoFilmRutaRepository sotanoFilmRutaRepository,
                                       TipoUsbRepository tipoUsbRepository) {
        this.contenedorPeliculaRepository = contenedorPeliculaRepository;
        this.sotanoFilmRutaRepository = sotanoFilmRutaRepository;
        this.tipoUsbRepository = tipoUsbRepository;
    }

    @Override
    public void crearCatalogoPortable720() throws Exception {
        List<ContenedorPeliculaEntity> contenedoresEntities = this.contenedorPeliculaRepository
                .obtenerContenedoresAReducirTamano();
        ModelMapper modelMapper = new ModelMapper();
        List<ContenedorPeliculaSinPeliculaDTO> contenedoresDTOs = contenedoresEntities.stream()
                .map(contenedor ->
                        modelMapper.map(contenedor, ContenedorPeliculaSinPeliculaDTO.class)).collect(Collectors.toList());

        for (ContenedorPeliculaSinPeliculaDTO contenedorDTO : contenedoresDTOs) {
            //this.crearContenedorPortable(contenedorDTO, this.portableVideoFilter720); Nota: como el modelo de datos cambio, no ejecutar sin revizar, quiza toque centrar la ejecucion en rutas y no contenedores
        }

        System.out.println("Stopper");
    }

    @Override
    public void crearCatalogoPortable480() throws Exception {
        /**List<SotanoFilmRutaEntity> rutasEntities = this.sotanoFilmRutaRepository.obtenerRutas1080Sin480();
        ModelMapper modelMapper = new ModelMapper();
        Comparator<SotanoFilmRutaEntity> comparator = (o1, o2)
                -> o2.getContenedor().getTamanoKB() - o1.getContenedor().getTamanoKB();
        rutasEntities = rutasEntities.stream().sorted(comparator).collect(Collectors.toList());

        List<SotanoFilmRutaConContenedorDTO> rutasDTOs = rutasEntities.stream().map(ruta ->
                modelMapper.map(ruta, SotanoFilmRutaConContenedorDTO.class)).collect(Collectors.toList());*/

        ModelMapper modelMapper = new ModelMapper();
        List<SotanoFilmRutaEntity> rutasEntities = this.sotanoFilmRutaRepository.obtenerMuestraPequena();
        List<SotanoFilmRutaConContenedorDTO> rutasDTOs = rutasEntities.stream().map(ruta ->
                modelMapper.map(ruta, SotanoFilmRutaConContenedorDTO.class)).collect(Collectors.toList());

        for (SotanoFilmRutaConContenedorDTO rutaDTO : rutasDTOs) {
            //this.crearContenedorPortable(rutaDTO, this.portableVideoFilter480);
            this.crearContenedorPortableMuestraPequeña(rutaDTO, this.portableVideoFilter480);
        }
        System.out.println("Stopper");
    }

    @Override
    public void organizarNombresNo1080Si720() {
        List<ContenedorPeliculaEntity> contenedores = this.contenedorPeliculaRepository.obtenerContenedoresPortables();
        for (ContenedorPeliculaEntity contenedor : contenedores) {
            if (Objects.nonNull(contenedor)) {
                String nombre = contenedor.getNombre();
                if (Objects.nonNull(nombre) && !nombre.isEmpty() && nombre.contains("1080")) {
                    String nuevoNombre = nombre.replace("1080", "720");
                    contenedor.setNombre(nuevoNombre);
                    this.contenedorPeliculaRepository.save(contenedor);
                }
            }
        }

    }

    @Override
    public void crearUsb(String tipoUSB, String unidadUSB) {
        if (Objects.nonNull(tipoUSB) && !tipoUSB.isEmpty()
            && Objects.nonNull(unidadUSB) && !unidadUSB.isEmpty()) {
            TipoUsbEntity tipoUsbEntity = this.tipoUsbRepository.findByTipo(tipoUSB).orElse(null);
            if (Objects.nonNull(tipoUsbEntity)) {
                String idsContenedores = tipoUsbEntity.getIdsContenedores();
                List<String> idsContenedoresList = Arrays.asList(idsContenedores.split(","));
                for (String idContenedorStr : idsContenedoresList) {
                    this.guardarContenedorPelicula(idContenedorStr, unidadUSB);
                }
            }
        }
    }

    @Override
    public void organizarNombresCatalogoPortable() {
        Path rutaCatalogoPortablePath = Paths.get(this.rutaCatalogoPortable);
        try (Stream<Path> stream = Files.walk(rutaCatalogoPortablePath)) {
            List<Path> rutas = stream.collect(Collectors.toList());
            for (Path ruta : rutas) {
                if (Files.exists(ruta) && !Files.isDirectory(ruta)) {
                    String nombre = String.valueOf(ruta.getFileName());
                    if (nombre.contains("1080") && nombre.contains("Portable")) {
                        String nuevoNombre = nombre.replace("1080", "720");
                        Path nuevaRuta = ruta.getParent().resolve(nuevoNombre);
                        Files.move(ruta, nuevaRuta);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void guardarContenedorPelicula (String idContenedorStr, String unidadUSB) {
        Integer idContenedor =  Integer.parseInt(idContenedorStr);
        SotanoFilmRutaEntity rutaContenedorEntity = this.sotanoFilmRutaRepository.findByContenedorId(idContenedor);
        String rutaContenedorStr = rutaContenedorEntity.getRuta();
        Path rutaContenedor = Paths.get(rutaContenedorStr);
        Path rutaUSB = Paths.get(unidadUSB + ":/").resolve(rutaContenedor.getFileName()); //Aca lo que hacemos es señalas la raiz de la USB
        try {
            Files.copy(rutaContenedor, rutaUSB, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**private void crearContenedorPortable (SotanoFilmRutaConContenedorDTO rutaConContendorDTO, String videoFilter) throws Exception {

        if (Objects.nonNull(rutaConContendorDTO) && Objects.nonNull(rutaConContendorDTO.getRuta())
                && Objects.nonNull(rutaConContendorDTO.getContenedor()) && Utiles.esRutaPeliculaValida(rutaConContendorDTO.getRuta())) {
            FFmpeg ffmpeg = new FFmpeg(this.ffmpegRutaEjecutable);
            String rutaInput = rutaConContendorDTO.getRuta();
            String rutaOutput = this.crearRutaDestino(Paths.get(rutaInput), "1080", "480");
            FFmpegBuilder builder = ffmpeg.builder()
                    .overrideOutputFiles(true)
                    .setInput(rutaInput)
                    .done()
                    .addOutput(rutaOutput)
                    .setVideoCodec(this.portableCreacionCodec)
                    .setVideoFilter(videoFilter)
                    .addExtraArgs("-cq", this.portableCalidadConstante)
                    .addExtraArgs("-movflags", this.portableOptimizadoWeb)
                    .done();

            FFprobe ffprobe = new FFprobe(this.ffprobeRutaEjecutable);
            boolean resultadoOK = this.lanzarJob(ffmpeg, ffprobe, builder, rutaInput);
            if (resultadoOK) {
                Path rutaOutputPath = Paths.get(rutaOutput);
                this.guardarNuevoContenedor(rutaConContendorDTO.getContenedor(), rutaOutputPath);
            }
        }
    }*/

    private void crearContenedorPortableMuestraPequeña (SotanoFilmRutaConContenedorDTO rutaConContendorDTO, String videoFilter) throws Exception {

        if (Objects.nonNull(rutaConContendorDTO) && Objects.nonNull(rutaConContendorDTO.getRuta())
                && Objects.nonNull(rutaConContendorDTO.getContenedor()) && Utiles.esRutaPeliculaValida(rutaConContendorDTO.getRuta())) {
            FFmpeg ffmpeg = new FFmpeg(this.ffmpegRutaEjecutable);
            String rutaInput = rutaConContendorDTO.getRuta();
            String rutaOutput = "D://SotanoCatalogoDesarrolloMuestraPequeña/" + Paths.get(rutaInput).getFileName().toString().replace("1080", "480");
            FFmpegBuilder builder = ffmpeg.builder()
                    .overrideOutputFiles(true)
                    .setInput(rutaInput)
                    .done()
                    .addOutput(rutaOutput)
                    .setVideoCodec(this.portableCreacionCodec)
                    .setVideoFilter(videoFilter)
                    .addExtraArgs("-cq", this.portableCalidadConstante)
                    .addExtraArgs("-movflags", this.portableOptimizadoWeb)
                    .done();

            FFprobe ffprobe = new FFprobe(this.ffprobeRutaEjecutable);
            boolean resultadoOK = this.lanzarJob(ffmpeg, ffprobe, builder, rutaInput);
            if (resultadoOK) {
                //Path rutaOutputPath = Paths.get(rutaOutput);
                //this.guardarNuevoContenedor(rutaConContendorDTO.getContenedor(), rutaOutputPath);
            }
        }
    }

    private void guardarRuta (ContenedorPeliculaEntity guardada, String ruta) {
        SotanoFilmRutaEntity rutaEntity = SotanoFilmRutaEntity.builder()
                .ruta(ruta)
                .contenedor(guardada)
                .build();
        this.sotanoFilmRutaRepository.save(rutaEntity);
    }

    private void guardarNuevoContenedor (ContenedorPeliculaSinPeliculaDTO contenedorDTO, Path rutaOutputPath) throws Exception {
        if (Objects.nonNull(rutaOutputPath) && Utiles.esRutaPeliculaValida(rutaOutputPath)) {
            String nombre = rutaOutputPath.getFileName().toString();
            String extencion = rutaOutputPath.getFileName().toString().split("\\.")[1];
            Integer tamanoKB = this.obtenerTamanoKB(Files.size(rutaOutputPath));
            PeliculaEntity peliculaEntity = this.obtenerPeliculaEntity(contenedorDTO);
            List<DetalleContenedorEntity> detalles = this.obtenerDetalles(rutaOutputPath);
            if (Objects.nonNull(peliculaEntity)) {
                ContenedorPeliculaEntity contenedorNuevo = ContenedorPeliculaEntity.builder()
                        .nombre(nombre)
                        .extencion(extencion)
                        .tamanoKB(tamanoKB)
                        //.ruta(ruta)
                        .pelicula(peliculaEntity)
                        .build();
                contenedorNuevo.setDetallesYSetteoDelPadre(detalles); //metodo de setteo de los detalles y del padre
                ContenedorPeliculaEntity guardada = this.contenedorPeliculaRepository.save(contenedorNuevo);
                String ruta = rutaOutputPath.toString();
                this.guardarRuta(guardada, ruta);
            }
        }
    }

    private Integer obtenerTamanoKB (Long bytes) {
        Integer resultado = 0;
        if (Objects.nonNull(bytes) && bytes > 0) {
            long tamanoKBLong = bytes/1024;
            resultado = (int) tamanoKBLong;
        }
        return resultado;
    }

    private PeliculaEntity obtenerPeliculaEntity (ContenedorPeliculaSinPeliculaDTO contenedorDTO) {
        PeliculaEntity peliculaEntity = null;
        if (Objects.nonNull(contenedorDTO) && Objects.nonNull(contenedorDTO.getId())) {
            Integer id = contenedorDTO.getId();
            ContenedorPeliculaEntity contenedorPeliculaEntity = this.contenedorPeliculaRepository
                    .findById(id).orElse(null);
            if (Objects.nonNull(contenedorPeliculaEntity)) {
                peliculaEntity = contenedorPeliculaEntity.getPelicula();
            }
        }
        return peliculaEntity;
    }

    private List<DetalleContenedorEntity> obtenerDetalles (Path rutaOutputPath) throws Exception {
        List<DetalleContenedorEntity> detalles = new ArrayList<>();
        if (Objects.nonNull(rutaOutputPath) && Utiles.esRutaPeliculaValida(rutaOutputPath)) {
            FFprobe ffprobe = new FFprobe(this.ffprobeRutaEjecutable);
            FFmpegProbeResult probe = ffprobe.probe(rutaOutputPath.toString());
            detalles = this.construirDetallesEntity(probe);
        }
        return detalles;
    }

    private boolean lanzarJob (FFmpeg ffmpeg, FFprobe ffprobe, FFmpegBuilder builder, String rutaInput) throws Exception {
        boolean resultado = false;
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        System.out.println(rutaInput);
        FFmpegJob job = executor.createJob(builder, progress -> {});
        try {
            job.run();
            resultado = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Este metodo crea la ruta de destino de la pelicula portable, si la pelicula pertenece al catelaogo "..\Catalogo-V8"
     * Entonces replica la estructura de carpetas del catalogo dentro del catalogo portable, si no pertenece al catalogo
     * entonces se coloca en la raiz del catalogo portable
     * */
    private String crearRutaDestino (Path rutaPelicula, String resolucionActual, String resolucionDestino) throws Exception {
        Path ruta = null;
        if (Utiles.esRutaPeliculaValida(rutaPelicula)) {
            if (rutaPelicula.toString().contains("Catalogo-V")) { //Si la pelicula hace parte del catalogo general
                String rutaPeliculaDesdeCategoria = rutaPelicula.toString().split("1-Peliculas")[1];
                rutaPeliculaDesdeCategoria = this.rutaCatalogoPortable + rutaPeliculaDesdeCategoria.substring(0, rutaPeliculaDesdeCategoria.length());
                rutaPeliculaDesdeCategoria = rutaPeliculaDesdeCategoria.replace(".", "-Portable.");
                rutaPeliculaDesdeCategoria = rutaPeliculaDesdeCategoria.replace(resolucionActual, resolucionDestino);
                ruta = Paths.get(rutaPeliculaDesdeCategoria);
                Path rutaFolder = ruta.getParent();
                if (!Files.exists(rutaFolder))
                    Files.createDirectories(rutaFolder);

            } else {
                ruta = Paths.get(this.rutaCatalogoPortable + "/"); //Si no hace parte del catalogo se crea en al raiz del portable
            }
        }
        return ruta.toString();
    }

    private List<DetalleContenedorEntity> construirDetallesEntity (FFmpegProbeResult probeResult) {
        List<DetalleContenedorEntity> detalles = new ArrayList<>();
        if (Objects.nonNull(probeResult)) {
            for (FFmpegStream stream : probeResult.getStreams()) {
                String codec = stream.codec_name;
                String codecCompleto = stream.codec_long_name;
                String codecType = stream.codec_type.toString();
                Integer width = stream.width;
                Integer heigth = stream.height;
                String resolucion = width + "x" + heigth;
                Float fps = stream.avg_frame_rate.floatValue();
                Integer duracionMin = 0;
                Float bitrateKbps = 0F;
                Integer tamanoKB = 0;
                if (Objects.nonNull(stream.tags)) {
                    duracionMin = Utiles.obtenerDuracionEnMinutos(stream.tags.get("DURATION"));
                    bitrateKbps = Utiles.obtenerBitRateEnKB(stream.tags.get("BPS"));
                    tamanoKB = Utiles.obtenerTamanoEnKB(stream.tags.get("NUMBER_OF_BYTES"));
                }
                String aspectoRatio = stream.display_aspect_ratio;
                DetalleContenedorEntity detalle = DetalleContenedorEntity.builder()
                        .codec(codec)
                        .codecCompleto(codecCompleto)
                        .codecType(codecType)
                        .width(width)
                        .heigth(heigth)
                        .resolucion(resolucion)
                        .fps(fps)
                        .duracionMin(duracionMin)
                        .bitrateKbps(bitrateKbps)
                        .tamanoKB(tamanoKB)
                        .aspectoRatio(aspectoRatio)
                        .build();
                detalles.add(detalle);
            }
        }
        return detalles;
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
}
