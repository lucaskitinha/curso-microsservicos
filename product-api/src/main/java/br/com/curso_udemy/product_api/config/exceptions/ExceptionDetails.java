package br.com.curso_udemy.product_api.config.exceptions;

import lombok.Data;

@Data
public class ExceptionDetails {

	private int status;
	private String message;
}
