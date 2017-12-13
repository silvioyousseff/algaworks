package com.algaworks.api.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenPreProcessor implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest rq = (HttpServletRequest) request;

		if ("/oauth/token".equalsIgnoreCase(rq.getRequestURI()) && "refresh_token".equals(rq.getParameter("grant_type"))
				&& rq.getCookies() != null) {

			for (Cookie cookie : rq.getCookies()) {
				if (cookie.getName().equals("refreshToken")) {
					rq = new MyServletRequestWrapper(rq, cookie.getValue());
				}
			}
		}
		
		chain.doFilter(rq, response);
	}

	static class MyServletRequestWrapper extends HttpServletRequestWrapper {

		public String refreshToken;

		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			this.refreshToken = refreshToken;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			map.put("refresh_token", new String[] { refreshToken });
			map.setLocked(true);
			return map;
		}
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}
}
