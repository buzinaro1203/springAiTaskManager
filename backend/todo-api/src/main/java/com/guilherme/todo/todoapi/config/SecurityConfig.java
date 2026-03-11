package com.guilherme.todo.todoapi.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  // Bean que converte o UserService em UserDetailsService
  // @Bean
  // public UserDetailsService userDetailsService(UserService userService) {
  // return username -> userService.findByEmail(username)
  // .map(user -> org.springframework.security.core.userdetails.User.builder()
  // .username(user.getEmail())
  // .password(user.getPassword())
  // .roles("USER") // se tiver roles diferentes, ajuste aqui
  // .build())
  // .orElseThrow(() -> new RuntimeException("User not found"));
  // }

  // PasswordEncoder
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // DaoAuthenticationProvider que usa UserDetailsService + PasswordEncoder
  @Bean
  public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  // AuthenticationManager necessário para autenticação com
  // DaoAuthenticationProvider
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  // Configuração da segurança HTTP
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> {
        })
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // <--- permite preflight
            .requestMatchers("/api/users/register").permitAll()
            .anyRequest().authenticated())
        .httpBasic(withDefaults());

    return http.build();
  }
}
