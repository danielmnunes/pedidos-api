package com.dev.pedidos_api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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
	public ResponseEntity<?> buscar(@PathVariable String id) {
		return produtos.buscarPorId(id)
				.<ResponseEntity<?>>map(ResponseEntity::ok)
				.orElseGet(() -> Problemas.naoEncontrado("Produto não encontrado", "/produtos/" + id));
	}

	@PostMapping
	public ResponseEntity<Produto> inserir(@RequestBody Produto produto) {
		var id = produto.id() != null && !produto.id().isBlank()
				? produto.id()
				: UUID.randomUUID().toString();
		var novo = new Produto(id, produto.nome(), produto.categoria(), produto.preco(), produto.estoque());
		produtos.inserir(novo);
		return ResponseEntity.created(URI.create("/produtos/" + id)).body(novo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> atualizar(@PathVariable String id, @RequestBody Produto produto) {
		var atualizado = new Produto(id, produto.nome(), produto.categoria(), produto.preco(), produto.estoque());
		if (produtos.atualizar(atualizado) == 0) {
			return Problemas.naoEncontrado("Produto não encontrado", "/produtos/" + id);
		}
		return ResponseEntity.ok(atualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> excluir(@PathVariable String id) {
		if (produtos.excluir(id) == 0) {
			return Problemas.naoEncontrado("Produto não encontrado", "/produtos/" + id);
		}
		return ResponseEntity.noContent().build();
	}

}
