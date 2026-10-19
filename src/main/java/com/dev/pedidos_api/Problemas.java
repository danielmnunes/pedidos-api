package com.dev.pedidos_api;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

final class Problemas {

	private Problemas() {
	}

	static ResponseEntity<ProblemDetail> naoEncontrado(String detalhe, String caminho) {
		var problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detalhe);
		problema.setInstance(URI.create(caminho));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_PROBLEM_JSON)
			.body(problema);
	}

}
