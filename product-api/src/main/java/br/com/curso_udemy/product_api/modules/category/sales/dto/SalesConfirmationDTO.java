package br.com.curso_udemy.product_api.modules.category.sales.dto;

import br.com.curso_udemy.product_api.modules.category.sales.enums.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesConfirmationDTO {

	private String salesId;
	private SalesStatus status;
}
