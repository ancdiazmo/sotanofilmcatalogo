package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sotanofilm_imagen_pelicula")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImagenPeliculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "extencion", length = 10)
    private String extencion;

    @Column(name = "tamano_KB")
    private Integer tamanoKB;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "pelicula_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_imagen_pelicula_pelicula")
    )
    private PeliculaEntity pelicula;

}
