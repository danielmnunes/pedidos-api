package com.dev.pedidos_api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	public List<Pedido> listarPorId(@PathVariable long id) {
		return pedidos.listarPorId(id);
	}

	@PostMapping
	@Transactional
	public ResponseEntity<?> criar(@RequestBody CriarPedidoRequest request) {
		var cliente = clienteRepository.buscarPorId(request.clienteId()).orElse(null);
		if (cliente == null) {
			return ResponseEntity.badRequest().body("Cliente não encontrado.");
		}

		if (cliente.bloqueado()) {
			return ResponseEntity.badRequest().body("Cliente bloqueado.");
		}

		var itens = new ArrayList<ItemNovo>();
		var total = BigDecimal.ZERO;
		for (var itemRequest : request.itens()) {
			var produto = produtoRepository.buscarPorId(itemRequest.produtoId()).orElse(null);
			if (produto == null) {
				return ResponseEntity.badRequest().body("Produto " + itemRequest.produtoId() + " não encontrado.");
			}

			itens.add(new ItemNovo(produto.id(), produto.nome(), itemRequest.quantidade()));
			total = total.add(produto.preco().multiply(BigDecimal.valueOf(itemRequest.quantidade())));
		}

		if (cliente.vip()) {
			total = total.subtract(total.multiply(new BigDecimal("0.10")));
		}
		if (total.compareTo(new BigDecimal("2000")) > 0) {
			total = total.subtract(total.multiply(new BigDecimal("0.05")));
		}

		var status = "Pendente";
		if (cliente.vip()) {
			status = "EmAnalise";
		}

		if (cliente.corporativo() && total.compareTo(new BigDecimal("5000")) > 0) {
			status = "AguardandoValidacao";
		}

		var dataCriacao = LocalDateTime.now().withNano(0).toString();
		var id = pedidos.inserir(cliente.id(), status, total, dataCriacao);
		var itensPedido = new ArrayList<ItemPedido>();
		for (var item : itens) {
			var itemId = pedidos.inserirItem(id, item.produtoId(), item.quantidade());
			itensPedido.add(new ItemPedido(itemId, item.produto(), item.quantidade()));
		}
		return ResponseEntity.ok(new Pedido(id, cliente.nome(), status, total, dataCriacao, List.copyOf(itensPedido)));
	}

	private record ItemNovo(long produtoId, String produto, int quantidade) {
	}

}
