package br.com.curso_udemy.product_api.modules.category.model;

import br.com.curso_udemy.product_api.modules.category.dto.CategoryRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Column(name = "description", nullable = false)
	private String description;

	public static Category of(CategoryRequest request) {
		var category = new Category();
		BeanUtils.copyProperties(request, category);
		return category;
	}
}
