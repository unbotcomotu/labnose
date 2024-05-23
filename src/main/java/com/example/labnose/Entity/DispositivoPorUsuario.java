package com.example.labnose.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_dispositivo", nullable = false)
    private Dispositivo dispositivo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Usuario alumno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_profesor", nullable = false)
    private Usuario profesor;

    @Size(max = 45)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "tipo", length = 45)
    private String tipo;

    @Size(max = 45)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "fecha_inicio", length = 45)
    private String fechaInicio;

    @Size(max = 45)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "fecha_fin", length = 45)
    private String fechaFin;

}