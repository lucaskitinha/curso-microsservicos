package br.com.curso_udemy.product_api.modules.category.repository;

import br.com.curso_udemy.product_api.modules.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findByDescriptionIgnoreCaseContaining(String description);
}
