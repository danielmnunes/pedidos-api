package com.dev.pedidos_api;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "Cadastro de produtos")
public class ProdutoController {

	private final ProdutoRepository produtos;

	public ProdutoController(ProdutoRepository produtos) {
		this.produtos = produtos;
	}

	@GetMapping
	public List<Produto> listar() {
		return produtos.listar();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable long id) {
		return produtos.buscarPorId(id).<ResponseEntity<?>>map(ResponseEntity::ok)
			.orElseGet(() -> Problemas.naoEncontrado("Produto não encontrado", "/produtos/" + id));
	}

	@PostMapping
	public ResponseEntity<Produto> inserir(@Valid @RequestBody ProdutoBody produto) {
		var novo = new Produto(0, produto.nome(), produto.categoria(), produto.preco(), produto.estoque());
		var id = produtos.inserir(novo);
		var criado = new Produto(id, novo.nome(), novo.categoria(), novo.preco(), novo.estoque());
		return ResponseEntity.created(URI.create("/produtos/" + id)).body(criado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> atualizar(@PathVariable long id, @Valid @RequestBody ProdutoBody produto) {
		var atualizado = new Produto(id, produto.nome(), produto.categoria(), produto.preco(), produto.estoque());
		if (produtos.atualizar(atualizado) == 0) {
			return Problemas.naoEncontrado("Produto não encontrado", "/produtos/" + id);
		}
		return ResponseEntity.ok(atualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> excluir(@PathVariable long id) {
		if (produtos.excluir(id) == 0) {
			return Problemas.naoEncontrado("Produto não encontrado", "/produtos/" + id);
		}
		return ResponseEntity.noContent().build();
	}

	private record ProdutoBody(String nome, String categoria, BigDecimal preco,
		@NotNull(message = "Informe o estoque") Integer estoque) {
	}

}
