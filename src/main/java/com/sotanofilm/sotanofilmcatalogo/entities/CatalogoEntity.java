package com.sotanofilm.sotanofilmcatalogo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sotano_catalogo")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CatalogoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "concecutivo_folder", nullable = false)
    private Integer concecutivoFolder;

    @Column(name = "tipo", nullable = false)
    private String tipo;

}
