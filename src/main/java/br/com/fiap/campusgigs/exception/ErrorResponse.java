package br.com.fiap.campusgigs.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
	private List<FieldErrorDetail> fieldErrors;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FieldErrorDetail {

		private String field;
		private String message;
	}
}
