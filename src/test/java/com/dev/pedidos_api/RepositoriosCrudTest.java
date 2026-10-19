package com.dev.pedidos_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RepositoriosCrudTest {

	@Autowired
	private ClienteRepository clientes;

	@Autowired
	private ProdutoRepository produtos;

	@Test
	void crudDeCliente() {
		var id = UUID.randomUUID().toString();
		var cliente = new Cliente(id, "Elena Costa", "elena.costa@pedidosnet.com.br", true, false, false, "2026-02-01T00:00:00");

		clientes.inserir(cliente);

		assertEquals(cliente, clientes.buscarPorId(id).orElseThrow());
		assertTrue(clientes.listar().stream().anyMatch(item -> item.id().equals(id)));

		var atualizado = new Cliente(id, "Elena Costa Lima", "elena.lima@pedidosnet.com.br", false, true, true, "2026-02-02T00:00:00");
		assertEquals(1, clientes.atualizar(atualizado));
		assertEquals(atualizado, clientes.buscarPorId(id).orElseThrow());

		assertEquals(1, clientes.excluir(id));
		assertTrue(clientes.buscarPorId(id).isEmpty());
	}

	@Test
	void crudDeProduto() {
		var id = UUID.randomUUID().toString();
		var produto = new Produto(id, "Hub USB", "Periféricos", new BigDecimal("89.90"), 12);

		produtos.inserir(produto);

		var salvo = produtos.buscarPorId(id).orElseThrow();
		assertEquals(produto.id(), salvo.id());
		assertEquals(produto.nome(), salvo.nome());
		assertEquals(produto.categoria(), salvo.categoria());
		assertEquals(0, produto.preco().compareTo(salvo.preco()));
		assertEquals(produto.estoque(), salvo.estoque());
		assertTrue(produtos.listar().stream().anyMatch(item -> item.id().equals(id)));

		var atualizado = new Produto(id, "Hub USB-C", "Acessórios", new BigDecimal("99.50"), 7);
		assertEquals(1, produtos.atualizar(atualizado));
		var depois = produtos.buscarPorId(id).orElseThrow();
		assertEquals(atualizado.nome(), depois.nome());
		assertEquals(atualizado.categoria(), depois.categoria());
		assertEquals(0, atualizado.preco().compareTo(depois.preco()));
		assertEquals(atualizado.estoque(), depois.estoque());

		assertEquals(1, produtos.excluir(id));
		assertTrue(produtos.buscarPorId(id).isEmpty());
	}

}
