package com.example.labnose.Config;

import com.example.labnose.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig {
    final DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    private RedirectStrategy redirectStrategy=new DefaultRedirectStrategy();

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource){
        JdbcUserDetailsManager users=new JdbcUserDetailsManager(dataSource);
        String sql1="select correo,contrasena,activo from usuario where correo=?";
        String sql2="select u.correo,r.nombre from usuario u inner join rol r on u.id_rol=r.id_rol where u.correo=? and u.activo=1";

        users.setUsersByUsernameQuery(sql1);
        users.setAuthoritiesByUsernameQuery(sql2);
        return users;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           UsuarioRepository usuarioRepository) throws Exception{
        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/processLogin")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .successHandler((request, response, authentication) -> {
                    DefaultSavedRequest defaultSavedRequest=(DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                    HttpSession session=request.getSession();
                    session.setAttribute("usuario",usuarioRepository.findUsuarioByCorreo(authentication.getName()));
                    if(defaultSavedRequest!=null){
                        String targetURL= defaultSavedRequest.getRedirectUrl();
                        redirectStrategy.sendRedirect(request,response,targetURL);
                    }else {
                        String rol="";
                        for(GrantedAuthority role:authentication.getAuthorities()){
                            rol=role.getAuthority();
                            break;
                        }
                        if(rol.equals("Administrador")){
                            response.sendRedirect("/dispositivos");
                        }else if(rol.equals("Profesor")){
                            response.sendRedirect("/dispositivos");
                        }else if(rol.equals("Alumno")){
                            response.sendRedirect("/dispositivos");
                        }
                    }
                });

        http.authorizeHttpRequests()
                .requestMatchers("/dispositivos").authenticated()
                .requestMatchers("/actualizarListaDispositivos","/vistaAgregarDispositivo","/vistaEditarDispositivo").hasAnyAuthority("Administrador")
                .requestMatchers("/reservas","/vistaAgregarReserva","/agregarReserva","/devolverReserva").hasAuthority("Alumno")
                .requestMatchers("/vistaAgregarPrestamo","/agregarPrestamo","/devolverPrestamo").hasAnyAuthority("Profesor")
                .requestMatchers("/prestamos").hasAnyAuthority("Alumno","Profesor")
                .anyRequest().permitAll();

        http.logout()
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

        return http.build();
    }

}
