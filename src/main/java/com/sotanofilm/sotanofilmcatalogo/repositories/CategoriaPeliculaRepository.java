package com.sotanofilm.sotanofilmcatalogo.repositories;

import com.sotanofilm.sotanofilmcatalogo.entities.CategoriaPeliculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoriaPeliculaRepository extends JpaRepository<CategoriaPeliculaEntity, Long> {

    Optional<CategoriaPeliculaEntity> findByNombre(String nombreCategoria);
}
