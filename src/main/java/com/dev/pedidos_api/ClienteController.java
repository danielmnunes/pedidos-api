package com.dev.pedidos_api;

import java.net.URI;
import java.time.LocalDateTime;
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

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Cadastro de clientes")
public class ClienteController {

	private final ClienteRepository clientes;

	public ClienteController(ClienteRepository clientes) {
		this.clientes = clientes;
	}

	@GetMapping
	public List<Cliente> listar() {
		return clientes.listar();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable long id) {
		return clientes.buscarPorId(id).<ResponseEntity<?>>map(ResponseEntity::ok)
			.orElseGet(() -> Problemas.naoEncontrado("Cliente não encontrado", "/clientes/" + id));
	}

	@PostMapping
	public ResponseEntity<Cliente> inserir(@RequestBody ClienteBody cliente) {
		var dataCadastro = cliente.dataCadastro() != null && !cliente.dataCadastro().isBlank()
			? cliente.dataCadastro()
			: LocalDateTime.now().withNano(0).toString();
		var novo = new Cliente(cliente.id() == null ? 0 : cliente.id(), cliente.nome(), cliente.email(),
			Boolean.TRUE.equals(cliente.vip()), Boolean.TRUE.equals(cliente.corporativo()),
			Boolean.TRUE.equals(cliente.bloqueado()), dataCadastro);
		var id = clientes.inserir(novo);
		var criado = new Cliente(id, novo.nome(), novo.email(), novo.vip(), novo.corporativo(), novo.bloqueado(),
			novo.dataCadastro());
		return ResponseEntity.created(URI.create("/clientes/" + id)).body(criado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> atualizar(@PathVariable long id, @RequestBody ClienteBody cliente) {
		var atualizado = new Cliente(id, cliente.nome(), cliente.email(), Boolean.TRUE.equals(cliente.vip()),
			Boolean.TRUE.equals(cliente.corporativo()), Boolean.TRUE.equals(cliente.bloqueado()),
			cliente.dataCadastro());
		if (clientes.atualizar(atualizado) == 0) {
			return Problemas.naoEncontrado("Cliente não encontrado", "/clientes/" + id);
		}
		return ResponseEntity.ok(atualizado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> excluir(@PathVariable long id) {
		if (clientes.excluir(id) == 0) {
			return Problemas.naoEncontrado("Cliente não encontrado", "/clientes/" + id);
		}
		return ResponseEntity.noContent().build();
	}

	private record ClienteBody(Long id, String nome, String email, Boolean vip, Boolean corporativo, Boolean bloqueado,
		String dataCadastro) {
	}

}
