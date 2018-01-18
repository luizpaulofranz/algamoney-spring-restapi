package com.algaworks.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.algaworks.algamoney.api.config.token.TokenExtraInformation;

@Configuration
@EnableAuthorizationServer
//essa classe eh responsavel pelas autenticacoes dos APLICATIVOS CLIENTES e não dos usuarios 
//para criar um token, o spring cria um resource /oauth/token
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient("angular")
			.secret("@ngul@r0")
			.scopes("read", "write")//esse escopo pode conter qlq array de strings, 
			//eh usado para controle de nivel de acesso aos metodos
			.authorizedGrantTypes("password", "refresh_token")//passwordflow do oauth, sao os fluxos do protocolo oauth
			.accessTokenValiditySeconds(1800)//tempo de vida do token, 30 mins
			.refreshTokenValiditySeconds(3600 * 24)//poremos dar refresh nesse token por ateh um dia
		.and()
			//outro aplicativo cliente, com escopo mais limitado
			.withClient("mobile")
			.secret("m0b1l30")
			.scopes("read")//esse tem escopo apenas de leitura
			.authorizedGrantTypes("password", "refresh_token")//passwordflow do oauth, sao os fluxos do protocolo oauth
			.accessTokenValiditySeconds(1800)//tempo de vida do token, 30 mins
			.refreshTokenValiditySeconds(3600 * 24);//poremos dar refresh nesse token por ateh um dia
		
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//com isso podemos manipular o payload do token
		//acrescentando informações adicionais
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		//aqui passamos uma lista de informações que devem estar no payload
		//o ultimo deve ser o accessTokenConverter, pois é o responsável por encodar o payload
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore())
			//como estamos passando informacoes customizadas ao token, usamos o objeto Chain
			//.accessTokenConverter(accessTokenConverter())
			.tokenEnhancer(tokenEnhancerChain)
			.reuseRefreshTokens(false)//quando da um refresh, um novo token eh criado
			.authenticationManager(authenticationManager);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks");//senha da criptografia do token
		return accessTokenConverter;
	}

	@Bean
	//para armazenar o token
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}	
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
	    return new TokenExtraInformation();
	}

}
