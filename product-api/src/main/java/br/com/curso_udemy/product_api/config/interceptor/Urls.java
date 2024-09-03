package br.com.curso_udemy.product_api.config.interceptor;

import java.util.List;

public class Urls {

	public static final List<String> PROTECTED_URLS = List.of(
			"api/product",
			"api/supplier",
			"api/category"
	);
}
