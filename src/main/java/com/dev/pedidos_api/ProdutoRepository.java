package com.dev.pedidos_api;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProdutoRepository {

	private static final RowMapper<Produto> MAPEADOR = (rs, rowNum) -> new Produto(rs.getLong("id"),
		rs.getString("nome"), rs.getString("categoria"), rs.getBigDecimal("preco"), rs.getInt("estoque"));

	private final JdbcClient jdbcClient;

	public ProdutoRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public long inserir(Produto produto) {
		return jdbcClient.sql("""
			INSERT INTO produtos (nome, categoria, preco, estoque)
			VALUES (:nome, :categoria, :preco, :estoque)
			RETURNING id
			""").param("nome", produto.nome()).param("categoria", produto.categoria()).param("preco", produto.preco())
			.param("estoque", produto.estoque()).query(Long.class).single();
	}

	public Optional<Produto> buscarPorId(long id) {
		return jdbcClient.sql("""
			SELECT id, nome, categoria, preco, estoque
			FROM produtos
			WHERE id = :id
			""").param("id", id).query(MAPEADOR).optional();
	}

	public List<Produto> listar() {
		return jdbcClient.sql("""
			SELECT id, nome, categoria, preco, estoque
			FROM produtos
			ORDER BY nome
			""").query(MAPEADOR).list();
	}

	public int atualizar(Produto produto) {
		return jdbcClient.sql("""
			UPDATE produtos
			SET nome = :nome,
			    categoria = :categoria,
			    preco = :preco,
			    estoque = :estoque
			WHERE id = :id
			""").param("id", produto.id()).param("nome", produto.nome()).param("categoria", produto.categoria())
			.param("preco", produto.preco()).param("estoque", produto.estoque()).update();
	}

	public int excluir(long id) {
		return jdbcClient.sql("DELETE FROM produtos WHERE id = :id").param("id", id).update();
	}

}
