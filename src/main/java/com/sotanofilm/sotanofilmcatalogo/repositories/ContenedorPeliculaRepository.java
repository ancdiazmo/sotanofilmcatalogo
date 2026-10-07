package com.sotanofilm.sotanofilmcatalogo.repositories;

import com.sotanofilm.sotanofilmcatalogo.entities.ContenedorPeliculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContenedorPeliculaRepository extends JpaRepository<ContenedorPeliculaEntity, Integer> {

    //El DISTINCT es importante, ya que hace que no dupliquen los contenedores por detalle, sino que hace el container tenga sus detalles adentro
    @Query("""
            select distinct contenedor1
            from ContenedorPeliculaEntity contenedor1
            join fetch contenedor1.detalles detalles
            where detalles.codecType = 'VIDEO'
            and not exists
                  (select contenedor2
                   from ContenedorPeliculaEntity contenedor2
                   where contenedor2.nombre like '%Portable%'
                   and contenedor1.pelicula = contenedor2.pelicula) 
                   order by contenedor1.tamanoKB DESC""")
    List<ContenedorPeliculaEntity> obtenerContenedoresAReducirTamano();

    @Query("""
            select contenedor
            from ContenedorPeliculaEntity contenedor
            where contenedor.nombre like '%Portable%'""")
    List<ContenedorPeliculaEntity> obtenerContenedoresPortables();
}
