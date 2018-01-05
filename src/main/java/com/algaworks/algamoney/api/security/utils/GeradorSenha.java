package com.algaworks.algamoney.api.security.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//serve so pra gerar senhas encodadas
public class GeradorSenha {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		//encoda a senha "admin"
		System.out.println(encoder.encode("admin"));
	}
	
}