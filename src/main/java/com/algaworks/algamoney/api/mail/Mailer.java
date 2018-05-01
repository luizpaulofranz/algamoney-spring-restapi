package com.algaworks.algamoney.api.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.algamoney.api.model.Lancamento;
import com.algaworks.algamoney.api.model.Usuario;

@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender sender;
	@Autowired
	private TemplateEngine thymeleaf;

//  testando o envio
/*	
	@Autowired
	private LancamentoRepository repo;
	@EventListener
	private void teste(ApplicationReadyEvent event) {
		String template = "mail/aviso-lancamentos-vencidos";
		
		List<Lancamento> lista = repo.findAll();
		
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("lancamentos", lista);
		
		this.sendMail("testes.algaworks@gmail.com", 
				Arrays.asList("luizpaulofranz@gmail.com"), 
				"Testando", template, variaveis);
		System.out.println("Terminado o envio de e-mail...");
	}
*/
	/**
	 * Envia os emails de Lancamentos Vencidos
	 */
	public void avisarSobreLancamentosVencidos(
			List<Lancamento> vencidos, List<Usuario> destinatarios) {
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("lancamentos", vencidos);
		
		List<String> emails = destinatarios.stream()
				.map(u -> u.getEmail())
				.collect(Collectors.toList());
		
		this.sendMail("testes.algaworks@gmail.com", 
				emails, 
				"Lançamentos vencidos", 
				"mail/aviso-lancamentos-vencidos", 
				variaveis);
	}
	
	// alem dos dados basicos, passamos o caminho do template, e um mapa de 
	// parametros pra dentro do template, nesse caso "lancamentos" com uma lista de lancamentos
	public void sendMail (
			String remetente, 
			List<String> destinatarios, String assunto, String template, 
			Map<String, Object> variaveis
	){
		Context context = new Context(new Locale("pt", "BR"));
		// lambda para percorrer as variaveis
		variaveis.entrySet().forEach(
				e -> context.setVariable(e.getKey(), e.getValue()));
		
		String mensagem = thymeleaf.process(template, context);
		
		this.sendMail(remetente, destinatarios, assunto, mensagem);
	}
	
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
