package com.sotanofilm.sotanofilmcatalogo.services.implementaciones;

import com.sotanofilm.sotanofilmcatalogo.dto.ContenedorPeliculaSinPeliculaDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.DetalleContenedorSinContenedorDTO;
import com.sotanofilm.sotanofilmcatalogo.entities.ContenedorPeliculaEntity;
import com.sotanofilm.sotanofilmcatalogo.repositories.ContenedorPeliculaRepository;
import com.sotanofilm.sotanofilmcatalogo.services.ContenedorPeliculaService;
import com.sotanofilm.sotanofilmcatalogo.utiles.Utiles;
import lombok.extern.log4j.Log4j2;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ContenedorPeliculaServiceImpl implements ContenedorPeliculaService {

    @Value("${sotanofilm.ffprobe}")
    private String ffprobeRutaEjecutable;

    private final ContenedorPeliculaRepository contenedorPeliculaRepository;

    @Autowired
    public ContenedorPeliculaServiceImpl(ContenedorPeliculaRepository contenedorPeliculaRepository) {
        this.contenedorPeliculaRepository = contenedorPeliculaRepository;
    }

    @Override
    public List<ContenedorPeliculaSinPeliculaDTO> obtenerTodos() {
        List<ContenedorPeliculaSinPeliculaDTO> resultado = new ArrayList<>();
        List<ContenedorPeliculaEntity> contenedoresEntities = this.contenedorPeliculaRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();
        resultado = contenedoresEntities.stream().map(contenedorEntity ->
                modelMapper.map(contenedorEntity, ContenedorPeliculaSinPeliculaDTO.class)).collect(Collectors.toList());
        return resultado;
    }

    @Override
    public List<DetalleContenedorSinContenedorDTO> analizarPelicula(String ruta) throws IOException {
        List<DetalleContenedorSinContenedorDTO> resultado = new ArrayList<>();
        if (Objects.nonNull(ruta) && !ruta.isEmpty()) {
            if (Utiles.esRutaPeliculaValida(ruta)) {
                FFprobe ffprobe = new FFprobe(this.ffprobeRutaEjecutable);
                FFmpegProbeResult probeResult = ffprobe.probe(ruta.toString());
                resultado = this.construirDetallesDTOs(probeResult);
            }
        }
        return resultado;
    }

    @Override
    public void eliminarContenedor(Integer idContenedor) {
        this.contenedorPeliculaRepository.deleteById(idContenedor);
    }

    private List<DetalleContenedorSinContenedorDTO> construirDetallesDTOs (FFmpegProbeResult probeResult) {
        List<DetalleContenedorSinContenedorDTO> detalles = new ArrayList<>();
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
                DetalleContenedorSinContenedorDTO detalle = DetalleContenedorSinContenedorDTO.builder()
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
}
