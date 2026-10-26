package com.dev.pedidos_api;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CriarPedidoRequest(@NotNull(message = "Informe o cliente") Long clienteId,
	@NotNull(message = "Informe os itens do pedido") @Valid List<CriarItemPedidoRequest> itens) {
}
