package com.dev.pedidos_api;

public record Cliente(long id, String nome, String email, boolean vip, boolean corporativo, boolean bloqueado,
	String dataCadastro) {
}
