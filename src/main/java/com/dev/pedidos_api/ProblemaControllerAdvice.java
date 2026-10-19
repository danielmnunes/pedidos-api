package com.dev.pedidos_api;

import java.net.URI;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
class ProblemaControllerAdvice {

	@ExceptionHandler(DataAccessException.class)
	ResponseEntity<ProblemDetail> conflito(DataAccessException ex, HttpServletRequest request) {
		var causa = ex.getMostSpecificCause().getMessage();
		if (causa == null || !causa.contains("UNIQUE constraint failed")) {
			throw ex;
		}
		var problema = ProblemDetail.forStatusAndDetail(
				HttpStatus.CONFLICT,
				"Já existe um registro com esses dados");
		problema.setInstance(URI.create(request.getRequestURI()));
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.body(problema);
	}

}
