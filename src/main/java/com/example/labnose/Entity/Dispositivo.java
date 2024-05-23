package com.example.labnose.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dispositivo")
public class Dispositivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "disponibilidad", nullable = false)
    private Integer disponibilidad;

    @NotNull
    @Column(name = "eliminado", nullable = false)
    private Byte eliminado;

}