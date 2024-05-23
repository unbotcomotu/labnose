package com.example.labnose.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dispositivo_por_usuario")
public class DispositivoPorUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo_por_usuario", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dispositivo", nullable = false)
    private Dispositivo idDispositivo;

    @Size(max = 45)
    @Column(name = "tipo", length = 45)
    private String tipo;

    @Size(max = 45)
    @Column(name = "fecha_inicio", length = 45)
    private String fechaInicio;

    @Size(max = 45)
    @Column(name = "fecha_fin", length = 45)
    private String fechaFin;

}