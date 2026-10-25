package com.dev.pedidos_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoController")
class PedidoControllerTest {

	@InjectMocks
	private PedidoController controller;

	@Mock
	private PedidoRepository pedidos;

	@Mock
	private ClienteRepository clienteRepository;

	@Mock
	private ProdutoRepository produtoRepository;

	@Test
	@DisplayName("devolve os pedidos do repositório")
	void listar() {
		var pedido = new Pedido("1", "Ana Ferreira", "Pendente", new BigDecimal("10.00"), "2026-10-25T00:00:00",
			List.of());
		when(pedidos.listar()).thenReturn(List.of(pedido));

		var resultado = controller.listar();

		assertEquals(List.of(pedido), resultado);
		verify(pedidos).listar();
	}

	@Test
	@DisplayName("devolve os pedidos do id informado")
	void listarPorId() {
		var pedido = new Pedido("1", "Ana Ferreira", "Pendente", new BigDecimal("10.00"), "2026-10-25T00:00:00",
			List.of());
		when(pedidos.listarPorId("1")).thenReturn(List.of(pedido));

		var resultado = controller.listarPorId("1");

		assertEquals(List.of(pedido), resultado);
		verify(pedidos).listarPorId("1");
	}

	@Test
	@DisplayName("cria pedido corporativo não VIP com dois monitores e três headsets do print")
	void criarPedidoCorporativoNaoVip() {
		var cliente = new Cliente("cliente-print", "Cliente corporativo", "print@example.com", false, true, false,
			"2026-10-25T00:00:00");
		var monitor = new Produto("monitor-print", "Monitor", "Monitores", new BigDecimal("1899.00"), 2);
		var headset = new Produto("headset-print", "Headset", "Áudio", new BigDecimal("429.90"), 3);
		when(clienteRepository.buscarPorId(cliente.id())).thenReturn(Optional.of(cliente));
		when(produtoRepository.buscarPorId(monitor.id())).thenReturn(Optional.of(monitor));
		when(produtoRepository.buscarPorId(headset.id())).thenReturn(Optional.of(headset));
		var request = new CriarPedidoRequest(cliente.id(),
			List.of(new CriarItemPedidoRequest(monitor.id(), 2), new CriarItemPedidoRequest(headset.id(), 3)));

		var resposta = controller.criar(request);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		var pedido = assertInstanceOf(Pedido.class, resposta.getBody());
		assertNotNull(pedido.id());
		assertNotNull(LocalDateTime.parse(pedido.dataCriacao()));
		assertEquals(cliente.nome(), pedido.cliente());
		assertEquals("Pendente", pedido.status());
		// Bruto de 5.087,70 com desconto de 5%: 4.833,315, abaixo do limite corporativo
		// de 5.000.
		assertEquals(0, new BigDecimal("4833.315").compareTo(pedido.total()));
		assertEquals(2, pedido.itens().size());
		var itemMonitor = pedido.itens().get(0);
		var itemHeadset = pedido.itens().get(1);
		assertNotNull(itemMonitor.id());
		assertEquals(monitor.nome(), itemMonitor.produto());
		assertEquals(2, itemMonitor.quantidade());
		assertNotNull(itemHeadset.id());
		assertEquals(headset.nome(), itemHeadset.produto());
		assertEquals(3, itemHeadset.quantidade());
		verify(pedidos).inserir(pedido.id(), cliente.id(), pedido.status(), pedido.total(), pedido.dataCriacao());
		verify(pedidos).inserirItem(itemMonitor.id(), pedido.id(), monitor.id(), 2);
		verify(pedidos).inserirItem(itemHeadset.id(), pedido.id(), headset.id(), 3);
		verifyNoMoreInteractions(pedidos);
	}

}
