package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sotanofilm_ruta")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SotanoFilmRutaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "ruta")
    private String ruta;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //La relacion con coleccion es opcional, ya que hay peliculas que no pertenecen a colecciones
    @JoinColumn(
            name = "catalogo_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sotanofilm_ruta_catalogo")
    )
    private CatalogoEntity catalogo;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //La relacion con coleccion es opcional, ya que hay peliculas que no pertenecen a colecciones
    @JoinColumn(
            name = "categoria_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sotanofilm_ruta_categoria")
    )
    private CategoriaPeliculaEntity categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //La relacion con coleccion es opcional, ya que hay peliculas que no pertenecen a colecciones
    @JoinColumn(
            name = "coleccion_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sotanofilm_ruta_coleccion_pelicula")
    )
    private ColeccionPeliculaEntity coleccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //La relacion con coleccion es opcional, ya que hay peliculas que no pertenecen a colecciones
    @JoinColumn(
            name = "pelicula_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sotanofilm_ruta_pelicula")
    )
    private PeliculaEntity pelicula;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //La relacion con coleccion es opcional, ya que hay peliculas que no pertenecen a colecciones
    @JoinColumn(
            name = "contenedor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sotanofilm_ruta_contenedor")
    )
    private ContenedorPeliculaEntity contenedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //La relacion con coleccion es opcional, ya que hay peliculas que no pertenecen a colecciones
    @JoinColumn(
            name = "imagen_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sotanofilm_ruta_imagen")
    )
    private ImagenPeliculaEntity imagen;

}
