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

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update dispositivo set disponibilidad=disponibilidad-1 where id_dispositivo=?1")
    void actualizarDisponibilidad(Integer idDispositivo);
}