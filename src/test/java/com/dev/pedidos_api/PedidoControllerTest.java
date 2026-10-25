package com.dev.pedidos_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}
