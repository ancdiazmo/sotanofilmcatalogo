package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sotanofilm_contenedor_pelicula")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContenedorPeliculaEntity {

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
            foreignKey = @ForeignKey(name = "fk_contenedor_pelicula_pelicula")
    )
    private PeliculaEntity pelicula;

    /**
     * Este metodo se creo con el fin de hacer el setteo bidireccional que se necesita para hacer la pesistencia y borrado
     * en cascada, el metodo lo que hace es el setteo abitual, y ademas settea para cada imagen recibida el padre
     * osea el objeto mismo, esto es necesario ya que hibernate no lo hace de manera automatica
     *
     * @param List<DetalleContenedorEntity> detalles, los detalles del contendor
     * */
    public void setDetallesYSetteoDelPadre(List<DetalleContenedorEntity> detalles) {
        this.setDetalles(detalles);
        for (DetalleContenedorEntity detalleEntity : this.detalles) {
            detalleEntity.setContenedor(this);
        }
    }

    @OneToMany(
            mappedBy = "contenedor",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
            //orphanRemoval = true
    )
    private List<DetalleContenedorEntity> detalles = new ArrayList<>();
}
