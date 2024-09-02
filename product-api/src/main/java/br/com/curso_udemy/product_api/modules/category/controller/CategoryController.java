package br.com.curso_udemy.product_api.modules.category.controller;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.modules.category.dto.CategoryRequest;
import br.com.curso_udemy.product_api.modules.category.dto.CategoryResponse;
import br.com.curso_udemy.product_api.modules.category.service.CategoryService;
import br.com.curso_udemy.product_api.modules.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.modules.product.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PostMapping
	public CategoryResponse save(@RequestBody CategoryRequest request) {
		return categoryService.save(request);
	}

	@GetMapping
	public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

	@GetMapping("/{id}")
	public CategoryResponse findById(@PathVariable Integer id) {
        return categoryService.findByIdResponse(id);
    }

	@GetMapping("description/{description}")
	public List<CategoryResponse> findByDescription(@PathVariable String description) {
		return categoryService.findByDescription(description);
	}

	@DeleteMapping("/{id}")
	public SuccessResponse delete(@PathVariable Integer id) {
		return categoryService.delete(id);
	}

	@PutMapping("/{id}")
	public CategoryResponse update(@PathVariable Integer id, @RequestBody CategoryRequest request) {
		return categoryService.update(request, id);
	}
}
