package com.example.labnose.Repository;

import com.example.labnose.Entity.DispositivoPorUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DispositivoPorUsuarioRepository extends JpaRepository<DispositivoPorUsuario, Integer> {
    @Query(nativeQuery = true,value = "select * from dispositivo_por_usuario where id_profesor=?1 and tipo='Préstamo'")
    List<DispositivoPorUsuario> listaPrestamosProfesor(Integer idProfesor);

    @Query(nativeQuery = true,value = "select * from dispositivo_por_usuario where id_alumno=?1 and tipo='Préstamo'")
    List<DispositivoPorUsuario> listaPrestamosAlumno(Integer idAlumno);

    @Query(nativeQuery = true,value = "select * from dispositivo_por_usuario where id_alumno=?1 and tipo='Reserva'")
    List<DispositivoPorUsuario> listaReservasAlumno(Integer idAlumno);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "insert into dispositivo_por_usuario(id_alumno, id_profesor, id_dispositivo, tipo, fecha_inicio, fecha_fin) values(?1,?2,?3,?4,?5,?6)")
    void agregarDispositivoPorUsuario(Integer idAlumno,Integer idProfesor,Integer idDispositivo,String tipo,String fechaInicio,String fechaFin);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "delete from dispositivo_por_usuario where id_dispositivo_por_usuario=?1")
    void eliminarDispositivoPorUsuario(Integer idDispositivoPorUsuario);


}