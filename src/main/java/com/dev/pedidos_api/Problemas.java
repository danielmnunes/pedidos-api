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
		return resposta(HttpStatus.NOT_FOUND, detalhe, caminho);
	}

	static ResponseEntity<ProblemDetail> requisicaoInvalida(String detalhe, String caminho) {
		return resposta(HttpStatus.BAD_REQUEST, detalhe, caminho);
	}

	private static ResponseEntity<ProblemDetail> resposta(HttpStatus status, String detalhe, String caminho) {
		var problema = ProblemDetail.forStatusAndDetail(status, detalhe);
		problema.setInstance(URI.create(caminho));
		return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problema);
	}

}
