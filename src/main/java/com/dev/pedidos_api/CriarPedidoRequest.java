package com.dev.pedidos_api;

import java.util.List;

public record CriarPedidoRequest(String clienteId, List<CriarItemPedidoRequest> itens) {
}
