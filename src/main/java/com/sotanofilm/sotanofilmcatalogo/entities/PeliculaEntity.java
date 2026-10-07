package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sotanofilm_pelicula")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeliculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "concecutivo_folder")
    private Integer concecutivoFolder;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "nombre_separado", nullable = false, length = 255)
    private String nombreSeparado;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "descripcion_corta", nullable = false)
    private String descripcionCorta;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "categoria_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pelicula_categoria_pelicula")
    )
    private CategoriaPeliculaEntity categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) //La relacion con coleccion es opcional, ya que hay peliculas que no pertenecen a colecciones
    @JoinColumn(
            name = "coleccion_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pelicula_coleccion_pelicula")
    )
    private ColeccionPeliculaEntity coleccion;

    /**
     * Este metodo se creo con el fin de hacer el setteo bidireccional que se necesita para hacer la pesistencia
     * en cascada, el metodo lo que hace es el setteo abitual, y ademas settea para cada imagen recibida el padre
     * osea el objeto mismo, esto es necesario ya que hibernate no lo hace de manera automatica
     *
     * @param List<PeliculaImagenEntity> peliculasImagenesEntites, las imagenes de peliculas como tales
     * */
    public void setImagenesYSetteoDePadre(List<ImagenPeliculaEntity> peliculasImagenesEntites) {
        this.setImagenes(peliculasImagenesEntites);
        for (ImagenPeliculaEntity peliculaImagenEntity : this.imagenes) {
            peliculaImagenEntity.setPelicula(this);
        }
    }

    @OneToMany(
            mappedBy = "pelicula",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
            //orphanRemoval = true
    )
    private List<ImagenPeliculaEntity> imagenes = new ArrayList<>();

    /**
     * Este metodo se creo con el fin de hacer el setteo bidireccional que se necesita para hacer la pesistencia
     * en cascada, el metodo lo que hace es el setteo abitual, y ademas settea para cada contenedor recibido al padre
     * osea el objeto mismo, esto es necesario ya que hibernate no lo hace de manera automatica
     *
     * @param List<PeliculaContenedorEntity> peliculasContenedoresEntites, los contenedores de peliculas como tales
     * */
    public void setContenedoresYSetteoDePadre(List<ContenedorPeliculaEntity> peliculasContenedoresEntites) {
        this.setContenedores(peliculasContenedoresEntites);
        for (ContenedorPeliculaEntity peliculaContenedorEntity : this.contenedores) {
            peliculaContenedorEntity.setPelicula(this);
        }
    }

    @OneToMany(
            mappedBy = "pelicula",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
            //orphanRemoval = true
    )
    private List<ContenedorPeliculaEntity> contenedores = new ArrayList<>();
}
