package com.dev.pedidos_api;

import java.math.BigDecimal;
import java.util.List;

public record Pedido(String id, String cliente, String status, BigDecimal total, String dataCriacao,
	List<ItemPedido> itens) {
}
