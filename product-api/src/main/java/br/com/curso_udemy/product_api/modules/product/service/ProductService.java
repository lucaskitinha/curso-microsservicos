package br.com.curso_udemy.product_api.modules.product.service;

import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.modules.category.service.CategoryService;
import br.com.curso_udemy.product_api.modules.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.modules.product.dto.ProductResponse;
import br.com.curso_udemy.product_api.modules.product.model.Product;
import br.com.curso_udemy.product_api.modules.product.repository.ProductRepository;
import br.com.curso_udemy.product_api.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SupplierService supplierService;

	private static final Integer ZERO = 0;

	public ProductResponse save(ProductRequest request) {
		validateProductDataNotInformed(request);
		validateCategoryAndSupplierIdInformed(request);

		var category = categoryService.findById(request.getCategoryId());
		var supplier = supplierService.findById(request.getSupplierId());
		var product = productRepository.save(Product.of(request,category, supplier));
		return ProductResponse.of(product);
	}

	private void validateProductDataNotInformed(ProductRequest request) {
		if(isEmpty(request.getName())) {
			throw new ValidationException("The product's name was not informed");
		}
		if (isEmpty(request.getQuantityAvailable())) {
			throw new ValidationException("The product's quantity available was not informed");
		}
		if(request.getQuantityAvailable() <= ZERO) {
			throw new ValidationException("The product's quantity available must be greater than zero");
		}
	}

	private void validateCategoryAndSupplierIdInformed(ProductRequest request) {
		if(isEmpty(request.getCategoryId())) {
			throw new ValidationException("The category ID was not informed");
		}
		if (isEmpty(request.getSupplierId())) {
			throw new ValidationException("The supplier ID was not informed");
		}
	}

}
