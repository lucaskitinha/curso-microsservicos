package br.com.curso_udemy.product_api.modules.category.service;

import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.modules.category.dto.CategoryRequest;
import br.com.curso_udemy.product_api.modules.category.dto.CategoryResponse;
import br.com.curso_udemy.product_api.modules.category.model.Category;
import br.com.curso_udemy.product_api.modules.category.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public Category findById(Integer id) {
		return categoryRepository
				.findById(id)
				.orElseThrow(() -> new ValidationException(("There is no Category for the given id")));
	}

	public CategoryResponse save(CategoryRequest categoryRequest) {
		validateCategoryNameInformed(categoryRequest);
		var category = categoryRepository.save(Category.of(categoryRequest));
		return CategoryResponse.of(category);
	}

	private void validateCategoryNameInformed(CategoryRequest categoryRequest) {
		if(isEmpty(categoryRequest.getDescription())) {
			throw new ValidationException("The category description was not informed");
		}
	}
}
