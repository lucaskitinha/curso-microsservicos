package br.com.curso_udemy.product_api.modules.supplier.service;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.modules.category.dto.CategoryResponse;
import br.com.curso_udemy.product_api.modules.product.service.ProductService;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierRequest;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.modules.supplier.model.Supplier;
import br.com.curso_udemy.product_api.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {

	@Autowired
	private SupplierRepository supplierRepository;
	@Autowired
	private ProductService productService;

	public SupplierResponse findByIdResponse(Integer id) {
		validateInformedId(id);

		return SupplierResponse.of(findById(id));
	}

	public List<SupplierResponse> findByName(String name) {
		if(isEmpty(name)) {
			throw new ValidationException("The supplier name must be informed");
		}
		return supplierRepository
				.findByNameIgnoreCaseContaining(name)
				.stream()
				.map(SupplierResponse::of)
				.collect(Collectors.toList());
	}

	public List<SupplierResponse> findAll() {
		return supplierRepository
				.findAll()
				.stream()
				.map(SupplierResponse::of)
				.collect(Collectors.toList());
	}

	public Supplier findById(Integer id) {
		return supplierRepository
				.findById(id)
				.orElseThrow(() -> new ValidationException(("There is no Supplier for the given id")));
	}

	public SupplierResponse save(SupplierRequest supplierRequest) {
		validateSupplierNameInformed(supplierRequest);
		Supplier supplier = supplierRepository.save(Supplier.of(supplierRequest));
		return SupplierResponse.of(supplier);
	}

	public SupplierResponse update(SupplierRequest supplierRequest, Integer id) {
		validateSupplierNameInformed(supplierRequest);
		validateInformedId(id);
		var supplier  = Supplier.of(supplierRequest);
		supplier.setId(id);
		supplierRepository.save(supplier);
		return SupplierResponse.of(supplier);
	}

	public SuccessResponse delete(Integer id) {
		validateInformedId(id);
		if(productService.existsBySupplierId(id)) {
			throw new ValidationException("Cannot delete supplier with associated products");
		}
		supplierRepository.deleteById(id);
		return SuccessResponse.create("Supplier deleted successfully");
	}

	private void validateInformedId(Integer id) {
		if(isEmpty(id)) {
			throw new ValidationException("The supplier id was not informed");
		}
	}

	private void validateSupplierNameInformed(SupplierRequest supplierRequest) {
		if(isEmpty(supplierRequest.getName())) {
			throw new ValidationException("The category description was not informed");
		}
	}
}
