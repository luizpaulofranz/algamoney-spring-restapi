package com.algaworks.algamoney.api.config.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.algaworks.algamoney.api.security.UsuarioSistema;

/**
 * Essa classe implementa as regras para acrescentar informações ao payload do Token JWT.
 *
 */
public class TokenExtraInformation implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		//tivemos de criar um Wrapper para a classe User do spring security
		//que adiciona nossa model Usuario dentro dele
		UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
		
		//aqui criamos nossa carga adicional, que será passada como Json dentro do token
		Map<String, Object> addInfo = new HashMap<>();
		//acrescentamos o campo extra "nome" ao payload
		addInfo.put("nome", usuarioSistema.getUsuario().getNome());
		//adicionamos isso ao token e o retornamos
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addInfo);
		return accessToken;
	}

}