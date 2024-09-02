package br.com.curso_udemy.product_api.modules.supplier.model;

import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierRequest;
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
@Table(name = "supplier")
public class Supplier {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Column(name = "name", nullable = false)
	private String name;

	public static Supplier of(SupplierRequest request) {
		var supplier = new Supplier();
		BeanUtils.copyProperties(request, supplier);
		return supplier;
	}
}
