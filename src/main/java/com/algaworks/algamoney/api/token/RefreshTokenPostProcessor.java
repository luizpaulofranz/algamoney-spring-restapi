package com.algaworks.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

/**
 * Essa classe eh responsavel por remover o refresh_token do body do response, pois as boas praticas recomendam que a aplicacao
 * cliente nao deve ter acesso direto a esse refresh_token, portanto essa classe o salva em um cookie e remove do response.
 * 
 * O QUE ISSO SIGNIFICA? Significa que a aplicacao cliente vai ter o refresh_token salvo em um cookie.
 * 
 * Esse implements ResponseBodyAdvice intercepta responses de requisicoes. 
 *
 */
@ControllerAdvice
//esse implements intercepta responses de requisicoes onde o body eh OAuthAccessToken
//OAuthAccessToken eh o retorno das requisicoes para criacao e refres de tokens
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	@Override
	//esse metodo eh quem filtra qual das requisicoes deve ser interceptada pelo metodo de baixo
	//pois varias requisicoes diferentes podem retornar esse tipo de objeto
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		//postAccessToken como o nome sugere, quer dizer depois de gerado o access token
		//quando um novo token eh gerado, junto com ele eh criado o refresh_token, portanto eh nesse caso que devemos atuar
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	@Override
	//e esse eh o metodo que remove o refresh_token do response, e o salva em um cookie
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		//esses objetos sao necessarios para criar o cookie
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		
		//esse aqui so eh usado para remover o refresh_token
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		String refreshToken = body.getRefreshToken().getValue();
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		removerRefreshTokenDoBody(token);
		
		return body;
	}

	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(false); // TODO: Mudar para true em producao
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");
		refreshTokenCookie.setMaxAge(2592000);//tempo de vida 30 dias
		resp.addCookie(refreshTokenCookie);
	}

}


