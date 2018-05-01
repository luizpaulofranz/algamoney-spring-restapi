package com.algaworks.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


@Configuration
public class S3Config {
	
	@Autowired
	private AlgamoneyApiConfig apiConfig;
	
	@Bean
	public AmazonS3 amazonS3() {
		// pegamos as credenciais de acesso da Amazon por meio das properties
		AWSCredentials credenciais = new BasicAWSCredentials(
				apiConfig.getS3().getAccessKeyId(), apiConfig.getS3().getSecretAccessKey());
		// precisamos das credenciais Amazon para retornar seus servicos 
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credenciais))
				.build();
		
		return amazonS3;
	}

}
