package br.com.curso_udemy.product_api.config;

import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {

	public static HttpServletRequest getCurrentRequest() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes())
					.getRequest();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ValidationException("The current request could not be proccessed.");
		}
	}
}
