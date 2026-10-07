package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_usb")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoUsbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "ids_contenedores", nullable = false)
    private String idsContenedores;
}
