package com.algaworks.algamoney.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

//indica que e uma classe de configuracao
@Configuration
//habilita a seguranca na API
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
		.withUser("admin").password("admin").roles("ROLE");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			//.antMatchers("/categorias").permitAll() //para permitir acesso sem autenticacao
			.anyRequest().authenticated()
			.and()
			.httpBasic()//metodo de autenticacao
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//desabilita toda sessao
			.and()
			.csrf().disable();
	}

}
