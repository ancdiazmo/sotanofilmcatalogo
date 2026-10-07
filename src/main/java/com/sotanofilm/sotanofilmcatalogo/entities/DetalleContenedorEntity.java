package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sotanofilm_detalle_contenedor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetalleContenedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "codec")
    private String codec;

    @Column(name = "codec_completo")
    private String codecCompleto;

    @Column(name = "codec_type")
    private String codecType;

    @Column(name = "width")
    private Integer width;

    @Column(name = "heigth")
    private Integer heigth;

    @Column(name = "resolucion", length = 20)
    private String resolucion;

    @Column(name = "fps")
    private Float fps;

    @Column(name = "duracion_min")
    private Integer duracionMin;

    @Column(name = "bitrate_kbps")
    private Float bitrateKbps;

    @Column(name = "tamano_KB")
    private Integer tamanoKB;

    @Column(name = "aspecto_ratio")
    private String aspectoRatio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "contenedor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_detalle_contenedor_contenedor")
    )
    private ContenedorPeliculaEntity contenedor;
}
