package br.com.fiap.campusgigs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

import br.com.fiap.campusgigs.client.ViaCepClient;

@Configuration
@ImportHttpServices(group = "viacep", types = ViaCepClient.class)
public class HttpClientConfig {
}
