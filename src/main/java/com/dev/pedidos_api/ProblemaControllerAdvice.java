package com.dev.pedidos_api;

import java.net.URI;
import java.util.LinkedHashSet;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class ProblemaControllerAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ProblemDetail> requisicaoInvalida(MethodArgumentNotValidException ex, HttpServletRequest request) {
		var mensagens = new LinkedHashSet<String>();
		for (var erro : ex.getBindingResult().getAllErrors()) {
			if (erro.getDefaultMessage() != null && !erro.getDefaultMessage().isBlank()) {
				mensagens.add(erro.getDefaultMessage());
			}
		}
		var detalhe = mensagens.isEmpty() ? "A requisição contém dados inválidos" : String.join(". ", mensagens);
		return Problemas.requisicaoInvalida(detalhe, request.getRequestURI());
	}

	@ExceptionHandler(DataAccessException.class)
	ResponseEntity<ProblemDetail> conflito(DataAccessException ex, HttpServletRequest request) {
		var causa = ex.getMostSpecificCause().getMessage();
		if (causa == null || !causa.contains("UNIQUE constraint failed")) {
			throw ex;
		}
		var problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Já existe um registro com esses dados");
		problema.setInstance(URI.create(request.getRequestURI()));
		return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_PROBLEM_JSON)
			.body(problema);
	}

}
