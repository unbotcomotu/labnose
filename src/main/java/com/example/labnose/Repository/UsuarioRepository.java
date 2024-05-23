package com.example.labnose.Repository;

import com.example.labnose.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findUsuarioByCorreo(String correo);

    @Query(nativeQuery = true,value = "select * from usuario where id_rol=3")
    List<Usuario> listarAlumnos();
}