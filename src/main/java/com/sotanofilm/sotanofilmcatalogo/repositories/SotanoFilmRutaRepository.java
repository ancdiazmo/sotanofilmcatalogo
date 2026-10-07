package com.sotanofilm.sotanofilmcatalogo.repositories;

import com.sotanofilm.sotanofilmcatalogo.entities.SotanoFilmRutaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SotanoFilmRutaRepository extends JpaRepository<SotanoFilmRutaEntity, Integer> {

    SotanoFilmRutaEntity findByContenedorId(Integer idContenedor);

    @Query(value = """
            select distinct ruta
            from ContenedorPeliculaEntity contenedor1
            inner join SotanoFilmRutaEntity ruta on contenedor1 = ruta.contenedor
            where contenedor1.nombre like '%1080%'
            and not exists
                (select contenedor2
                    from ContenedorPeliculaEntity contenedor2
                    where contenedor2.nombre like '%480%'
                    and contenedor1.pelicula = contenedor2.pelicula)
            """)
    List<SotanoFilmRutaEntity> obtenerRutas1080Sin480();

    @Query(value = """
            select ruta
            from SotanoFilmRutaEntity ruta
            where ruta.id in (359,525,307)
            """)
    List<SotanoFilmRutaEntity> obtenerMuestraPequena();
}
