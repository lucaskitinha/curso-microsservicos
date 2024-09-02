package br.com.curso_udemy.product_api.modules.supplier.service;

import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierRequest;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.modules.supplier.model.Supplier;
import br.com.curso_udemy.product_api.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {

	@Autowired
	private SupplierRepository supplierRepository;

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

	private void validateSupplierNameInformed(SupplierRequest supplierRequest) {
		if(isEmpty(supplierRequest.getName())) {
			throw new ValidationException("The category description was not informed");
		}
	}
}
