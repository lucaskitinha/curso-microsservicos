package br.com.curso_udemy.product_api.modules.supplier.controller;

import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierRequest;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

	@Autowired
	private SupplierService supplierService;

	@PostMapping
	public SupplierResponse save(@RequestBody SupplierRequest request) {
		return supplierService.save(request);
	}
}
