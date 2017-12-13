package com.algaworks.api.exceptionhandler;

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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.algaworks.api.service.exception.PessoaInativaException;
import com.algaworks.api.service.exception.PessoaNullException;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	MessageSource messageSource;

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<Erro> listaErros = Arrays
				.asList(new Erro(messageSource.getMessage("mensagem.invalido", null, LocaleContextHolder.getLocale()),
						ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));

		return handleExceptionInternal(ex, listaErros, headers, HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		return handleExceptionInternal(ex, criarListaErros(ex.getBindingResult()), headers, HttpStatus.BAD_REQUEST,
				request);
	}

	//salvar lancamento com id da pessoa errado
	@org.springframework.web.bind.annotation.ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request) {

		List<Erro> listaErros = Arrays.asList(
				new Erro(messageSource.getMessage("recurso.operacao.invalida", null, LocaleContextHolder.getLocale()),
						ExceptionUtils.getRootCauseMessage(ex)));

		return handleExceptionInternal(ex, listaErros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	//excluir pessoa que nao existe
	@org.springframework.web.bind.annotation.ExceptionHandler({ EmptyResultDataAccessException.class })
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
			WebRequest request) {

		List<Erro> listaErros = Arrays.asList(
				new Erro(messageSource.getMessage("recurso.nao.encontrado", null, LocaleContextHolder.getLocale()),
						ExceptionUtils.getRootCauseMessage(ex)));

		return handleExceptionInternal(ex, listaErros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@org.springframework.web.bind.annotation.ExceptionHandler({ PessoaNullException.class })
	public ResponseEntity<Object> handlePessoaNullException(PessoaNullException ex,
			WebRequest request) {

		List<Erro> listaErros = Arrays.asList(
				new Erro(messageSource.getMessage("recurso.pessoa.nao.encontrada", null, LocaleContextHolder.getLocale()),
						ExceptionUtils.getRootCauseMessage(ex)));

		return handleExceptionInternal(ex, listaErros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@org.springframework.web.bind.annotation.ExceptionHandler({ PessoaInativaException.class })
	public ResponseEntity<Object> handlePessoaInativaException(PessoaInativaException ex,
			WebRequest request) {

		List<Erro> listaErros = Arrays.asList(
				new Erro(messageSource.getMessage("recurso.pessoa.inativa", null, LocaleContextHolder.getLocale()),
						ExceptionUtils.getRootCauseMessage(ex)));

		return handleExceptionInternal(ex, listaErros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	public List<Erro> criarListaErros(BindingResult bindingResult) {
		List<Erro> listaErros = new ArrayList<Erro>();

		for (FieldError erro : bindingResult.getFieldErrors()) {
			listaErros.add(new Erro(messageSource.getMessage(erro, LocaleContextHolder.getLocale()), erro.toString()));
		}

		return listaErros;
	}

	public static class Erro {
		public String erroUsuario;
		public String erroDesenvolvedor;

		public Erro(String erroUsuario, String erroDesenvolvedor) {
			this.erroUsuario = erroUsuario;
			this.erroDesenvolvedor = erroDesenvolvedor;
		}

		public String getErroUsuario() {
			return erroUsuario;
		}

		public void setErroUsuario(String erroUsuario) {
			this.erroUsuario = erroUsuario;
		}

		public String getErroDesenvolvedor() {
			return erroDesenvolvedor;
		}

		public void setErroDesenvolvedor(String erroDesenvolvedor) {
			this.erroDesenvolvedor = erroDesenvolvedor;
		}
	}
}
