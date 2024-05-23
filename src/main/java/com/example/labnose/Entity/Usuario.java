package com.example.labnose.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Size(max = 45)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "nombre", nullable = false,length = 45)
    private String nombre;

    @Size(max = 45)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "apellido", nullable = false,length = 45)
    private String apellido;

    @Size(max = 45)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "correo", nullable = false,length = 45)
    private String correo;

    @Size(max = 256)
    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "contrasena", nullable = false,length = 256)
    private String contrasena;

    @NotNull(message = "El campo no puede estar vacío")
    @Column(name = "activo", nullable = false)
    private Boolean activo;
}