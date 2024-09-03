package br.com.curso_udemy.product_api.modules.product.controller;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.modules.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.modules.product.dto.ProductResponse;
import br.com.curso_udemy.product_api.modules.product.dto.ProductSalesResponse;
import br.com.curso_udemy.product_api.modules.product.service.ProductService;
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
@RequestMapping("/api/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	@PostMapping
	public ProductResponse save(@RequestBody ProductRequest request) {
		return productService.save(request);
	}

	@GetMapping
	public List<ProductResponse> findAll() {
		return productService.findAll();
	}

	@GetMapping("/{id}")
	public ProductResponse findById(@PathVariable Integer id) {
		return productService.findByIdResponse(id);
	}

	@GetMapping("name/{name}")
	public List<ProductResponse> findByName(@PathVariable String name) {
		return productService.findByName(name);
	}

	@GetMapping("supplier/{supplierId}")
	public List<ProductResponse> findBySupplierId(@PathVariable Integer supplierId) {
		return productService.findBySupplierId(supplierId);
	}

	@GetMapping("category/{categoryId}")
	public List<ProductResponse> findByCategoryId(@PathVariable Integer categoryId) {
		return productService.findByCategoryId(categoryId);
	}

	@DeleteMapping("/{id}")
	public SuccessResponse delete(@PathVariable Integer id) {
		return productService.delete(id);
	}

	@PutMapping("/{id}")
	public ProductResponse update(@PathVariable Integer id, @RequestBody ProductRequest request) {
		return productService.update(request, id);
	}

	@GetMapping("{id}/sales")
	public ProductSalesResponse findProductSales(@PathVariable Integer id) {
		return productService.findProductSales(id);
	}
}
