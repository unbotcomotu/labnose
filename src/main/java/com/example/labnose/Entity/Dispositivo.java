package com.example.labnose.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "dispositivo")
public class Dispositivo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Digits(integer = 10,fraction = 0)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "disponibilidad", nullable = false)
    private Integer disponibilidad;

    @Column(name = "eliminado", nullable = false)
    private Boolean eliminado;

}