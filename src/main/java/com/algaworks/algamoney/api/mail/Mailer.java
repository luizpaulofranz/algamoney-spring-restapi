package com.algaworks.algamoney.api.mail;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender sender;

//  testando o envio	
//  ApplicationReadyEvent eh um evento padrao do Spring, em conjunto com a anotacao @EventListner 
//	podemos criar hooks em pontos do ciclo de vida do spring
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//    	this.sendMail("testehue@gmail.com", 
//				Arrays.asList("luizpaulofranz@gmail.com"), 
//				"Testando", "Olá!<br/>Teste ok.");
//		System.out.println("Terminado o envio de e-mail...");
//	}
	
	public void sendMail(String remetente, List<String> destinatarios, String assunto, String msg) {
		try {
			MimeMessage mimeMessage = sender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(msg, true);//true indica q eh HTML
			
			sender.send(mimeMessage);
		} catch (MessagingException e) {
			// apenas relancamos a escecao
			throw new RuntimeException("Problemas com o envio de e-mail!", e); 
		}
	}

}
