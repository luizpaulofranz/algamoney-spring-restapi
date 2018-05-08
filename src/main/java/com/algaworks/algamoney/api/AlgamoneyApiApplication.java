package com.algaworks.algamoney.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
//habilita nossas configuracoes personalizadas
@EnableConfigurationProperties
public class AlgamoneyApiApplication {
	// fazemos isso para podermos usar o spring em classes nao spring
	// APPLICATTION_CONTEXT contem a instancia do proprio spring
	private static 	ApplicationContext APPLICATTION_CONTEXT;

	public static void main(String[] args) {
		APPLICATTION_CONTEXT = SpringApplication.run(AlgamoneyApiApplication.class, args);
	}
	// assim podemos usar a injecao de dependencias do spring e classes 
	// externas ao franework, como em repository.listener
	public static <T> T getBean(Class<T> type) {
		return APPLICATTION_CONTEXT.getBean(type);
	}  
}
