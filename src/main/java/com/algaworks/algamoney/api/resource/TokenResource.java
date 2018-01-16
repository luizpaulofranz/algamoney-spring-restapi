package com.algaworks.algamoney.api.resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algamoney.api.config.AlgamoneyApiConfig;

@RestController
@RequestMapping("/tokens")
//esse resource eh para fazer o logout
//ele apenas apaga o refreshToken, o AccessToken fica por
//conta do app cliente apagar, ou deixar q ele expire
public class TokenResource {
	
	//adicionamos nossa propria configuracao, ver classe AlgamoneyApiConfig
	@Autowired
	private AlgamoneyApiConfig config;

	@DeleteMapping("/revoke")
	//na URL tokens/revoke, deletamos o refreshToken dos cookies
	public void revoke(HttpServletRequest req, HttpServletResponse resp) {
		Cookie cookie = new Cookie("refreshToken", null);
		cookie.setHttpOnly(true);
		//aqui usamos propriedades que configuramos em arquivos properties
		cookie.setSecure(config.getSeguranca().isEnableHttps());
		cookie.setPath(req.getContextPath() + "/oauth/token");
		cookie.setMaxAge(0);
		//retornamos 204 No Content
		resp.addCookie(cookie);
		resp.setStatus(HttpStatus.NO_CONTENT.value());
	}
	
}
