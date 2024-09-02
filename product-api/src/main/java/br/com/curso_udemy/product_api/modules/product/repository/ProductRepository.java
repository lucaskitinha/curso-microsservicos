package br.com.curso_udemy.product_api.modules.product.repository;

import br.com.curso_udemy.product_api.modules.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
