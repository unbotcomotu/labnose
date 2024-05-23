package com.example.labnose.Controller;

import com.example.labnose.Entity.Dispositivo;
import com.example.labnose.Entity.DispositivoPorUsuario;
import com.example.labnose.Entity.Usuario;
import com.example.labnose.Repository.DispositivoPorUsuarioRepository;
import com.example.labnose.Repository.DispositivoRepository;
import com.example.labnose.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class KiwiController {

    private final DispositivoRepository dispositivoRepository;
    private final DispositivoPorUsuarioRepository dispositivoPorUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    public KiwiController(DispositivoRepository dispositivoRepository,
                          DispositivoPorUsuarioRepository dispositivoPorUsuarioRepository,
                          UsuarioRepository usuarioRepository) {
        this.dispositivoRepository = dispositivoRepository;
        this.dispositivoPorUsuarioRepository = dispositivoPorUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/dispositivos")
    public String dispositivos(Model model){
        model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
        return "dispositivos";
    }

    @GetMapping("/reservas")
    public String reservas(Model model,HttpSession session){
        Usuario usuario=(Usuario) session.getAttribute("usuario");
        if(usuario.getRol().getNombre().equals("profesor")){
            model.addAttribute("listaPrestamos",dispositivoPorUsuarioRepository.listaPrestamosProfesor(usuario.getId()));
        }else {
            model.addAttribute("listaPrestamos",dispositivoPorUsuarioRepository.listaPrestamosAlumno(usuario.getId()));
        }
        return "reservas";
    }

    @GetMapping("/prestamos")
    public String prestamos(Model model,HttpSession session){
        Usuario usuario=(Usuario) session.getAttribute("usuario");
        model.addAttribute("listaReservas",dispositivoPorUsuarioRepository.listaReservasAlumno(usuario.getId()));
        return "prestamos";
    }

    @GetMapping("/vistaAgregarDispositivo")
    public String vistaAgregarDispositivo(Model model, @ModelAttribute("dispositivo") Dispositivo dispositivo){
        return "agregarEditarDispositivo";
    }

    @GetMapping("/vistaEditarDispositivo")
    public String vistaEditarDispositivo(Model model, @ModelAttribute("dispositivo") Dispositivo dispositivo, HttpSession session,
                                         @RequestParam("id")Integer id){
        Optional<Dispositivo>optDispositivo=dispositivoRepository.findById(id);
        if(optDispositivo.isPresent()){
            dispositivo=optDispositivo.get();
            model.addAttribute("dispositivo",dispositivo);
            return "agregarEditarDispositivo";
        }else {
            return "redirect:/dispositivos";
        }
    }

    @PostMapping("/actualizarListaDispositivos")
    public String actualizarListaDispositivos(@ModelAttribute("dispositivo") @Valid Dispositivo dispositivo, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "agregarEditarDispositivo";
        }else {
            if(dispositivo.getId()!=null){
                dispositivoRepository.editarDispositivo(dispositivo.getId(),dispositivo.getNombre(),dispositivo.getCantidad());
            }else{
                dispositivoRepository.agregarDispositivo(dispositivo.getNombre(),dispositivo.getCantidad());
            }
            return "redirect:/dispositivos";
        }
    }

    @GetMapping("/eliminarDispositivo")
    public String eliminarDispositivo(@RequestParam("id")Integer id){
        dispositivoRepository.eliminarDispositivo(id);
        return "redirect:/dispositivos";
    }

    @GetMapping("/vistaAgregarPrestamo")
    public String vistaAgregarPrestamo(Model model,@ModelAttribute("dispositivoPorUsuario") DispositivoPorUsuario dispositivoPorUsuario){
        model.addAttribute("listaAlumnos",usuarioRepository.findAll());
        model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
        return "agregarPrestamo";
    }

    @PostMapping("/agregarPrestamo")
    public String agregarPrestamo(Model model,@ModelAttribute("dispositivoPorUsuario") @Valid DispositivoPorUsuario dispositivoPorUsuario, BindingResult bindingResult){
        Dispositivo dispositivo=dispositivoPorUsuario.getDispositivo();
        Timestamp fechaInicio= Timestamp.valueOf(dispositivoPorUsuario.getFechaInicio());
        Timestamp fechaFin= Timestamp.valueOf(dispositivoPorUsuario.getFechaFin());
        Boolean esValido=true;
        if(fechaFin.before(fechaInicio)){
            model.addAttribute("errorFecha","La fecha de inicio no puede ser después de la de culminación");
            esValido=false;
        }
        if(dispositivo.getDisponibilidad()==0){
            model.addAttribute("errorStock","No hay disponibilidad suficiente");
            esValido=false;
        }
        if(bindingResult.hasErrors()){
            esValido=false;
        }
        if(esValido){
            dispositivoPorUsuarioRepository.agregarDispositivoPorUsuario(dispositivoPorUsuario.getAlumno().getId(),dispositivoPorUsuario.getProfesor().getId(),dispositivoPorUsuario.getDispositivo().getId(),"Préstamo",dispositivoPorUsuario.getFechaInicio(),dispositivoPorUsuario.getFechaFin());
            dispositivoRepository.actualizarDisponibilidad(dispositivo.getId());
            return "redirect:/prestamos";
        }else {
            return "agregarPrestamo";
        }
    }

    @GetMapping("/vistaAgregarReserva")
    public String vistaAgregarReserva(Model model,@ModelAttribute("dispositivoPorUsuario") DispositivoPorUsuario dispositivoPorUsuario){
        model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
        return "agregarReserva";
    }

    @PostMapping("/agregarReserva")
    public String agregarReserva(Model model,@ModelAttribute("dispositivoPorUsuario") @Valid DispositivoPorUsuario dispositivoPorUsuario, BindingResult bindingResult){
        Dispositivo dispositivo=dispositivoPorUsuario.getDispositivo();
        Timestamp fechaInicio= Timestamp.valueOf(dispositivoPorUsuario.getFechaInicio());
        Timestamp fechaFin= Timestamp.valueOf(dispositivoPorUsuario.getFechaFin());
        Boolean esValido=true;
        if(fechaFin.before(fechaInicio)){
            model.addAttribute("errorFecha","La fecha de inicio no puede ser después de la de culminación");
            esValido=false;
        }
        if(dispositivo.getDisponibilidad()==0){
            model.addAttribute("errorStock","No hay disponibilidad suficiente");
            esValido=false;
        }
        if(bindingResult.hasErrors()){
            esValido=false;
        }
        if(esValido){
            dispositivoPorUsuarioRepository.agregarDispositivoPorUsuario(dispositivoPorUsuario.getAlumno().getId(),null,dispositivoPorUsuario.getDispositivo().getId(),"Reserva",dispositivoPorUsuario.getFechaInicio(),dispositivoPorUsuario.getFechaFin());
            dispositivoRepository.actualizarDisponibilidad(dispositivo.getId());
            return "redirect:/dispositivos";
        }else {
            return "agregarPrestamo";
        }
    }
}
