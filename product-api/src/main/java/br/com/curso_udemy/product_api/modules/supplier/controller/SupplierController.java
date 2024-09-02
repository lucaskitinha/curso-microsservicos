package br.com.curso_udemy.product_api.modules.supplier.controller;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierRequest;
import br.com.curso_udemy.product_api.modules.supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

	@Autowired
	private SupplierService supplierService;

	@PostMapping
	public SupplierResponse save(@RequestBody SupplierRequest request) {
		return supplierService.save(request);
	}

	@GetMapping
	public List<SupplierResponse> findAll() {
		return supplierService.findAll();
	}

	@GetMapping("/{id}")
	public SupplierResponse findById(@PathVariable Integer id) {
		return supplierService.findByIdResponse(id);
	}

	@GetMapping("name/{name}")
	public List<SupplierResponse> findByName(@PathVariable String name) {
		return supplierService.findByName(name);
	}

	@DeleteMapping("/{id}")
	public SuccessResponse delete(@PathVariable Integer id) {
		return supplierService.delete(id);
	}
}
