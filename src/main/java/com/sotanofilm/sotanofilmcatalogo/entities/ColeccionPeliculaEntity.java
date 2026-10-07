package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sotanofilm_coleccion_pelicula")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColeccionPeliculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "concecutivo_folder")
    private Integer consecutivoFolder;

    @Column(name = "nombre_coleccion", nullable = false, length = 255)
    private String nombreColeccion;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "nombre_separado", nullable = false, length = 255)
    private String nombreSeparado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "categoria_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_coleccion_pelicula_categoria")
    )
    private CategoriaPeliculaEntity categoria;
}
