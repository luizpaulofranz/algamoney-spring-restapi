package com.algaworks.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

//indica que e uma classe de configuracao
@Configuration
// habilita a seguranca na API
@EnableWebSecurity
@EnableResourceServer
// essa anotacao permite o controle de acesso dos usuarios aos resources da API
//esse controle de acesso se da via metodo, por isso o Method no nome da classe
//podemos usar outras opcoes, como @EnableWebSecurity por exemplo
@EnableGlobalMethodSecurity(prePostEnabled = true)
// essa classe controla apenas os acessos aos recursos, dependendo da sua conexao
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		// para usuario e senha fixo
		// auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ROLE");

		// esse obj userDetailsService esta implementado dentro do pacote security
		// nele esta codificado a regra de autenticacao, e eh carregado as permissoes
		// o passwordEncoder eh para lidar com senhas criptografadas, tem um
		// exemplo no pacote security/utils
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// .antMatchers("/categorias").permitAll() 
				//para permitir acesso sem autenticacao
				.anyRequest().authenticated().and()
				// .httpBasic().and()//metodo de autenticacao Basic
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// desabilita toda sessao
				.and().csrf().disable();
	}

	@Override
	// pra tornar stateless
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}

	@Bean
	//o @Bean informa a injecao de dependencias do spring
	//Informando o tipo esperado e o tipo retornado
	//agora podemos usar o @AutoWired para a classe MethodSecurityExpressionHandler
	//e estara disponivel em toda a aplicacao.
	//o SpringSecurity exige isso para controle de nivel de acesso
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}

}
