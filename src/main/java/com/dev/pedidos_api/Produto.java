package com.dev.pedidos_api;

import java.math.BigDecimal;

public record Produto(long id, String nome, String categoria, BigDecimal preco, int estoque) {
}
