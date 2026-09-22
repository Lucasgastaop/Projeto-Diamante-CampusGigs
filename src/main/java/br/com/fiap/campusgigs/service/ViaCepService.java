package br.com.fiap.campusgigs.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import br.com.fiap.campusgigs.client.ViaCepClient;
import br.com.fiap.campusgigs.dto.ViaCepResponseDTO;
import br.com.fiap.campusgigs.exception.CepConsultaException;
import br.com.fiap.campusgigs.exception.CepNaoEncontradoException;

@Service
public class ViaCepService {

	private final ViaCepClient viaCepClient;

	public ViaCepService(ViaCepClient viaCepClient) {
		this.viaCepClient = viaCepClient;
	}

	public ViaCepResponseDTO buscar(String cep) {
		try {
			ViaCepResponseDTO endereco = viaCepClient.buscar(cep);
			if (endereco == null || endereco.possuiErro() || isBlank(endereco.getLocalidade())
					|| isBlank(endereco.getUf())) {
				throw new CepNaoEncontradoException("CEP não encontrado: " + cep);
			}
			return endereco;
		} catch (CepNaoEncontradoException ex) {
			throw ex;
		} catch (RestClientException ex) {
			throw new CepConsultaException("Falha ao consultar o CEP");
		}
	}

	private boolean isBlank(String valor) {
		return valor == null || valor.isBlank();
	}
}
