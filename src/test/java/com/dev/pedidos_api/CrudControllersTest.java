package com.dev.pedidos_api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CrudControllersTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JdbcClient jdbcClient;

	@Test
	void crudDeClientes() throws Exception {
		var criado = mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"Elena Costa","email":"elena.costa@pedidosnet.com.br","vip":true}
			""")).andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.nome").value("Elena Costa")).andExpect(jsonPath("$.vip").value(true)).andReturn();

		var id = com.jayway.jsonpath.JsonPath.read(criado.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(get("/clientes/" + id)).andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("elena.costa@pedidosnet.com.br"));

		mockMvc.perform(get("/clientes")).andExpect(status().isOk())
			.andExpect(jsonPath("$[?(@.id == '" + id + "')].nome").value("Elena Costa"));

		mockMvc
			.perform(put("/clientes/" + id).contentType(MediaType.APPLICATION_JSON).content(
				"""
					{"nome":"Elena Costa Lima","email":"elena.lima@pedidosnet.com.br","vip":false,"corporativo":true,"bloqueado":true,"dataCadastro":"2026-02-02T00:00:00"}
					"""))
			.andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Elena Costa Lima"))
			.andExpect(jsonPath("$.bloqueado").value(true));

		mockMvc.perform(delete("/clientes/" + id)).andExpect(status().isNoContent());

		mockMvc.perform(get("/clientes/" + id)).andExpect(status().isNotFound())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.title").value("Not Found")).andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.detail").value("Cliente não encontrado"))
			.andExpect(jsonPath("$.instance").value("/clientes/" + id));
	}

	@Test
	void jsonInvalidoViraProblemDetail() throws Exception {
		mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("{"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.title").value("Bad Request"));
	}

	@Test
	void emailDuplicadoViraConflito() throws Exception {
		var corpo = """
			{"nome":"Elena Costa","email":"elena.duplicada@pedidosnet.com.br","vip":false,"corporativo":false,"bloqueado":false}
			""";
		mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
			.andExpect(status().isConflict())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.status").value(409)).andExpect(jsonPath("$.title").value("Conflict"))
			.andExpect(jsonPath("$.detail").value("Já existe um registro com esses dados"))
			.andExpect(jsonPath("$.instance").value("/clientes"));
	}

	@Test
	void crudDeProdutos() throws Exception {
		var criado = mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"Hub USB","categoria":"Periféricos","preco":89.90,"estoque":12}
			""")).andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.nome").value("Hub USB")).andReturn();

		var id = com.jayway.jsonpath.JsonPath.read(criado.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(get("/produtos/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.estoque").value(12));

		mockMvc.perform(get("/produtos")).andExpect(status().isOk())
			.andExpect(jsonPath("$[?(@.id == '" + id + "')].categoria").value("Periféricos"));

		mockMvc.perform(put("/produtos/" + id).contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"Hub USB-C","categoria":"Acessórios","preco":99.50,"estoque":7}
			""")).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Hub USB-C"))
			.andExpect(jsonPath("$.estoque").value(7));

		mockMvc.perform(delete("/produtos/" + id)).andExpect(status().isNoContent());

		mockMvc.perform(get("/produtos/" + id)).andExpect(status().isNotFound());

		mockMvc.perform(put("/produtos/" + id).contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"Inexistente","categoria":"Nenhuma","preco":1,"estoque":0}
			""")).andExpect(status().isNotFound());
	}

	@Test
	void listaPedidos() throws Exception {
		var pedidoId = UUID.randomUUID().toString();
		var itemId = UUID.randomUUID().toString();
		jdbcClient.sql("""
			INSERT INTO pedidos (id, cliente_id, status, total, data_criacao)
			VALUES (:id, :clienteId, :status, :total, :dataCriacao)
			""").param("id", pedidoId).param("clienteId", "97b36cfb-76f7-5c00-a772-af57c9288ab3")
			.param("status", "ABERTO").param("total", 120).param("dataCriacao", "2026-03-01T10:00:00").update();
		jdbcClient.sql("""
			INSERT INTO itens_pedido (id, pedido_id, produto_id, quantidade)
			VALUES (:id, :pedidoId, :produtoId, :quantidade)
			""").param("id", itemId).param("pedidoId", pedidoId)
			.param("produtoId", "cf6d3d8d-d3a0-554d-9336-2e968b4a5d85").param("quantidade", 2).update();

		mockMvc.perform(get("/pedidos")).andExpect(status().isOk())
			.andExpect(jsonPath("$[?(@.id == '" + pedidoId + "')].cliente").value("Ana Ferreira"))
			.andExpect(jsonPath("$[?(@.id == '" + pedidoId + "')].itens[0].produto").value("Teclado mecânico"));

		mockMvc.perform(get("/pedidos/" + pedidoId)).andExpect(status().isOk())
			.andExpect(jsonPath("$[0].cliente").value("Ana Ferreira"))
			.andExpect(jsonPath("$[0].itens[0].quantidade").value(2));

		mockMvc.perform(get("/pedidos/" + UUID.randomUUID())).andExpect(status().isOk())
			.andExpect(content().json("[]"));
	}

	@Test
	void criaPedidoComDescontos() throws Exception {
		var vipAbaixoDoLimite = criarPedido(
			"""
				{"clienteId":"97b36cfb-76f7-5c00-a772-af57c9288ab3","itens":[{"produtoId":"cf6d3d8d-d3a0-554d-9336-2e968b4a5d85","quantidade":2}]}
				""");
		assertEquals(0, new BigDecimal("629.82").compareTo(totalDoPedido(vipAbaixoDoLimite)));

		var comumAcimaDoLimite = criarPedido(
			"""
				{"clienteId":"1c5d07e5-e1f1-5218-b971-ed105a63c9bd","itens":[{"produtoId":"865d3129-5149-5ef4-ac8c-4ae39217e103","quantidade":2}]}
				""");
		assertEquals(0, new BigDecimal("3038.10").compareTo(totalDoPedido(comumAcimaDoLimite)));

		var vipAcimaDoLimite = criarPedido(
			"""
				{"clienteId":"97b36cfb-76f7-5c00-a772-af57c9288ab3","itens":[{"produtoId":"d5734acf-c4c7-53f0-884d-e0c64c4f1f32","quantidade":1},{"produtoId":"865d3129-5149-5ef4-ac8c-4ae39217e103","quantidade":1}]}
				""");
		assertEquals(0, new BigDecimal("2983.095").compareTo(totalDoPedido(vipAcimaDoLimite)));

		mockMvc.perform(get("/pedidos/" + vipAbaixoDoLimite)).andExpect(status().isOk())
			.andExpect(jsonPath("$[0].cliente").value("Ana Ferreira"))
			.andExpect(jsonPath("$[0].status").value("Pendente"))
			.andExpect(jsonPath("$[0].itens[0].produto").value("Teclado mecânico"))
			.andExpect(jsonPath("$[0].itens[0].quantidade").value(2));
	}

	@Test
	void recusaPedidoComClienteOuProdutoInexistente() throws Exception {
		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("""
			{"clienteId":"%s","itens":[{"produtoId":"cf6d3d8d-d3a0-554d-9336-2e968b4a5d85","quantidade":1}]}
			""".formatted(UUID.randomUUID()))).andExpect(status().isBadRequest())
			.andExpect(content().string("Cliente não encontrado."));

		var produtoId = UUID.randomUUID().toString();
		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("""
			{"clienteId":"97b36cfb-76f7-5c00-a772-af57c9288ab3","itens":[{"produtoId":"%s","quantidade":1}]}
			""".formatted(produtoId))).andExpect(status().isBadRequest())
			.andExpect(content().string("Produto " + produtoId + " não encontrado."));
	}

	private String criarPedido(String json) throws Exception {
		var resposta = mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk()).andReturn();
		var corpo = resposta.getResponse().getContentAsString().trim();
		if (corpo.startsWith("\"") && corpo.endsWith("\"")) {
			return corpo.substring(1, corpo.length() - 1);
		}
		return corpo;
	}

	private BigDecimal totalDoPedido(String id) {
		return jdbcClient.sql("SELECT total FROM pedidos WHERE id = :id").param("id", id).query(BigDecimal.class)
			.single();
	}
}
