package br.com.curso_udemy.product_api.modules.category.service;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.modules.category.dto.CategoryRequest;
import br.com.curso_udemy.product_api.modules.category.dto.CategoryResponse;
import br.com.curso_udemy.product_api.modules.category.model.Category;
import br.com.curso_udemy.product_api.modules.category.repository.CategoryRepository;
import br.com.curso_udemy.product_api.modules.product.service.ProductService;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierRequest;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.modules.supplier.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ProductService productService;

	public CategoryResponse findByIdResponse(Integer id) {
		validateInformedId(id);

		return CategoryResponse.of(findById(id));
	}

	public List<CategoryResponse> findByDescription(String description) {
		if(isEmpty(description)) {
            throw new ValidationException("The category description must be informed");
        }
		return categoryRepository
				.findByDescriptionIgnoreCaseContaining(description)
				.stream()
				.map(CategoryResponse::of)
				.collect(Collectors.toList());
	}

	public List<CategoryResponse> findAll() {
		return categoryRepository
				.findAll()
				.stream()
				.map(CategoryResponse::of)
				.collect(Collectors.toList());
	}

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

	public CategoryResponse update(CategoryRequest categoryRequest, Integer id) {
		validateCategoryNameInformed(categoryRequest);
		validateInformedId(id);
		var category  = Category.of(categoryRequest);
		category.setId(id);
		categoryRepository.save(category);
		return CategoryResponse.of(category);
	}

	public SuccessResponse delete(Integer id) {
		validateInformedId(id);
		if(productService.existsBySupplierId(id)) {
			throw new ValidationException("Cannot delete category with associated products");
		}
		categoryRepository.deleteById(id);
		return SuccessResponse.create("Category deleted successfully");
	}

	private void validateInformedId(Integer id) {
		if(isEmpty(id)) {
			throw new ValidationException("The Category id was not informed");
		}
	}

	private void validateCategoryNameInformed(CategoryRequest categoryRequest) {
		if(isEmpty(categoryRequest.getDescription())) {
			throw new ValidationException("The category description was not informed");
		}
	}
}
