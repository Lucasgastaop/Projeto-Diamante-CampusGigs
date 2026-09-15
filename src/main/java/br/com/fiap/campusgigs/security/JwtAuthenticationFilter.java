package br.com.fiap.campusgigs.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header != null && header.startsWith(BEARER_PREFIX)
				&& SecurityContextHolder.getContext().getAuthentication() == null) {
			String token = header.substring(BEARER_PREFIX.length());
			autenticarSeTokenValido(token, request);
		}

		filterChain.doFilter(request, response);
	}

	private void autenticarSeTokenValido(String token, HttpServletRequest request) {
		if (!jwtService.isValid(token)) {
			return;
		}

		try {
			String email = jwtService.extractEmail(token);
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (UsernameNotFoundException ignored) {
			SecurityContextHolder.clearContext();
		}
	}
}
