package com.dev.pedidos_api;

import java.util.List;

public record CriarPedidoRequest(long clienteId, List<CriarItemPedidoRequest> itens) {
}
