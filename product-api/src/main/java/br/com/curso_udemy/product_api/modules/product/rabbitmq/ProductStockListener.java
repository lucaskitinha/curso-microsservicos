package br.com.curso_udemy.product_api.modules.product.rabbitmq;

import br.com.curso_udemy.product_api.modules.product.dto.ProductStockDTO;
import br.com.curso_udemy.product_api.modules.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductStockListener {

	@Autowired
	private ProductService productService;

	@RabbitListener(queues = "${app-config.rabbit.queue.product-stock}")
	public void recieveProductStockMessage(ProductStockDTO productStockDTO) throws JsonProcessingException {
		log.info("Recieving Message: {}", new ObjectMapper().writeValueAsString(productStockDTO));
		productService.updateProductStock(productStockDTO);
	}
}
