package com.algaworks.algamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.algaworks.algamoney.api.config.AlgamoneyApiConfig;

//indica que e um componente spring
@Component
//essa regra tem precedencia maior sobre as outras
@Order(Ordered.HIGHEST_PRECEDENCE)
/**
 * Essa classe intercepta as requisicoes, e adiciona os Headers HTTP necessarios para habilitar o CORS.
 * 
 * Esta implementada para liberar globalmente a aplicacao ao CORS. a unica URL que deu trabalho
 * foi a do token, pois a politica Same Origin Policy, cria uma requiscao extra, que era prontamente negada pelo servidor. 
 *
 */
public class CorsFilter implements Filter {
	
	//pegamos nosso arquivo de configuracao
	@Autowired
	private AlgamoneyApiConfig config;
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		//pegamos uma propriedade definida em nosso arquivo de configuracao
		String originPermitida = config.getOriginPermitida();
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		//esses Headers sao globais para toda e qualquer requisicao
		response.setHeader("Access-Control-Allow-Origin", originPermitida);
        response.setHeader("Access-Control-Allow-Credentials", "true");
		
        //aqui filtramos a requisicao extra gerada pela Same Origin Policy, e retornamos um response
		if ("OPTIONS".equals(request.getMethod()) && originPermitida.equals(request.getHeader("Origin"))) {
			//adicionamos os headers necessarios para a liberacao do CORS
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
        	response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
        	//essa autorizacao de CORS sera valida por uma hora, ou seja, o navegador nao vai enviar um request OPTIONS
        	//para o servidor durante uma hora
        	response.setHeader("Access-Control-Max-Age", "3600");
			//retornamos um response OK, autorizando o CORS
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			//caso contrario, o request segue o fluxo normal
			chain.doFilter(req, resp);
		}
		
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
