package br.com.fiap.campusgigs.model.enums;

public enum PapelUsuario {
	ADMIN,
	USER;

	public String asRole() {
		return "ROLE_" + name();
	}
}
