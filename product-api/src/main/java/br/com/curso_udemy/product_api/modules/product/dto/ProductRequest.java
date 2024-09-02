package br.com.curso_udemy.product_api.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductRequest {

	private String name;
	@JsonProperty("quantity_available")
	private Integer quantityAvailable;
	private Integer supplierId;
	private Integer categoryId;
}
