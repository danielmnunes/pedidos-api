package com.dev.pedidos_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RepositoriosCrudTest {

	@Autowired
	private ClienteRepository clientes;

	@Autowired
	private ProdutoRepository produtos;

	@Autowired
	private PedidoRepository pedidos;

	@Autowired
	private JdbcClient jdbcClient;

	@Test
	void crudDeCliente() {
		var id = UUID.randomUUID().toString();
		var cliente = new Cliente(id, "Elena Costa", "elena.costa@pedidosnet.com.br", true, false, false,
			"2026-02-01T00:00:00");

		clientes.inserir(cliente);

		assertEquals(cliente, clientes.buscarPorId(id).orElseThrow());
		assertTrue(clientes.listar().stream().anyMatch(item -> item.id().equals(id)));

		var atualizado = new Cliente(id, "Elena Costa Lima", "elena.lima@pedidosnet.com.br", false, true, true,
			"2026-02-02T00:00:00");
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

	@Test
	void listaPedidosEItensPorIdDoPedido() {
		var pedidoId = UUID.randomUUID().toString();
		var outroPedidoId = UUID.randomUUID().toString();
		var clienteId = "97b36cfb-76f7-5c00-a772-af57c9288ab3";
		var tecladoId = "cf6d3d8d-d3a0-554d-9336-2e968b4a5d85";
		var mouseId = "b1fa0332-9ac6-5e0f-a270-dbf1c3d040f9";

		inserirPedido(pedidoId, clienteId, "ABERTO", "120.00", "2026-03-01T10:00:00");
		inserirPedido(outroPedidoId, clienteId, "FECHADO", "40.00", "2026-03-02T10:00:00");
		inserirItem(UUID.randomUUID().toString(), pedidoId, tecladoId, 2);
		inserirItem(UUID.randomUUID().toString(), pedidoId, mouseId, 1);
		inserirItem(UUID.randomUUID().toString(), outroPedidoId, tecladoId, 4);

		var listado = pedidos.listar().stream().filter(pedido -> pedido.id().equals(pedidoId)).findFirst()
			.orElseThrow();
		assertEquals("Ana Ferreira", listado.cliente());
		var nomes = listado.itens().stream().map(ItemPedido::produto).toList();
		assertEquals(2, nomes.size());
		assertTrue(nomes.contains("Teclado mecânico"));
		assertTrue(nomes.contains("Mouse sem fio"));

		var porId = pedidos.listarPorId(pedidoId);
		assertEquals(1, porId.size());
		assertEquals(2, porId.getFirst().itens().size());
		assertEquals(1, pedidos.listarPorId(outroPedidoId).getFirst().itens().size());
	}

	private void inserirPedido(String id, String clienteId, String status, String total, String dataCriacao) {
		jdbcClient.sql("""
			INSERT INTO pedidos (id, cliente_id, status, total, data_criacao)
			VALUES (:id, :clienteId, :status, :total, :dataCriacao)
			""").param("id", id).param("clienteId", clienteId).param("status", status)
			.param("total", new BigDecimal(total)).param("dataCriacao", dataCriacao).update();
	}

	private void inserirItem(String id, String pedidoId, String produtoId, int quantidade) {
		jdbcClient.sql("""
			INSERT INTO itens_pedido (id, pedido_id, produto_id, quantidade)
			VALUES (:id, :pedidoId, :produtoId, :quantidade)
			""").param("id", id).param("pedidoId", pedidoId).param("produtoId", produtoId)
			.param("quantidade", quantidade).update();
	}
}
