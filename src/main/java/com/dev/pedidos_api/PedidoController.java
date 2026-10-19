package com.dev.pedidos_api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Consulta de pedidos")
public class PedidoController {

	private final PedidoRepository pedidos;

	public PedidoController(PedidoRepository pedidos) {
		this.pedidos = pedidos;
	}

	@GetMapping
	public List<Pedido> listar() {
		return pedidos.listar();
	}

	@GetMapping("/{id}")
	public List<Pedido> listarPorId(@PathVariable String id) {
		return pedidos.listarPorId(id);
	}

}
