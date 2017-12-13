package com.algaworks.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.algaworks.api.config.property.AlgaworksApiProperty;

@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	@Autowired
	private AlgaworksApiProperty algaworksApiProperty;
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> convertType) {
		return returnType.getMethod().getName().equals("postAccessToken");
	}
	
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		HttpServletRequest rq = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse rs = ((ServletServerHttpResponse) response).getServletResponse();
		String refreshToken = body.getRefreshToken().getValue();
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		adicionarRefreshTokenCookie(refreshToken, rq, rs);
		removerRefreshTokenBody(token);
		
		return body;
	}

	private void adicionarRefreshTokenCookie(String refreshToken, HttpServletRequest rq, HttpServletResponse rs) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(algaworksApiProperty.getSeguranca().isEnableHttps());
		refreshTokenCookie.setPath(rq.getContextPath() + "/oauth/token");
		refreshTokenCookie.setMaxAge(2592000);
		rs.addCookie(refreshTokenCookie);
	}
	
	private void removerRefreshTokenBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}
}
