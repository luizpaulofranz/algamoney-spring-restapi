package com.algaworks.algamoney.api.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Essa classe permite configurarmos a aplicacao via arquivos properties, assim criamos nossas 
 * proprias propriedades de configuração.
 * 
 * Para funcionar, precisamos adicionar a anotação @ EnableConfigurationProperties no 
 * arquivo de bootstrap da aplicação: AlgamoneyApiApplication
 * 
 * O Arquivo que usamos essas configurações application-pro.properties.
 * As propriedades definidas nessa classe estão acessíveis nos arquivos de configuração, 
 * e podem ser utilizados na aplicação com os valores setados nos arquivos properties.
 * 
 * Um exemplo de utilizacao dessa configuracao esta no resource TokenResource
 */ 

//anotação necessaria para habilitar a classe como uma classe de configuração
//algamoney é o nome dessas propriedades nos aquivos properties
@ConfigurationProperties("algamoney")
@Component
public class AlgamoneyApiConfig {

	//configuração da URL atual do site
	private String originPermitida = "http://localhost:8000";

	//para tornar a classe Seguranca acessivel nos arquivos properties
	private Seguranca seguranca = new Seguranca();

	public Seguranca getSeguranca() {
		return seguranca;
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
	 *	Essa subclasse serve para podermos segmentar as configurações nos arquivos, assim podemos chamar
	 *	nas configurações: algamoney.seguranca.enableHttps.
	 *
	 *	O que faz o segurança ser acessivel, é a propriedade seguranca da classe wrapper.
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
	
}
