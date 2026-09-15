package br.com.fiap.campusgigs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			JwtService jwtService,
			UserDetailsService userDetailsService,
			JwtAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
		JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);

		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/", "/error").permitAll()
						.requestMatchers(HttpMethod.POST, "/usuarios", "/auth/login").permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
