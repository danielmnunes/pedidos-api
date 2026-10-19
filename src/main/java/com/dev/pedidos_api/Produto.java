package com.dev.pedidos_api;

import java.math.BigDecimal;

public record Produto(
		String id,
		String nome,
		String categoria,
		BigDecimal preco,
		int estoque) {
}
