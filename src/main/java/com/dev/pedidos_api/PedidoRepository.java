package com.dev.pedidos_api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class PedidoRepository {

	private static final String CONSULTA = """
		SELECT p.id,
		       c.nome AS cliente,
		       p.status,
		       p.total,
		       p.data_criacao,
		       i.id AS item_id,
		       pr.nome AS produto,
		       i.quantidade
		FROM pedidos p
		JOIN clientes c ON c.id = p.cliente_id
		LEFT JOIN itens_pedido i ON i.pedido_id = p.id
		LEFT JOIN produtos pr ON pr.id = i.produto_id
		""";

	private static final RowMapper<Linha> LINHA = (rs, rowNum) -> new Linha(rs.getString("id"), rs.getString("cliente"),
		rs.getString("status"), rs.getBigDecimal("total"), rs.getString("data_criacao"), rs.getString("item_id"),
		rs.getString("produto"), rs.getInt("quantidade"));

	private final JdbcClient jdbcClient;

	public PedidoRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public List<Pedido> listar() {
		return agrupar(jdbcClient.sql(CONSULTA + """
			ORDER BY p.data_criacao, p.id, i.id
			""").query(LINHA).list());
	}

	public List<Pedido> listarPorId(String pedidoId) {
		return agrupar(jdbcClient.sql(CONSULTA + """
			WHERE p.id = :pedidoId
			ORDER BY p.data_criacao, p.id, i.id
			""").param("pedidoId", pedidoId).query(LINHA).list());
	}

	public void inserir(String id, String clienteId, String status, BigDecimal total, String dataCriacao) {
		jdbcClient.sql("""
			INSERT INTO pedidos (id, cliente_id, status, total, data_criacao)
			VALUES (:id, :clienteId, :status, :total, :dataCriacao)
			""").param("id", id).param("clienteId", clienteId).param("status", status).param("total", total)
			.param("dataCriacao", dataCriacao).update();
	}

	public void inserirItem(String id, String pedidoId, String produtoId, int quantidade) {
		jdbcClient.sql("""
			INSERT INTO itens_pedido (id, pedido_id, produto_id, quantidade)
			VALUES (:id, :pedidoId, :produtoId, :quantidade)
			""").param("id", id).param("pedidoId", pedidoId).param("produtoId", produtoId)
			.param("quantidade", quantidade).update();
	}

	private static List<Pedido> agrupar(List<Linha> linhas) {
		var pedidos = new LinkedHashMap<String, Montagem>();
		for (var linha : linhas) {
			var montagem = pedidos.computeIfAbsent(linha.id(), id -> new Montagem(linha));
			if (linha.itemId() != null) {
				montagem.itens.add(new ItemPedido(linha.itemId(), linha.produto(), linha.quantidade()));
			}
		}
		return pedidos.values().stream().map(montagem -> new Pedido(montagem.id, montagem.cliente, montagem.status,
			montagem.total, montagem.dataCriacao, List.copyOf(montagem.itens))).toList();
	}

	private record Linha(String id, String cliente, String status, BigDecimal total, String dataCriacao, String itemId,
		String produto, int quantidade) {
	}

	private static final class Montagem {
		private final String id;
		private final String cliente;
		private final String status;
		private final BigDecimal total;
		private final String dataCriacao;
		private final List<ItemPedido> itens = new ArrayList<>();

		private Montagem(Linha linha) {
			this.id = linha.id();
			this.cliente = linha.cliente();
			this.status = linha.status();
			this.total = linha.total();
			this.dataCriacao = linha.dataCriacao();
		}
	}

}
