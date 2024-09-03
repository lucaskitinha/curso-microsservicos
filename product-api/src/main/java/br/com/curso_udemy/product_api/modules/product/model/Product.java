package br.com.curso_udemy.product_api.modules.product.model;

import br.com.curso_udemy.product_api.modules.category.model.Category;
import br.com.curso_udemy.product_api.modules.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.modules.supplier.model.Supplier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "idsupplier", nullable = false)
	private Supplier supplier;

	@ManyToOne
	@JoinColumn(name = "idcategory", nullable = false)
	private Category category;

	@Column(name = "quantity_available", nullable = false)
	private Integer quantityAvailable;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

	public static Product of(ProductRequest request, Category category, Supplier supplier) {
        return Product.builder()
				.name(request.getName())
				.quantityAvailable(request.getQuantityAvailable())
				.category(category)
				.supplier(supplier)
				.build();
    }

	public void updateStock(Integer quantity) {
		quantityAvailable = quantityAvailable - quantity;
	}
}
