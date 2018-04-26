package com.algaworks.algamoney.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Essa classe permite configurarmos a aplicacao via arquivos properties, assim
 * criamos nossas proprias propriedades de configuração.
 * 
 * Para funcionar, precisamos adicionar a anotação @
 * EnableConfigurationProperties no arquivo de bootstrap da aplicação:
 * AlgamoneyApiApplication
 * 
 * O Arquivo que usamos essas configurações application-pro.properties. As
 * propriedades definidas nessa classe estão acessíveis nos arquivos de
 * configuração, e podem ser utilizados na aplicação com os valores setados nos
 * arquivos properties.
 * 
 * Um exemplo de utilizacao dessa configuracao esta no resource TokenResource
 */

// anotação necessaria para habilitar a classe como uma classe de configuração
// algamoney é o nome dessas propriedades nos aquivos properties
@ConfigurationProperties("algamoney")
@Component
public class AlgamoneyApiConfig {

	// configuração da URL atual do site
	private String originPermitida = "http://localhost:8000";

	// para tornar a classe Seguranca acessivel nos arquivos properties
	private Seguranca seguranca = new Seguranca();
	private Mail mail= new Mail();

	public Seguranca getSeguranca() {
		return seguranca;
	}
	
	public Mail getMail() {
		return mail;
	}

	public void setSeguranca(Seguranca s) {
		this.seguranca = s;
	}

	public String getOriginPermitida() {
		return originPermitida;
	}

	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}

	/**
	 * Essa subclasse serve para podermos segmentar as configurações nos
	 * arquivos, assim podemos chamar nas configurações:
	 * algamoney.seguranca.enableHttps.
	 *
	 * O que faz o segurança ser acessivel, é a propriedade seguranca da classe
	 * wrapper.
	 */
	public static class Seguranca {

		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}

	}

	public static class Mail {
		private String host;
		private Integer port;
		private String username;
		private String password;

		public void setHost(String host) {
			this.host = host;
		}
		
		public String getHost() {
			return this.host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

}
