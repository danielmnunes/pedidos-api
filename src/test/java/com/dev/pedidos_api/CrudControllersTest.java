package com.dev.pedidos_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Cadastro HTTP")
class CrudControllersTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("criação de cliente ignora o id do corpo")
	void criarClienteIgnoraIdInformado() throws Exception {
		var email = "novo." + UUID.randomUUID() + "@exemplo.com";
		var resultado = mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("""
			{"id":1,"nome":"Novo Cliente","email":"%s","vip":true}
			""".formatted(email))).andExpect(status().isCreated()).andReturn();

		var id = idDe(resultado);
		assertNotEquals(1L, id);
		assertTrue(id > 4);
		assertEquals("/clientes/" + id, resultado.getResponse().getHeader("Location"));
		mockMvc.perform(get("/clientes/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value(email));
		mockMvc.perform(get("/clientes/1")).andExpect(status().isOk())
			.andExpect(jsonPath("$.nome").value("Ana Ferreira"))
			.andExpect(jsonPath("$.email").value("ana.ferreira@pedidosnet.com.br"));
	}

	@Test
	@DisplayName("criação de produto ignora o id do corpo")
	void criarProdutoIgnoraIdInformado() throws Exception {
		var resultado = mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("""
			{"id":1,"nome":"Hub USB","categoria":"Periféricos","preco":89.90,"estoque":4}
			""")).andExpect(status().isCreated()).andExpect(jsonPath("$.nome").value("Hub USB")).andReturn();

		var id = idDe(resultado);
		assertNotEquals(1L, id);
		assertTrue(id > 7);
		assertEquals("/produtos/" + id, resultado.getResponse().getHeader("Location"));
		mockMvc.perform(get("/produtos/1")).andExpect(status().isOk())
			.andExpect(jsonPath("$.nome").value("Teclado mecânico")).andExpect(jsonPath("$.estoque").value(42));
	}

	@Test
	@DisplayName("não reutiliza o id de um cliente excluído")
	void naoReutilizaIdDeClienteExcluido() throws Exception {
		var primeiro = criarCliente();
		mockMvc.perform(delete("/clientes/" + primeiro)).andExpect(status().isNoContent());

		var segundo = criarCliente();

		assertTrue(segundo > primeiro);
		mockMvc.perform(get("/clientes/" + primeiro)).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("não reutiliza o id de um produto excluído")
	void naoReutilizaIdDeProdutoExcluido() throws Exception {
		var primeiro = criarProduto("Primeiro");
		mockMvc.perform(delete("/produtos/" + primeiro)).andExpect(status().isNoContent());

		var segundo = criarProduto("Segundo");

		assertTrue(segundo > primeiro);
		mockMvc.perform(get("/produtos/" + primeiro)).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("produto sem estoque devolve 400")
	void produtoSemEstoque() throws Exception {
		mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"Sem estoque","categoria":"Teste","preco":10.00}
			""")).andExpect(status().isBadRequest()).andExpect(problema("Informe o estoque", "/produtos"));
	}

	@Test
	@DisplayName("atualização sem estoque devolve 400 e mantém o valor")
	void atualizarProdutoSemEstoque() throws Exception {
		mockMvc.perform(put("/produtos/1").contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"Teclado mecânico","categoria":"Periféricos","preco":349.90}
			""")).andExpect(status().isBadRequest()).andExpect(problema("Informe o estoque", "/produtos/1"));

		mockMvc.perform(get("/produtos/1")).andExpect(status().isOk()).andExpect(jsonPath("$.estoque").value(42));
	}

	@Test
	@DisplayName("estoque zero informado é aceito")
	void produtoComEstoqueZero() throws Exception {
		mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"Esgotado","categoria":"Teste","preco":10.00,"estoque":0}
			""")).andExpect(status().isCreated()).andExpect(jsonPath("$.estoque").value(0));
	}

	@Test
	@DisplayName("pedido sem clienteId devolve 400")
	void pedidoSemCliente() throws Exception {
		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("""
			{"itens":[{"produtoId":1,"quantidade":1}]}
			""")).andExpect(status().isBadRequest()).andExpect(problema("Informe o cliente", "/pedidos"));
	}

	@Test
	@DisplayName("clienteId nulo devolve 400")
	void pedidoComClienteNulo() throws Exception {
		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("""
			{"clienteId":null,"itens":[{"produtoId":1,"quantidade":1}]}
			""")).andExpect(status().isBadRequest()).andExpect(problema("Informe o cliente", "/pedidos"));
	}

	@Test
	@DisplayName("pedido sem produtoId devolve 400")
	void pedidoSemProduto() throws Exception {
		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("""
			{"clienteId":1,"itens":[{"quantidade":1}]}
			""")).andExpect(status().isBadRequest()).andExpect(problema("Informe o produto", "/pedidos"));
	}

	@Test
	@DisplayName("pedido sem quantidade devolve 400")
	void pedidoSemQuantidade() throws Exception {
		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("""
			{"clienteId":1,"itens":[{"produtoId":1}]}
			""")).andExpect(status().isBadRequest()).andExpect(problema("Informe a quantidade", "/pedidos"));
	}

	@Test
	@DisplayName("pedido sem itens devolve 400")
	void pedidoSemItens() throws Exception {
		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("""
			{"clienteId":1}
			""")).andExpect(status().isBadRequest()).andExpect(problema("Informe os itens do pedido", "/pedidos"));
	}

	@Test
	@DisplayName("clienteId zero continua sendo cliente inexistente")
	void pedidoComClienteZero() throws Exception {
		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("""
			{"clienteId":0,"itens":[{"produtoId":1,"quantidade":1}]}
			""")).andExpect(status().isBadRequest()).andExpect(content().string("Cliente não encontrado."));
	}

	@Test
	@DisplayName("e-mail duplicado continua em conflito")
	void emailDuplicado() throws Exception {
		var corpo = """
			{"nome":"Elena","email":"elena.duplicada.%s@exemplo.com"}
			""".formatted(UUID.randomUUID());
		mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content(corpo))
			.andExpect(status().isConflict())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
			.andExpect(jsonPath("$.status").value(409))
			.andExpect(jsonPath("$.detail").value("Já existe um registro com esses dados"))
			.andExpect(jsonPath("$.instance").value("/clientes"));
	}

	private long criarCliente() throws Exception {
		var resultado = mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"Temporário","email":"temp.%s@exemplo.com"}
			""".formatted(UUID.randomUUID()))).andExpect(status().isCreated()).andReturn();
		return idDe(resultado);
	}

	private long criarProduto(String nome) throws Exception {
		var resultado = mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("""
			{"nome":"%s","categoria":"Teste","preco":10.00,"estoque":1}
			""".formatted(nome))).andExpect(status().isCreated()).andReturn();
		return idDe(resultado);
	}

	private static long idDe(MvcResult resultado) throws Exception {
		Number id = JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");
		return id.longValue();
	}

	private static org.springframework.test.web.servlet.ResultMatcher problema(String detalhe, String caminho) {
		return resultado -> {
			status().isBadRequest().match(resultado);
			content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON).match(resultado);
			jsonPath("$.status").value(400).match(resultado);
			jsonPath("$.title").value("Bad Request").match(resultado);
			jsonPath("$.detail").value(detalhe).match(resultado);
			jsonPath("$.instance").value(caminho).match(resultado);
		};
	}

}
