package com.algaworks.algamoney.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	// classe para manipulacao de mensagens personalizaveis
	// resources/messages.properties
	private MessageSource messageSource;

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		// pegamos essa propriedade do arquivo resources/messages.properties
		String message = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		String log = ex.getCause() != null ? ex.getCause().toString(): ex.toString();
		// criamos uma lista de erros apenas para padronizar a chamada
		List<Erro> erros = Arrays.asList(new Erro(message, log));
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	@Override
	// excecao lancada pelo Spring quando tenta validar as @Entity com o @Valid
	// nos resources
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler({ DataIntegrityViolationException.class } )
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
		
		String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
		//ExceptionUtils retorna a excecao raiz, retornando qual foi a violacao de integridade que ocorreu
		String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));

		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	// trata excecoes ao deletar um recurso inexistente, retorna um 404
	@ExceptionHandler({EmptyResultDataAccessException.class})
	protected ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request){
		String message = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		String log = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(message, log));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	// BindingResult contem todos os erros
	private List<Erro> criarListaDeErros(BindingResult bindingResult) {
		List<Erro> erros = new ArrayList<>();

		// BindingResult contem todos os erros por campo
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String log = fieldError.toString();
			erros.add(new Erro(message, log));
		}

		return erros;
	}

	/*
	 * Classe para gerar um Json com uma mensagem amigavel de erro e depois um
	 * log para os Devs
	 */
	public static class Erro {

		private String message;
		private String log;

		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.message = mensagemUsuario;
			this.log = mensagemDesenvolvedor;
		}

		public String getMessage() {
			return message;
		}

		public String getLog() {
			return log;
		}

	}
}
