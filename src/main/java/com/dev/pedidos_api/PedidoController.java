package com.dev.pedidos_api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Criação e consulta de pedidos")
public class PedidoController {

	private final PedidoRepository pedidos;
	private final ClienteRepository clienteRepository;
	private final ProdutoRepository produtoRepository;

	public PedidoController(PedidoRepository pedidos, ClienteRepository clienteRepository,
		ProdutoRepository produtoRepository) {
		this.pedidos = pedidos;
		this.clienteRepository = clienteRepository;
		this.produtoRepository = produtoRepository;
	}

	@GetMapping
	public List<Pedido> listar() {
		return pedidos.listar();
	}

	@GetMapping("/{id}")
	public List<Pedido> listarPorId(@PathVariable String id) {
		return pedidos.listarPorId(id);
	}

	@PostMapping
	@Transactional
	public ResponseEntity<String> criar(@RequestBody CriarPedidoRequest request) {
		var cliente = clienteRepository.buscarPorId(request.clienteId()).orElse(null);
		if (cliente == null) {
			return ResponseEntity.badRequest().body("Cliente não encontrado.");
		}

		var itens = new ArrayList<ItemNovo>();
		var total = BigDecimal.ZERO;
		for (var itemRequest : request.itens()) {
			var produto = produtoRepository.buscarPorId(itemRequest.produtoId()).orElse(null);
			if (produto == null) {
				return ResponseEntity.badRequest().body("Produto " + itemRequest.produtoId() + " não encontrado.");
			}

			itens.add(new ItemNovo(UUID.randomUUID().toString(), produto.id(), itemRequest.quantidade()));
			total = total.add(produto.preco().multiply(BigDecimal.valueOf(itemRequest.quantidade())));
		}

		if (cliente.vip()) {
			total = total.subtract(total.multiply(new BigDecimal("0.10")));
		}
		if (total.compareTo(new BigDecimal("2000")) > 0) {
			total = total.subtract(total.multiply(new BigDecimal("0.05")));
		}

		var id = UUID.randomUUID().toString();
		pedidos.inserir(id, cliente.id(), "Pendente", total, LocalDateTime.now().withNano(0).toString());
		for (var item : itens) {
			pedidos.inserirItem(item.id(), id, item.produtoId(), item.quantidade());
		}
		return ResponseEntity.ok(id);
	}

	private record ItemNovo(String id, String produtoId, int quantidade) {
	}

}
