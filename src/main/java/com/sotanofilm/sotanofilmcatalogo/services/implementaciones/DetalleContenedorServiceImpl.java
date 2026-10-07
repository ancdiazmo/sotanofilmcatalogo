package com.sotanofilm.sotanofilmcatalogo.services.implementaciones;

import com.sotanofilm.sotanofilmcatalogo.dto.ContenedorPeliculaSinPeliculaDTO;
import com.sotanofilm.sotanofilmcatalogo.dto.CrearDetalleContenedorDTO;
import com.sotanofilm.sotanofilmcatalogo.entities.ContenedorPeliculaEntity;
import com.sotanofilm.sotanofilmcatalogo.entities.DetalleContenedorEntity;
import com.sotanofilm.sotanofilmcatalogo.repositories.ContenedorPeliculaRepository;
import com.sotanofilm.sotanofilmcatalogo.repositories.DetalleContenedorRepository;
import com.sotanofilm.sotanofilmcatalogo.services.DetalleContenedorService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Log4j2
@Service
public class DetalleContenedorServiceImpl implements DetalleContenedorService {

    @Value("${sotanofilm.ffprobe}")
    private String ffprobeRutaEjecutable;

    private final DetalleContenedorRepository detalleContenedorRepository;
    private final ContenedorPeliculaRepository contenedorPeliculaRepository;

    @Autowired
    public DetalleContenedorServiceImpl(DetalleContenedorRepository contenedorDetalleRepository,
                                        ContenedorPeliculaRepository contenedorPeliculaRepository) {
        this.detalleContenedorRepository = contenedorDetalleRepository;
        this.contenedorPeliculaRepository = contenedorPeliculaRepository;
    }

    @Override
    public CrearDetalleContenedorDTO crear(@Valid CrearDetalleContenedorDTO contenedorDetalleDTO) { //Se agrega @Valida en metodo en caso de ser usado desde otros lugares
        CrearDetalleContenedorDTO resultado = null;
        ModelMapper modelMapper = new ModelMapper();
        DetalleContenedorEntity entity = modelMapper.map(contenedorDetalleDTO, DetalleContenedorEntity.class);
        DetalleContenedorEntity entityGuardada = this.detalleContenedorRepository.save(entity);
        resultado = modelMapper.map(entityGuardada, CrearDetalleContenedorDTO.class);
        return resultado;
    }

    @Override
    public boolean settiarContenedor(List<DetalleContenedorEntity> detalleEntity, ContenedorPeliculaSinPeliculaDTO contenedorDTO) {
        boolean resultado = false;
        ContenedorPeliculaEntity contenedorPeliculaEntity = this.contenedorPeliculaRepository.
                findById(contenedorDTO.getId()).orElse(null);
        if (Objects.nonNull(contenedorPeliculaEntity)) {
            for (DetalleContenedorEntity destalle :  detalleEntity) {
                destalle.setContenedor(contenedorPeliculaEntity);
            }
            resultado = true;
        }
        return resultado;
    }
}
