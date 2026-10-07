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
}
