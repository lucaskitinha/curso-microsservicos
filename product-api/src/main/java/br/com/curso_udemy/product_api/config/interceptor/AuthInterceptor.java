package br.com.curso_udemy.product_api.config.interceptor;

import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.modules.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

	private static final String AUTHORIZATION = "Authorization";
	private static final String TRANSACTION_ID = "transactionid";

	private final JwtService jwtService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response,
			Object handler) {
		if (isOptions(request) || isPublicUrl(request.getRequestURI())) {
			return true;
		}
		if (isEmpty(request.getHeader(TRANSACTION_ID))) {
			throw new ValidationException("The transactionid header is required.");
		}
		var authorization = request.getHeader(AUTHORIZATION);
		jwtService.validateAuthorization(authorization);
		request.setAttribute("serviceid", UUID.randomUUID().toString());
		return true;
	}

	private boolean isPublicUrl(String url) {
		return Urls.PROTECTED_URLS
				.stream()
				.noneMatch(url::contains);
	}

	private boolean isOptions(HttpServletRequest request) {
		return HttpMethod.OPTIONS.name().equals(request.getMethod());
	}
}
