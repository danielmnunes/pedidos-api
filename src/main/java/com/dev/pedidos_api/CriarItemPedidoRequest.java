package com.dev.pedidos_api;

import jakarta.validation.constraints.NotNull;

public record CriarItemPedidoRequest(@NotNull(message = "Informe o produto") Long produtoId,
	@NotNull(message = "Informe a quantidade") Integer quantidade) {
}
