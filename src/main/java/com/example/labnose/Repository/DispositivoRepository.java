package com.example.labnose.Repository;

import com.example.labnose.Entity.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Integer> {
    @Query(nativeQuery = true,value = "select * from dispositivo where eliminado=0")
    List<Dispositivo> listarDispositivosNoEliminados();

    @Query(nativeQuery = true,value = "select * from dispositivo d inner join dispositivo_por_usuario dpu on d.id_dispositivo=dpu.id_dispositivo where dpu.id_dispositivo_por_usuario=?1")
    Dispositivo dispositivoPorDispositivoPorUsuario(Integer idDispositivoPorUsuario);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "insert into dispositivo(nombre,cantidad,disponibilidad,eliminado) values (?1,?2,?2,0)")
    void agregarDispositivo(String nombre,Integer cantidad);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update dispositivo set nombre=?2,disponibilidad=disponibilidad-(cantidad-?3),cantidad=?3 where id_dispositivo=?1")
    void editarDispositivo(Integer idDispositivo,String nombre,Integer cantidad);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update dispositivo set eliminado=1 where id_dispositivo=?1")
    void eliminarDispositivo(Integer idDispositivo);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update dispositivo set disponibilidad=disponibilidad+?2 where id_dispositivo=?1")
    void aumentarDisponibilidad(Integer idDispositivo,Integer aumento);
}