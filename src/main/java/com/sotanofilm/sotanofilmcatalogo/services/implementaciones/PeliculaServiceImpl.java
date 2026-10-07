package com.sotanofilm.sotanofilmcatalogo.services.implementaciones;

import com.sotanofilm.sotanofilmcatalogo.dto.PeliculaDTO;
import com.sotanofilm.sotanofilmcatalogo.entities.PeliculaEntity;
import com.sotanofilm.sotanofilmcatalogo.repositories.PeliculaRepository;
import com.sotanofilm.sotanofilmcatalogo.services.PeliculaService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
public class PeliculaServiceImpl implements PeliculaService {

    private PeliculaRepository peliculaRepository;

    @Autowired
    public PeliculaServiceImpl(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }


    @Override
    public List<PeliculaDTO> obtenerTodasLasPeliculas() {
        List<PeliculaDTO> resultado = new ArrayList<>();
        List<PeliculaEntity> peliculasEntities = this.peliculaRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();
        resultado = peliculasEntities.stream().map(pelicula ->
                modelMapper.map(pelicula, PeliculaDTO.class)).toList();
        return resultado;
    }

    @Override
    public void borrarPelicula(Integer idPelicula) {
        if (Objects.nonNull(idPelicula)) {
            this.peliculaRepository.deleteById(idPelicula);
        }
    }
}
