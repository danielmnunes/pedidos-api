package com.dev.pedidos_api;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepository {

	private static final RowMapper<Cliente> MAPEADOR = (rs, rowNum) -> new Cliente(rs.getLong("id"),
		rs.getString("nome"), rs.getString("email"), rs.getInt("vip") != 0, rs.getInt("corporativo") != 0,
		rs.getInt("bloqueado") != 0, rs.getString("data_cadastro"));

	private final JdbcClient jdbcClient;

	public ClienteRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public long inserir(Cliente cliente) {
		return jdbcClient.sql("""
			INSERT INTO clientes (nome, email, vip, corporativo, bloqueado, data_cadastro)
			VALUES (:nome, :email, :vip, :corporativo, :bloqueado, :dataCadastro)
			RETURNING id
			""").param("nome", cliente.nome()).param("email", cliente.email()).param("vip", cliente.vip() ? 1 : 0)
			.param("corporativo", cliente.corporativo() ? 1 : 0).param("bloqueado", cliente.bloqueado() ? 1 : 0)
			.param("dataCadastro", cliente.dataCadastro()).query(Long.class).single();
	}

	public Optional<Cliente> buscarPorId(long id) {
		return jdbcClient.sql("""
			SELECT id, nome, email, vip, corporativo, bloqueado, data_cadastro
			FROM clientes
			WHERE id = :id
			""").param("id", id).query(MAPEADOR).optional();
	}

	public List<Cliente> listar() {
		return jdbcClient.sql("""
			SELECT id, nome, email, vip, corporativo, bloqueado, data_cadastro
			FROM clientes
			ORDER BY nome
			""").query(MAPEADOR).list();
	}

	public int atualizar(Cliente cliente) {
		return jdbcClient.sql("""
			UPDATE clientes
			SET nome = :nome,
			    email = :email,
			    vip = :vip,
			    corporativo = :corporativo,
			    bloqueado = :bloqueado,
			    data_cadastro = :dataCadastro
			WHERE id = :id
			""").param("id", cliente.id()).param("nome", cliente.nome()).param("email", cliente.email())
			.param("vip", cliente.vip() ? 1 : 0).param("corporativo", cliente.corporativo() ? 1 : 0)
			.param("bloqueado", cliente.bloqueado() ? 1 : 0).param("dataCadastro", cliente.dataCadastro()).update();
	}

	public int excluir(long id) {
		return jdbcClient.sql("DELETE FROM clientes WHERE id = :id").param("id", id).update();
	}

}
