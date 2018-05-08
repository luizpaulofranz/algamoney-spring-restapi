package com.algaworks.algamoney.api.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.algamoney.api.config.AlgamoneyApiConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;

@Component
public class S3 {
	
	private static final Logger logger = LoggerFactory.getLogger(S3.class);
	
	@Autowired
	private AlgamoneyApiConfig property;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	public String salvarTemporariamente(MultipartFile arquivo) {
		// permissoes que esse arquivo tem
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		// meta dados do arquivo
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(arquivo.getContentType());
		meta.setContentLength(arquivo.getSize());
		// nome de cada arquivo deve ser unico
		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());
		
		try {
			// classe que envia o arquivo ao S3
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					property.getS3().getBucket(),
					nomeUnico,
					arquivo.getInputStream(), 
					meta)
					.withAccessControlList(acl);
			// acrescentamos a tag, indicando que eh um arquivo temporario
			putObjectRequest.setTagging(new ObjectTagging(
					Arrays.asList(new Tag("expirar", "true"))));
			
			amazonS3.putObject(putObjectRequest);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Arquivo {} enviado com sucesso para o S3.", 
						arquivo.getOriginalFilename());
			}
			
			return nomeUnico;
		} catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", e);
		}
	}
	
	/*Esse metodo deve ser chamado depois do acima, ele apenas remove a tag "expirar"*/
	public void salvar(String objeto) {
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
				property.getS3().getBucket(), 
				objeto, 
				new ObjectTagging(Collections.emptyList()));
		
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}
	
	// deleta um arquivo do S3, lembrando q cada arquivo tem um nome unico
	public void remover(String anexo) {
		DeleteObjectRequest deleteRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), 
				anexo
			);
		amazonS3.deleteObject(deleteRequest);
		
	}
	
	public void substituir(String anexoAntigo, String anexoNovo) {
		if(StringUtils.hasText(anexoAntigo)) {
			this.remover(anexoAntigo);
		}
		this.salvar(anexoNovo);		
	}
	
	public String configurarUrl(String objeto) {
		return "\\\\" + property.getS3().getBucket() +
				".s3.amazonaws.com/" + objeto;
	}

	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}


}