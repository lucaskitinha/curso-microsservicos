package br.com.curso_udemy.product_api.modules.supplier.dto;

import br.com.curso_udemy.product_api.modules.supplier.model.Supplier;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SupplierResponse {

	private Integer id;
	private String name;

	public static SupplierResponse of(Supplier supplier) {
		var response = new SupplierResponse();
		BeanUtils.copyProperties(supplier, response);
		return response;
	}
}
