package com.algaworks.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;


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
				.withRegion(Regions.AP_SOUTH_1)
				.build();
		// criamos o nosso bucket, caso nao exista ainda
		if(!amazonS3.doesBucketExistV2(apiConfig.getS3().getBucket())) {
			amazonS3.createBucket(new CreateBucketRequest(apiConfig.getS3().getBucket()));
			
			//isso tudo acrescenta uma regra simples
			//arquivos marcados com a tag "expirar" serao excluidos depois de 1 dia
			BucketLifecycleConfiguration.Rule regraExpiracao = 
					new BucketLifecycleConfiguration.Rule()
					.withId("Regra de expiração de arquivos temporários")
					.withFilter(new LifecycleFilter(
							new LifecycleTagPredicate(new Tag("expirar", "true"))))
					.withExpirationInDays(1)
					.withStatus(BucketLifecycleConfiguration.ENABLED);
			
			BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
					.withRules(regraExpiracao);
			
			amazonS3.setBucketLifecycleConfiguration(apiConfig.getS3().getBucket(), 
					configuration);
		}
		
		return amazonS3;
	}

}
