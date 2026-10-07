package com.sotanofilm.sotanofilmcatalogo.repositories;

import com.sotanofilm.sotanofilmcatalogo.entities.TipoUsbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoUsbRepository extends JpaRepository<TipoUsbEntity, Integer> {

    Optional<TipoUsbEntity> findByTipo(String tipoUSB);
}
