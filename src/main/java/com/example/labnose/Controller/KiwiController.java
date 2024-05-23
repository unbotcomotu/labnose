package com.example.labnose.Controller;

import com.example.labnose.Entity.Dispositivo;
import com.example.labnose.Entity.DispositivoPorUsuario;
import com.example.labnose.Entity.Usuario;
import com.example.labnose.Repository.DispositivoPorUsuarioRepository;
import com.example.labnose.Repository.DispositivoRepository;
import com.example.labnose.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/prestamos")
    public String prestamos(Model model,HttpSession session){
        Usuario usuario=(Usuario) session.getAttribute("usuario");
        if(usuario.getRol().getNombre().equals("Profesor")){
            model.addAttribute("listaPrestamos",dispositivoPorUsuarioRepository.listaPrestamosProfesor(usuario.getId()));
        }else {
            model.addAttribute("listaPrestamos",dispositivoPorUsuarioRepository.listaPrestamosAlumno(usuario.getId()));
        }
        return "prestamos";
    }

    @GetMapping("/reservas")
    public String reservas(Model model,HttpSession session){
        Usuario usuario=(Usuario) session.getAttribute("usuario");
        model.addAttribute("listaReservas",dispositivoPorUsuarioRepository.listaReservasAlumno(usuario.getId()));
        return "reservas";
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
        model.addAttribute("listaAlumnos",usuarioRepository.listarAlumnos());
        model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
        return "agregarPrestamo";
    }

    @PostMapping("/agregarPrestamo")
    public String agregarPrestamo(RedirectAttributes attr, HttpSession session, Model model, @ModelAttribute("dispositivoPorUsuario") @Valid DispositivoPorUsuario dispositivoPorUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            model.addAttribute("listaAlumnos",usuarioRepository.listarAlumnos());
            model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
            return "agregarPrestamo";
        }
        Dispositivo dispositivo=dispositivoPorUsuario.getDispositivo();
        Usuario profesorActual=(Usuario) session.getAttribute("usuario");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        Timestamp fechaInicio= Timestamp.valueOf(LocalDateTime.parse(dispositivoPorUsuario.getFechaInicio(), formatter));
        Timestamp fechaFin= Timestamp.valueOf(LocalDateTime.parse(dispositivoPorUsuario.getFechaFin(), formatter));
        Integer disponibilidad=dispositivoRepository.disposnibilidadPorDispositivo(dispositivo.getId());
        Boolean esValido=true;
        if(fechaFin.before(fechaInicio)){
            model.addAttribute("errorFecha","La fecha de inicio no puede ser después de la de culminación");
            esValido=false;
        }
        if(disponibilidad==0){
            model.addAttribute("errorStock","No hay disponibilidad suficiente");
            esValido=false;
        }
        if(esValido){
            dispositivoPorUsuarioRepository.agregarDispositivoPorUsuario(dispositivoPorUsuario.getAlumno().getId(),profesorActual.getId(),dispositivoPorUsuario.getDispositivo().getId(),"Préstamo",fechaInicio.toString(),fechaFin.toString());
            dispositivoRepository.aumentarDisponibilidad(dispositivo.getId(),-1);
            return "redirect:/prestamos";
        }else {
            model.addAttribute("listaAlumnos",usuarioRepository.listarAlumnos());
            model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
            return "agregarPrestamo";
        }
    }

    @GetMapping("/devolverPrestamo")
    public String devolverPrestamo(@RequestParam("id")Integer id){
        Dispositivo dispositivo= dispositivoRepository.dispositivoPorDispositivoPorUsuario(id);
        dispositivoPorUsuarioRepository.eliminarDispositivoPorUsuario(id);
        dispositivoRepository.aumentarDisponibilidad(dispositivo.getId(),1);
        return "redirect:/prestamos";
    }

    @GetMapping("/vistaAgregarReserva")
    public String vistaAgregarReserva(Model model,@ModelAttribute("dispositivoPorUsuario") DispositivoPorUsuario dispositivoPorUsuario){
        model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
        return "agregarReserva";
    }

    @PostMapping("/agregarReserva")
    public String agregarReserva(HttpSession session,Model model,@ModelAttribute("dispositivoPorUsuario") @Valid DispositivoPorUsuario dispositivoPorUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
            return "agregarReserva";
        }
        Usuario alumno=(Usuario) session.getAttribute("usuario");
        Dispositivo dispositivo=dispositivoPorUsuario.getDispositivo();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        Timestamp fechaInicio= Timestamp.valueOf(LocalDateTime.parse(dispositivoPorUsuario.getFechaInicio(), formatter));
        Timestamp fechaFin= Timestamp.valueOf(LocalDateTime.parse(dispositivoPorUsuario.getFechaFin(), formatter));
        Integer disponibilidad=dispositivoRepository.disposnibilidadPorDispositivo(dispositivo.getId());
        Boolean esValido=true;
        if(fechaFin.before(fechaInicio)){
            model.addAttribute("errorFecha","La fecha de inicio no puede ser después de la de culminación");
            esValido=false;
        }
        if(disponibilidad==0){
            model.addAttribute("errorStock","No hay disponibilidad suficiente");
            esValido=false;
        }
        if(esValido){
            dispositivoPorUsuarioRepository.agregarDispositivoPorUsuario(alumno.getId(),null,dispositivoPorUsuario.getDispositivo().getId(),"Reserva",fechaInicio.toString(),fechaFin.toString());
            dispositivoRepository.aumentarDisponibilidad(dispositivo.getId(),-1);
            return "redirect:/reservas";
        }else {
            model.addAttribute("listaDispositivos",dispositivoRepository.listarDispositivosNoEliminados());
            return "agregarReserva";
        }
    }

    @GetMapping("/devolverReserva")
    public String devolverReserva(@RequestParam("id")Integer id){
        Dispositivo dispositivo= dispositivoRepository.dispositivoPorDispositivoPorUsuario(id);
        dispositivoPorUsuarioRepository.eliminarDispositivoPorUsuario(id);
        dispositivoRepository.aumentarDisponibilidad(dispositivo.getId(),1);
        return "redirect:/reservas";
    }
}
