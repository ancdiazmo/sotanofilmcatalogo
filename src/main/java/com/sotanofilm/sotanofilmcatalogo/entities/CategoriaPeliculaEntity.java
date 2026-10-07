package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sotanofilm_categoria_pelicula")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaPeliculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "concecutivo_folder")
    private Integer concecutivoFolder;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "nombre_separado", nullable = false, length = 255)
    private String nombreSeparado;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //La relacion con coleccion es opcional, ya que hay peliculas que no pertenecen a colecciones
    @JoinColumn(
            name = "catalogo_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_categoria_pelicula_catalogo")
    )
    private CatalogoEntity catalogo;

}
