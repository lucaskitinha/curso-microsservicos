package br.com.curso_udemy.product_api.modules.product.dto;

import br.com.curso_udemy.product_api.modules.category.dto.CategoryResponse;
import br.com.curso_udemy.product_api.modules.product.model.Product;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSalesResponse {

	private Integer id;
	private String name;
	@JsonProperty("quantity_available")
	private Integer quantityAvailable;
	@JsonProperty("created_at")
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime createdAt;
	private SupplierResponse supplier;
	private CategoryResponse category;
	private List<String> sales;

	public static ProductSalesResponse of(Product product, List<String> sales) {
		return ProductSalesResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.createdAt(product.getCreatedAt())
				.quantityAvailable(product.getQuantityAvailable())
				.supplier(SupplierResponse.of(product.getSupplier()))
				.category(CategoryResponse.of(product.getCategory()))
				.sales(sales)
				.build();
	}
}
