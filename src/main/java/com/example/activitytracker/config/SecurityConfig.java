package com.example.activitytracker.config;

import com.example.activitytracker.repository.UserRepository;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sm ->
				sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth ->
				auth
					.requestMatchers("/h2-console/**")
					.permitAll()
					.anyRequest()
					.authenticated()
			)
			.httpBasic(Customizer.withDefaults())
			.headers(headers -> headers.frameOptions(f -> f.sameOrigin())); // per H2 console
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return username ->
			userRepository
			.findByUsername(username)
			.map(u ->
				new org.springframework.security.core.userdetails.User(
					u.getUsername(),
					u.getPassword(),
					Collections.emptyList()
				)
			)
			.orElseThrow(() ->
				new UsernameNotFoundException("Utente non trovato: " + username)
			);
	}
}
