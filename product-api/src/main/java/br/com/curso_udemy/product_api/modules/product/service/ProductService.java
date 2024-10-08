package br.com.curso_udemy.product_api.modules.product.service;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.modules.product.dto.ProductCheckStockRequest;
import br.com.curso_udemy.product_api.modules.product.dto.ProductSalesResponse;
import br.com.curso_udemy.product_api.modules.sales.client.SalesClient;
import br.com.curso_udemy.product_api.modules.sales.dto.SalesConfirmationDTO;
import br.com.curso_udemy.product_api.modules.sales.dto.SalesProductResponse;
import br.com.curso_udemy.product_api.modules.sales.enums.SalesStatus;
import br.com.curso_udemy.product_api.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.curso_udemy.product_api.modules.category.service.CategoryService;
import br.com.curso_udemy.product_api.modules.product.dto.ProductQuantityDTO;
import br.com.curso_udemy.product_api.modules.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.modules.product.dto.ProductResponse;
import br.com.curso_udemy.product_api.modules.product.dto.ProductStockDTO;
import br.com.curso_udemy.product_api.modules.product.model.Product;
import br.com.curso_udemy.product_api.modules.product.repository.ProductRepository;
import br.com.curso_udemy.product_api.modules.supplier.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.curso_udemy.product_api.config.RequestUtil.getCurrentRequest;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Lazy
	private CategoryService categoryService;
	@Lazy
	private SupplierService supplierService;
	@Autowired
	private SalesConfirmationSender salesConfirmationSender;
	@Autowired
	private SalesClient salesClient;

	private static final Integer ZERO = 0;
	private static final String TRANSACTION_ID = "transactionid";
	private static final String SERVICE_ID = "serviceid";

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
		if(!productRepository.existsById(id)) {
			throw new ValidationException("The Product does not exists");
		}
		var sales = getSalesByProductId(id);
		if(!isEmpty(sales.getSalesId())) {
			throw new ValidationException("The Product cannot be deleted. There are sales for it. ");
		}
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

	public void updateProductStock(ProductStockDTO productStockDTO) {
		try {
			validateStockUpdateData(productStockDTO);
			updateStock(productStockDTO);
		} catch (Exception e) {
			log.error("Error while trying to update stock for message with error: {}", e.getMessage(), e);
			salesConfirmationSender
					.sendSalesConfirmationMessage(
							new SalesConfirmationDTO(productStockDTO.getSalesId(), SalesStatus.REJECTED,
									productStockDTO.getTransactionid())
					);
		}
	}

	@Transactional
	private void validateStockUpdateData(ProductStockDTO productStockDTO) {
		if(isEmpty(productStockDTO) || isEmpty(productStockDTO.getSalesId())) {
			throw new ValidationException("The product data an the sales ID must be informed");
		}
		if(isEmpty(productStockDTO.getProducts())) {
			throw new ValidationException("The sales' products must be informed");
		}

		productStockDTO.getProducts()
				.forEach(product -> {
					if(isEmpty(product.getQuantity()) || isEmpty(product.getProductId())) {
						throw new ValidationException("The product's ID and quantity must be informed");
					}
				});
	}

	private void updateStock(ProductStockDTO productStockDTO) {
		var productsForUpdate = new ArrayList<Product>();
		productStockDTO.getProducts()
				.forEach(salesProduct -> {
					var existingProduct = findById(salesProduct.getProductId());
					validateQuantityInStock(salesProduct, existingProduct);
					existingProduct.updateStock(salesProduct.getQuantity());
					productsForUpdate.add(existingProduct);
				});
		if(!isEmpty(productsForUpdate)) {
			productRepository.saveAll(productsForUpdate);
			var approvedMessage = new SalesConfirmationDTO(productStockDTO.getSalesId(), SalesStatus.APPROVED,
					productStockDTO.getTransactionid());
			salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
		}

	}

	private void validateQuantityInStock(ProductQuantityDTO salesProduct, Product existingProduct) {
		if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
			throw new ValidationException(
					String.format("The product %s is out of stock", existingProduct.getId()));
		}
	}

	public ProductSalesResponse findProductSales(Integer id) {
		var product = findById(id);

		try {
			var sales = getSalesByProductId(product.getId());
			return ProductSalesResponse.of(product, sales.getSalesId());

		} catch (Exception e) {
			e.printStackTrace();
			throw new ValidationException("There was an error trying to get the product's sales");
		}
	}

	private SalesProductResponse getSalesByProductId(Integer id) {
		try {
			var currentRequest = getCurrentRequest();
			var transactionid = currentRequest.getHeader(TRANSACTION_ID);
			var serviceid = currentRequest.getAttribute(SERVICE_ID);
			log.info("Sending GET request to orders by productid with data {} | [transactionId: {} | serviceId: {}]",
					id, transactionid, serviceid);
			var response = salesClient
					.findSalesByProductId(id)
					.orElseThrow(() -> new ValidationException("The sales was not found by this product"));
			log.info("Sending GET request to orders by productid with data {} | [transactionId: {} | serviceId: {}]",
					new ObjectMapper().writeValueAsString(response), transactionid, serviceid);
			return response;
		} catch (Exception e) {
			throw new ValidationException("The sales could not be found");
		}
	}

	public SuccessResponse checkProductsStock(ProductCheckStockRequest request) {
		try {
			var currentRequest = getCurrentRequest();
			var transactionid = currentRequest.getHeader(TRANSACTION_ID);
			var serviceid = currentRequest.getAttribute(SERVICE_ID);
			log.info("Request to POST product stock with data {} | [transactionId: {} | serviceId: {}]",
					new ObjectMapper().writeValueAsString(request), transactionid, serviceid);
			if (isEmpty(request)) {
				throw new ValidationException("The request data and products must be informed");
			}
			request.getProducts()
					.forEach(this::validateStock);
			var response = SuccessResponse.create("The stock is ok");

			log.info("Response to POST product stock with data {} | [transactionId: {} | serviceId: {}]",
					new ObjectMapper().writeValueAsString(response), transactionid, serviceid);

			return response;
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

	}

	private void validateStock(ProductQuantityDTO productQuantity) {
		if(isEmpty(productQuantity.getProductId()) || isEmpty(productQuantity.getQuantity())) {
			throw new ValidationException("Product ID and quantity must be informed");
		}
		var product = findById(productQuantity.getProductId());
		if(productQuantity.getQuantity() > product.getQuantityAvailable()) {
			throw new ValidationException(String.format("The product %s is out of stock", product.getId()));
		}
	}

}
