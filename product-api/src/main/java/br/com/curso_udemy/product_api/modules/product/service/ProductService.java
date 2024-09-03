package br.com.curso_udemy.product_api.modules.product.service;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.modules.category.service.CategoryService;
import br.com.curso_udemy.product_api.modules.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.modules.product.dto.ProductResponse;
import br.com.curso_udemy.product_api.modules.product.model.Product;
import br.com.curso_udemy.product_api.modules.product.repository.ProductRepository;
import br.com.curso_udemy.product_api.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Lazy
	private CategoryService categoryService;
	@Lazy
	private SupplierService supplierService;

	private static final Integer ZERO = 0;

	public Boolean existsByCategoryId(Integer categoryId){
		return productRepository.existsByCategoryId(categoryId);
	}

	public Boolean existsBySupplierId(Integer supplierId){
		return productRepository.existsBySupplierId(supplierId);
	}

	public ProductResponse findByIdResponse(Integer id) {
		validateInformedId(id);
		return ProductResponse.of(findById(id));
	}

	public List<ProductResponse> findByName(String name) {
		if(isEmpty(name)) {
			throw new ValidationException("The product name must be informed");
		}
		return productRepository
				.findByNameIgnoreCaseContaining(name)
				.stream()
				.map(ProductResponse::of)
				.collect(Collectors.toList());
	}

	public List<ProductResponse> findBySupplierId(Integer supplierId) {
		if(isEmpty(supplierId)) {
			throw new ValidationException("The product supplier ID must be informed");
		}
		return productRepository
				.findBySupplierId(supplierId)
				.stream()
				.map(ProductResponse::of)
				.collect(Collectors.toList());
	}

	public List<ProductResponse> findByCategoryId(Integer categoryId) {
		if(isEmpty(categoryId)) {
			throw new ValidationException("The product supplier ID must be informed");
		}
		return productRepository
				.findBySupplierId(categoryId)
				.stream()
				.map(ProductResponse::of)
				.collect(Collectors.toList());
	}

	public List<ProductResponse> findAll() {
		return productRepository
				.findAll()
				.stream()
				.map(ProductResponse::of)
				.collect(Collectors.toList());
	}

	public Product findById(Integer id) {
		return productRepository
				.findById(id)
				.orElseThrow(() -> new ValidationException(("There is no Product for the given id")));
	}

	public ProductResponse save(ProductRequest request) {
		validateProductDataNotInformed(request);
		validateCategoryAndSupplierIdInformed(request);

		var category = categoryService.findById(request.getCategoryId());
		var supplier = supplierService.findById(request.getSupplierId());
		var product = productRepository.save(Product.of(request,category, supplier));
		return ProductResponse.of(product);
	}

	public ProductResponse update(ProductRequest request, Integer id) {
		validateProductDataNotInformed(request);
		validateCategoryAndSupplierIdInformed(request);
		validateInformedId(id);

		var category = categoryService.findById(request.getCategoryId());
		var supplier = supplierService.findById(request.getSupplierId());
		var product = Product.of(request,category, supplier);
		product.setId(id);
		product = productRepository.save(product);
		return ProductResponse.of(product);
	}

	public SuccessResponse delete(Integer id) {
		validateInformedId(id);
        productRepository.deleteById(id);
        return SuccessResponse.create("Product deleted successfully");
	}

	private void validateInformedId(Integer id) {
		if(isEmpty(id)) {
			throw new ValidationException("The Product id was not informed");
		}
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
