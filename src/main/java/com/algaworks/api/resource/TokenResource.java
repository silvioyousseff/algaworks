package com.algaworks.api.resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.api.config.property.AlgaworksApiProperty;

@RestController
@RequestMapping("/tokens")
public class TokenResource {

	@Autowired
	private AlgaworksApiProperty algaworksApiProperty;
	
	@DeleteMapping("/revoke")
	public void revoke(HttpServletRequest rq, HttpServletResponse rs) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", null);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(algaworksApiProperty.getSeguranca().isEnableHttps());
		refreshTokenCookie.setPath(rq.getContextPath() + "/oauth/token");
		refreshTokenCookie.setMaxAge(0);
		
		rs.addCookie(refreshTokenCookie);
		rs.setStatus(HttpStatus.NO_CONTENT.value());
	}
}
