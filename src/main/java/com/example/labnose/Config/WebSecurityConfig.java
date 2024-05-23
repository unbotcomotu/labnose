package com.example.labnose.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig {
    final DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests()
                .requestMatchers("/dispositivos").authenticated()
                .requestMatchers("/reservas").hasAuthority("alumno")
                .requestMatchers("/prestamos").hasAnyAuthority("alumno","profesor")
                .anyRequest().permitAll();
        return http.build();
    }

}
